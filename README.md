# API_LOL - League of Legends API Client 🎮

Una aplicación robusta en Java que interactúa con la **Riot Games API** para extraer, procesar y mostrar estadísticas en tiempo real de jugadores de League of Legends. El proyecto está construido bajo una arquitectura limpia (MVC) utilizando Maven para la gestión de dependencias.

## 🚀 Características

- **Búsqueda de Invocadores:** Obtención del `PUUID` y datos de cuenta mediante el Riot ID (`gameName` + `tagLine`), gestionando automáticamente la codificación de espacios en la URL.
- **Consulta de Rango y Liga:** Extracción de la división, tier, puntos de liga (LP), victorias, derrotas y cálculo dinámico automatizado del **Winrate (%)** del jugador.
- **Top 3 de Maestría:** Identificación de los 3 campeones con más puntos de maestría, cruzando los IDs devueltos por Riot con la API de **Data Dragon** de forma dinámica para obtener los nombres reales de los personajes.
- **Historial Reciente:** Análisis de la última partida jugada por el usuario para extraer el campeón utilizado, el KDA preciso (`Kills/Deaths/Assists`), el resultado (Victoria/Derrota) y la conversión del Timestamp UNIX a fecha legible.
- **Versiones Dinámicas:** Consumo automatizado del endpoint de versiones de Data Dragon para asegurar que los recursos e imágenes del juego nunca se queden obsoletos tras un parche de Riot.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java (Compatible con Java 11/17/21)
- **Gestor de Proyectos:** Maven
- **Cliente HTTP:** `java.net.http.HttpClient` (Nativo de Java para peticiones asíncronas/síncronas)
- **Procesamiento JSON:** - [Google GSON](https://github.com/google/gson) (Para mapeo directo a objetos/modelos)
  - [JSON-Java (org.json)](https://github.com/stleary/JSON-java) (Para manipulación dinámica de JSONArrays y JSONObjects)
- **Optimización de Código:** [Lombok](https://projectlombok.org/) (Para la generación automática de Getters, Setters y `@Data`)

## ⚙️ Configuración e Instalación

Para proteger la integridad de las credenciales, la clave de la API de Riot no está hardcodeada en el código fuente y se encuentra protegida a través del sistema de exclusión de Git.

### Prerrequisitos

Asegúrate de tener instalado:
- JDK 11 o superior
- Maven

### Pasos para el Setup local

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/RicardoMatosR/API_LOL.git](https://github.com/RicardoMatosR/API_LOL.git)
   cd API_LOL
   ```

2. **Configurar las credenciales secretas:**
   El proyecto incluye un archivo de plantilla llamado `config.properties.example`. Debes crear una copia de este archivo en la raíz del proyecto llamada exactamente `config.properties`:
   ```bash
   cp config.properties.example config.properties
   ```

3. **Añadir tu API Key:**
   Abre el nuevo archivo `config.properties` con tu editor de texto favorito e introduce tu clave de desarrollo de Riot Games:
   ```properties
   riot.api.key=RGAPI-TU-CLAVE-PERSONAL-AQUI
   ```
   *Nota: El archivo `config.properties` está incluido en el `.gitignore`, por lo que tus claves nunca se subirán a GitHub por error.*

4. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```

5. **Ejecutar la aplicación:**
   ```bash
   mvn exec:java -Dexec.mainClass="Main"
   ```

## 📁 Estructura del Proyecto

```text
API_LOL/
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
