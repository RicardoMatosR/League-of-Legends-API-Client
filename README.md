# API_LOL - League of Legends API Client 🎮

Una aplicación robusta en Java que interactúa con la **Riot Games API** para extraer, procesar y mostrar estadísticas en tiempo real de jugadores de League of Legends. El proyecto está construido bajo una arquitectura limpia (MVC) utilizando Maven para la gestión de dependencias.

## 🚀 Características

- **Búsqueda de Invocadores:** Obtención del `PUUID` y datos de cuenta mediante el Riot ID (`gameName` + `tagLine`), gestionando automáticamente la codificación de espacios en la URL.
- **Consulta de Rango y Liga:** Extracción de la división, tier, puntos de liga (LP), victorias, derrotas y cálculo dinámico automatizado del **Winrate (%)** del jugador.
- **Top 3 de Maestría:** Identificación de los 3 campeones con más puntos de maestría, cruzando los IDs devueltos por Riot con la API de **Data Dragon** de forma dinámica para obtener los nombres reales de los personajes.
- **Historial Reciente:** Análisis de la última partida jugada por el usuario para extraer el campeón utilizado, el KDA preciso (`Kills/Deaths/Assists`), el resultado (Victoria/Derrota) y la conversión del Timestamp UNIX a fecha legible.
- **Versiones Dinámicas:** Consumo automatizado del endpoint de versiones de Data Dragon para asegurar que los recursos e imágenes del juego nunca se queden obsoletos tras un parche de Riot.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java (Java 21 o superior)
- **Gestor de Proyectos:** Maven
- **Cliente HTTP:** `java.net.http.HttpClient` (Nativo de Java para peticiones asíncronas/síncronas)
- **Procesamiento JSON:** - [Google GSON](https://github.com/google/gson) (Para mapeo directo a objetos/modelos)
  - [JSON-Java (org.json)](https://github.com/stleary/JSON-java) (Para manipulación dinámica de JSONArrays y JSONObjects)
- **Optimización de Código:** [Lombok](https://projectlombok.org/) (Para la generación automática de Getters, Setters y `@Data`)

## ⚙️ Configuración e Instalación

La API key de Riot **nunca está en el código**. Se lee de la variable de entorno `RIOT_API_KEY` o, si no existe, del fichero `config.properties`, que está en el `.gitignore`.

### Prerrequisitos

- JDK 21 o superior
- Maven
- Una API key de desarrollo de [Riot Developer Portal](https://developer.riotgames.com) (caduca cada 24 horas)

### Pasos

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/RicardoMatosR/League-of-Legends-API-Client.git
   cd League-of-Legends-API-Client
   ```

2. **Poner tu API key** (elige una opción):

   - Variable de entorno:
     ```bash
     # Linux / macOS
     export RIOT_API_KEY=RGAPI-tu-clave
     # Windows PowerShell
     $env:RIOT_API_KEY="RGAPI-tu-clave"
     ```
   - O fichero de configuración:
     ```bash
     cp config.properties.example config.properties
     ```
     y escribe tu clave en `riot.api.key`.

3. **Ejecutar:**
   ```bash
   mvn compile exec:java
   ```

## 🔒 Cómo se hacen las peticiones

- **Un solo `HttpClient`** para toda la app, con *timeouts*.
- La key viaja en la **cabecera `X-Riot-Token`**, no en la URL, para que no quede guardada en logs.
- Se comprueba el **código de estado** antes de leer el JSON: 401/403 (key caducada), 404 (jugador no encontrado) y 429 (límite de peticiones, con el tiempo de espera que indica Riot).
- El nombre y el tag se codifican con `URLEncoder`.
- La lista de campeones de Data Dragon se descarga **una sola vez** y se guarda en memoria.

## 📁 Estructura del Proyecto

```text
League-of-Legends-API-Client/
│
├── src/
│   └── main/
│       └── java/
│           ├── controller/
│           │   ├── APIController.java       # Gestión de peticiones HTTP y lógica de negocio
│           │   ├── FileController.java      # Control de persistencia y archivos locales
│           │   └── GestorInvocadores.java   # Orquestador del flujo de la aplicación
│           │
│           ├── model/
│           │   ├── Campeon.java             # Plantilla de datos para campeones y maestrías
│           │   └── Invocador.java           # Plantilla de datos para perfiles y rangos
│           │
│           └── Main.java                    # Punto de entrada de la aplicación
│
├── config.properties.example                # Plantilla pública de configuración
├── .gitignore                               # Filtro de archivos para Git (Protege config.properties)
└── pom.xml                                  # Configuración de Maven y dependencias del proyecto
```
