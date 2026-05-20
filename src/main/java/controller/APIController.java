package controller;

import com.google.gson.Gson;
import lombok.Data;
import model.Campeon;
import model.Invocador;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Data
public class APIController {
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<String> response;
    private String url;
    private final String api = "RGAPI-REMOVED";

    public Invocador obtenerPuuidYDevolverInvocador(String nameGame, String tagLine){
        Gson gson = new Gson();
        nameGame = nameGame.replace(" ", "%20");
        url = "https://europe.api.riotgames.com/riot/account/v1/accounts/by-riot-id/" + nameGame + "/" + tagLine + "?api_key=" + api;
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response =  client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObject = new JSONObject(response.body());
            Invocador invocador = gson.fromJson(jsonObject.toString(), Invocador.class);
            return invocador;
        }  catch (Exception e) {
            System.out.println("Error en la peticion");
        }
        return null;
    }

    public void conseguirRango(String puuid) {
        // url = "https://euw1.api.riotgames.com/lol/league/v4/entries/by-puuid/uioXWpIm56UeWTHtBI6jiA_pPgsQD_j64iO4jIB7jbqjr43LCWdBW_i-EFureRjmmO2IufFoRdCgjg?api_key=RGAPI-REMOVED";
        url = "https://euw1.api.riotgames.com/lol/league/v4/entries/by-puuid/" + puuid + "?api_key=" + api;
        Gson gson = new Gson();
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String bodyJSON = response.body();
            JSONArray jsonArray = new JSONArray(bodyJSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject liga = jsonArray.getJSONObject(i);
                Invocador invocador = gson.fromJson(liga.toString(), Invocador.class);
                System.out.println(invocador.getQueueType());
                System.out.println("DIVISION = " + invocador.getTier());
                System.out.println("RANK = " + invocador.getRank());
                System.out.println("PUNTOS = " + invocador.getLeaguePoints());
                System.out.println("WINS = " + invocador.getWins());
                System.out.println("LOSSES = " + invocador.getLosses());
                invocador.setWr(((double) invocador.getWins() / (invocador.getWins() + invocador.getLosses())) * 100);
                double winrate = invocador.getWr();
                System.out.printf("WINRATE = %.2f%%\n", winrate);
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTP (RANGO)");
        }
    }

    private String obtenerVersionMasReciente() {
        url = "https://ddragon.leagueoflegends.com/api/versions.json";
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            JSONArray jsonArrayVersiones = new JSONArray(body);
            return jsonArrayVersiones.getString(0);
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTP");
        }
        return "Version desconocida";
    }

    public void obtenerChampionByID(long championID) {
        //url = "https://ddragon.leagueoflegends.com/cdn/16.5.1/data/es_ES/champion.json";
        url = "https://ddragon.leagueoflegends.com/cdn/" + obtenerVersionMasReciente() + "/data/es_ES/champion.json";
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            JSONObject jsonObject = new JSONObject(body);

            JSONObject jsonObjectData = jsonObject.getJSONObject("data");

            for (String nombreCaja : jsonObjectData.keySet()) {
                JSONObject jsonObjectCampeon = jsonObjectData.getJSONObject(nombreCaja);
                String champID = jsonObjectCampeon.getString("key");
                String IDParametroPasadoAString = String.valueOf(championID);
                if (champID.equals(IDParametroPasadoAString)) {
                    System.out.print(jsonObjectCampeon.getString("name"));
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTP");
        }

    }

    /*public void obtener3ChampsMaxMaestria(String puuid) {
        url = "https://euw1.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-puuid/" + puuid + "?api_key=" + api;
        Gson gson = new Gson();
        https:
//euw1.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-puuid/uioXWpIm56UeWTHtBI6jiA_pPgsQD_j64iO4jIB7jbqjr43LCWdBW_i-EFureRjmmO2IufFoRdCgjg/top?api_key=RGAPI-REMOVED
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray(response.body());
            System.out.println(jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Campeon campeon = gson.

            }
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTTP");
        }
    }

    public void guardarVariableChampionIDyMaestria() {

    }*/

    public void imprimir3Mains(String puuid) {
        url = "https://euw1.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-puuid/" + puuid + "/top?api_key=" + api;
        Gson gson = new Gson();
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            JSONArray jsonArray3Mains = new JSONArray(body);
            System.out.println("TOP 3 CAMPEONES CON MAS MAESTRIA");
            for (int i = 0; i < jsonArray3Mains.length(); i++) {
                JSONObject jsonObjectChamp = jsonArray3Mains.getJSONObject(i);
                Campeon campeon = gson.fromJson(jsonObjectChamp.toString(), Campeon.class);
                obtenerChampionByID(campeon.getChampionId()); //utilizamos este metodo para saber cual es el nombre del id del campeon que aparece
                System.out.println(" - " + campeon.getChampionPoints() + " puntos de maestria");
            }
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTP (3mains)");
        }
    }

    private String obtenerUltimaPartida(String puuid) {
        url = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=1&api_key=" + api;
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray(response.body());
            return jsonArray.getString(0);
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTP (obtenerUltimaPartida)");
        }
        return "null";
    }

    public void verUltimaPartida(String puuid) {
        url = "https://europe.api.riotgames.com/lol/match/v5/matches/" + obtenerUltimaPartida(puuid) + "?api_key=" + api;
        int posicionArrayPartida = 0;
        Gson gson = new Gson();
        try {
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObjectPartida = new JSONObject(response.body());
            JSONObject jsonObjectMetaData = jsonObjectPartida.getJSONObject("metadata");
            JSONArray jsonArrayParticipants1 = jsonObjectMetaData.getJSONArray("participants");
            for (int i = 0; i < jsonArrayParticipants1.length(); i++) {
                String puuidJugadores = jsonArrayParticipants1.getString(i);
                if (puuidJugadores.equals(puuid)) {
                    posicionArrayPartida = i;
                }
            }
            JSONObject jsonObjectInfo = jsonObjectPartida.getJSONObject("info");
            JSONArray jsonArrayParticipants2 = jsonObjectInfo.getJSONArray("participants");
            JSONObject jsonObjectInvocador = jsonArrayParticipants2.getJSONObject(posicionArrayPartida);
            Invocador invocador = gson.fromJson(jsonObjectInvocador.toString(), Invocador.class);

            //PARA OBTENER LA FECHA EN LA QUE SE JUGO LA PARTIDA ESTA EN EL ATRIBUTO gameEndTimestamp QUE ESTA EN FORMATO formato Unix Timestamp (milisegundos),
            // ES UNA UNIDAD DE MEDIDA QUE MIDE EN MILISEGUNDOS DESDE 1970(EL BIG BANG DE LA INFORMATICA)
            long fechaPartidaEnMilisegundos = jsonObjectInfo.getLong("gameEndTimestamp");
            java.util.Date fechaReal = new java.util.Date(fechaPartidaEnMilisegundos);

            //Traducir victoria o derrota
            String resultado;
            if (invocador.isWin()) {
                resultado = "VICTORIA";
            } else {
                resultado = "DERROTA";
            }
            System.out.println("ULTIMA PARTIDA: " + invocador.getChampionName() + " CLINICO (" + invocador.getKills() + "/" + invocador.getDeaths() + "/" + invocador.getAssists() + ") " + resultado + " el " + fechaReal);
        } catch (Exception e) {
            System.out.println("ERROR EN LA PETICION HTTP (verUltimaPartida)");
        }
    }

    public void closeClient(){
        client.close();
    }
}
