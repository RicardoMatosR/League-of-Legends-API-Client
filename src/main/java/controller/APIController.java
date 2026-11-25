package controller;

import com.google.gson.Gson;
import model.Campeon;
import model.Invocador;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class APIController {

    // Rutas de Riot: "europe" para cuentas y partidas, "euw1" para liga y maestría
    private static final String REGIONAL = "https://europe.api.riotgames.com";
    private static final String PLATAFORMA = "https://euw1.api.riotgames.com";
    private static final String DATA_DRAGON = "https://ddragon.leagueoflegends.com";

    // Un solo HttpClient para toda la app: se reutiliza y mantiene las conexiones abiertas
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Gson gson = new Gson();
    private final String apiKey = buscarApiKey();

    // Cache de Data Dragon: se descarga una vez y no en cada campeón
    private String versionDataDragon;
    private Map<Long, String> nombresCampeones;

    public Invocador obtenerPuuidYDevolverInvocador(String gameName, String tagLine) {
        String url = REGIONAL + "/riot/account/v1/accounts/by-riot-id/"
                + codificar(gameName) + "/" + codificar(tagLine);
        String body = peticionRiot(url);
        if (body == null) {
            return null;
        }
        return gson.fromJson(body, Invocador.class);
    }

    public void conseguirRango(String puuid) {
        String body = peticionRiot(PLATAFORMA + "/lol/league/v4/entries/by-puuid/" + puuid);
        if (body == null) {
            return;
        }
        JSONArray ligas = new JSONArray(body);
        if (ligas.isEmpty()) {
            System.out.println("Sin clasificatorias jugadas esta temporada.");
            return;
        }
        for (int i = 0; i < ligas.length(); i++) {
            Invocador invocador = gson.fromJson(ligas.getJSONObject(i).toString(), Invocador.class);
            System.out.println(invocador.getQueueType());
            System.out.println("DIVISION = " + invocador.getTier());
            System.out.println("RANK = " + invocador.getRank());
            System.out.println("PUNTOS = " + invocador.getLeaguePoints());
            System.out.println("WINS = " + invocador.getWins());
            System.out.println("LOSSES = " + invocador.getLosses());
            long partidas = invocador.getWins() + invocador.getLosses();
            if (partidas > 0) {
                invocador.setWr((double) invocador.getWins() / partidas * 100);
                System.out.printf("WINRATE = %.2f%%%n", invocador.getWr());
            }
            System.out.println();
        }
    }

    public void imprimir3Mains(String puuid) {
        String body = peticionRiot(PLATAFORMA + "/lol/champion-mastery/v4/champion-masteries/by-puuid/" + puuid + "/top");
        if (body == null) {
            return;
        }
        JSONArray mains = new JSONArray(body);
        System.out.println("TOP 3 CAMPEONES CON MAS MAESTRIA");
        for (int i = 0; i < mains.length(); i++) {
            Campeon campeon = gson.fromJson(mains.getJSONObject(i).toString(), Campeon.class);
            // Riot devuelve el id del campeón; el nombre lo sacamos de Data Dragon
            System.out.println(obtenerNombreCampeon(campeon.getChampionId())
                    + " - " + campeon.getChampionPoints() + " puntos de maestria");
        }
    }

    public void verUltimaPartida(String puuid) {
        String idPartida = obtenerUltimaPartida(puuid);
        if (idPartida == null) {
            return;
        }
        String body = peticionRiot(REGIONAL + "/lol/match/v5/matches/" + idPartida);
        if (body == null) {
            return;
        }
        JSONObject partida = new JSONObject(body);

        // Buscamos en qué posición del array de participantes está nuestro jugador
        JSONArray puuids = partida.getJSONObject("metadata").getJSONArray("participants");
        int posicion = -1;
        for (int i = 0; i < puuids.length(); i++) {
            if (puuids.getString(i).equals(puuid)) {
                posicion = i;
            }
        }
        if (posicion == -1) {
            System.out.println("No se encontró al jugador en su última partida.");
            return;
        }

        JSONObject info = partida.getJSONObject("info");
        Invocador invocador = gson.fromJson(
                info.getJSONArray("participants").getJSONObject(posicion).toString(), Invocador.class);

        // gameEndTimestamp viene en milisegundos desde el 1 de enero de 1970 (Unix timestamp)
        java.util.Date fecha = new java.util.Date(info.getLong("gameEndTimestamp"));
        String resultado = invocador.isWin() ? "VICTORIA" : "DERROTA";
        System.out.println("ULTIMA PARTIDA: " + invocador.getChampionName() + " (" + invocador.getKills() + "/"
                + invocador.getDeaths() + "/" + invocador.getAssists() + ") " + resultado + " el " + fecha);
    }

    public void closeClient() {
        client.close();
    }

    // ---------- Métodos privados ----------

    private String obtenerUltimaPartida(String puuid) {
        String body = peticionRiot(REGIONAL + "/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=1");
        if (body == null) {
            return null;
        }
        JSONArray ids = new JSONArray(body);
        if (ids.isEmpty()) {
            System.out.println("No hay partidas recientes.");
            return null;
        }
        return ids.getString(0);
    }

    private String obtenerNombreCampeon(long championId) {
        if (nombresCampeones == null) {
            cargarCampeones();
        }
        return nombresCampeones.getOrDefault(championId, "Campeón " + championId);
    }

    // Descarga una sola vez la lista de campeones (id -> nombre) de la última versión de Data Dragon
    private void cargarCampeones() {
        nombresCampeones = new HashMap<>();
        if (versionDataDragon == null) {
            String versiones = peticion(DATA_DRAGON + "/api/versions.json", false);
            if (versiones == null) {
                return;
            }
            versionDataDragon = new JSONArray(versiones).getString(0);
        }
        String body = peticion(DATA_DRAGON + "/cdn/" + versionDataDragon + "/data/es_ES/champion.json", false);
        if (body == null) {
            return;
        }
        JSONObject data = new JSONObject(body).getJSONObject("data");
        for (String clave : data.keySet()) {
            JSONObject campeon = data.getJSONObject(clave);
            nombresCampeones.put(Long.parseLong(campeon.getString("key")), campeon.getString("name"));
        }
    }

    private String peticionRiot(String url) {
        if (apiKey == null) {
            System.out.println("Falta la API key de Riot. Mira el README (RIOT_API_KEY o config.properties).");
            return null;
        }
        return peticion(url, true);
    }

    // Todas las peticiones pasan por aquí: timeout, API key en cabecera y control del código de estado
    private String peticion(String url, boolean conApiKey) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Accept", "application/json");
            if (conApiKey) {
                // La key va en una cabecera y no en la URL, para que no acabe guardada en logs
                builder.header("X-Riot-Token", apiKey);
            }
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status == 200) {
                return response.body();
            }
            switch (status) {
                case 401, 403 -> System.out.println("Riot rechaza la API key (HTTP " + status
                        + "). Las keys de desarrollo caducan cada 24 horas: genera una nueva.");
                case 404 -> System.out.println("No encontrado (HTTP 404). Revisa el nombre y el tag.");
                case 429 -> System.out.println("Demasiadas peticiones (HTTP 429). Espera "
                        + response.headers().firstValue("Retry-After").orElse("unos") + " segundos.");
                default -> System.out.println("Error de la API (HTTP " + status + ").");
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Petición interrumpida.");
            return null;
        } catch (IOException e) {
            System.out.println("No se pudo conectar: " + e.getMessage());
            return null;
        }
    }

    // URLEncoder pone "+" en los espacios; en una ruta de URL tiene que ser "%20"
    private String codificar(String texto) {
        return URLEncoder.encode(texto.trim(), StandardCharsets.UTF_8).replace("+", "%20");
    }

    // La key se busca primero en la variable de entorno RIOT_API_KEY y si no, en config.properties
    // (que está en el .gitignore). Nunca está escrita en el código.
    private String buscarApiKey() {
        String desdeEntorno = System.getenv("RIOT_API_KEY");
        if (desdeEntorno != null && !desdeEntorno.isBlank()) {
            return desdeEntorno.trim();
        }
        Properties propiedades = new Properties();
        try (FileInputStream entrada = new FileInputStream("config.properties")) {
            propiedades.load(entrada);
            String key = propiedades.getProperty("riot.api.key");
            return (key == null || key.isBlank()) ? null : key.trim();
        } catch (IOException e) {
            return null;
        }
    }
}
