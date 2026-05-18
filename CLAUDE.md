# CLAUDE.md — Contexto del proyecto

## Qué es este proyecto

Aplicación móvil Android nativa para reportes ciudadanos sobre animales, organizados en tres categorías:

- **Animales perdidos** — un dueño reporta que su mascota se extravió.
- **Avistamientos** — cualquier usuario reporta haber visto un animal en la calle.
- **Abuso o maltrato animal** — denuncias ciudadanas sobre casos de maltrato.

Una funcionalidad central de la app es **vincular avistamientos con reportes de animales perdidos** mediante reconocimiento visual con IA, para ayudar a recuperar mascotas.

Es un proyecto académico de la materia Aplicaciones Móviles de la Universidad Autónoma de Bucaramanga (UNAB). Desarrollador único.

## Funcionalidades principales

- Autenticación de usuarios
- Crear, listar y consultar reportes con foto, ubicación, descripción y datos del animal
- Visualización de reportes en un mapa
- Visualización de reportes en un feed
- Detección automática de la especie del animal a partir de la foto
- Búsqueda de coincidencias visuales entre avistamientos y reportes de animales perdidos en la misma zona geográfica
- Ubicación del usuario en tiempo real

## Tech stack

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose
- **Inyección de dependencias:** Hilt
- **Backend:** Firebase (Auth, Firestore, Storage) — plan Spark (gratuito)
- **Mapa:** Google Maps Compose
- **Ubicación:** FusedLocationProviderClient (Play Services)
- **IA on-device:** ML Kit Image Labeling + TensorFlow Lite con MobileNet V3 small
- **Carga de imágenes:** Coil
- **Asincronía:** Coroutines + Flow

## Arquitectura

Clean Architecture ligera en tres capas:

- **ui** — composables, ViewModels, navegación, theme
- **domain** — modelos de dominio, interfaces de repositorios, casos de uso
- **data** — implementaciones de repositorios, fuentes de datos (Firebase, ML), DTOs y mappers

Los ViewModels exponen estado mediante `StateFlow`, con los estados de UI modelados como sealed types.

## Modelo de datos (conceptual)

- **Usuarios:** perfil básico (nombre, contacto, foto).
- **Reportes:** tipo (perdido / avistamiento / abuso), información del animal (especie, raza, color, embedding visual), ubicación (coordenadas + geohash), fotos, dueño del reporte, marca de tiempo, estado del reporte.
- **Almacenamiento:** las fotos de los reportes viven en Firebase Storage; los documentos de reportes y usuarios viven en Firestore.

## Diseño

El diseño completo de la aplicación está en Figma. Hay un servidor **Figma MCP** conectado, por lo que los tokens de diseño (colores, tipografía, spacing) y la estructura de cada pantalla se leen directamente desde el archivo de Figma. La implementación debe ser visualmente fiel al diseño original.

**Archivo de Figma:** <https://www.figma.com/design/Bhqp4QTKoT1Z3wefha7sor/Mockup-protitipo>

## Enfoque de IA

Pipeline 100% on-device, compatible con el plan gratuito de Firebase:

- **ML Kit Image Labeling** funciona como pre-filtro para detectar la especie del animal en la foto.
- **MobileNet V3 (TF Lite)** extrae un embedding visual (~1024 dimensiones) de la foto.
- El embedding se almacena en Firestore como parte del documento del reporte.
- La búsqueda de coincidencias se realiza consultando reportes dentro de una zona geográfica (filtro por prefijo de geohash) y calculando **similitud coseno del lado del cliente**.
- No se usa Firestore Vector Search (requeriría plan Blaze y no tiene SDK nativo de Kotlin para Android).

## Restricciones

- El proyecto se mantiene en el plan Spark de Firebase; sin dependencias o servicios de pago.
- Toda la UI en Jetpack Compose; sin XML layouts.
- Sin `LiveData`, `runBlocking` ni `GlobalScope`.

## Convenciones

- Identificadores en código (clases, funciones, variables, paquetes) en **inglés**.
- Strings de UI y comentarios en **español**.
- Material 3 + Compose como única tecnología de UI.
- En el theme, los nombres de colores y tipografías son semánticos por uso (por ejemplo `primaryAction`, `surfaceMuted`), no descriptivos del color.
