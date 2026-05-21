# BuscaMascota 🐾

Aplicación móvil Android para reportes ciudadanos sobre animales: mascotas
perdidas, avistamientos en la calle y casos de maltrato. Su funcionalidad
diferencial es **vincular avistamientos con reportes de animales perdidos**
mediante reconocimiento visual con IA on-device, para ayudar a los dueños
a recuperar a sus mascotas.

> Proyecto académico de la materia **Aplicaciones Móviles** —
> Universidad Autónoma de Bucaramanga (UNAB).
> Desarrollador único: Santiago Ruiz.

---

## 📋 Tabla de contenido

- [Funcionalidades](#-funcionalidades)
- [Tech stack](#-tech-stack)
- [Arquitectura](#-arquitectura)
- [Pipeline de IA on-device](#-pipeline-de-ia-on-device)
- [Modelo de datos](#-modelo-de-datos)
- [Cómo correr el proyecto](#-cómo-correr-el-proyecto)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Estado de desarrollo](#-estado-de-desarrollo)
- [Decisiones de diseño](#-decisiones-de-diseño)
- [Convenciones](#-convenciones)
- [Autor](#-autor)

---

## ✨ Funcionalidades

- **Autenticación con Firebase Auth** (email/contraseña) y **modo invitado**
  (sesión anónima) para explorar la app sin registrarse. La cuenta real solo
  se exige al momento de **publicar** un reporte.
- **Tres tipos de reporte:** animal perdido, avistamiento y abuso/maltrato.
- **Crear reporte** con foto, ubicación (mapa con buscador de dirección),
  especie (Perro / Gato / Otro), descripción y datos del animal.
- **Feed cronológico** de reportes abiertos.
- **Mapa de reportes** con marcadores diferenciados por tipo
  (rojo = perdido · azul = avistamiento · naranja = abuso).
- **Pantalla de alertas** filtrada por tipo de reporte.
- **Detalle del reporte** con ubicación legible (geocoding inverso) y foto
  ampliada.
- **Perfil del usuario** con sus reportes publicados.
- **Detección automática de especie** en la foto del reporte (ML Kit).
- **Búsqueda de coincidencias visuales** entre un avistamiento y los
  reportes de "perdido" cercanos (TensorFlow Lite + geohash).
- **Ubicación en tiempo real** del usuario (Play Services Location).

---

## 🛠 Tech stack

| Capa | Tecnología |
|---|---|
| **Lenguaje** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose + Material 3 |
| **Navegación** | Navigation Compose (rutas type-safe con kotlinx.serialization) |
| **DI** | Hilt 2.59.2 |
| **Asincronía** | Coroutines + Flow |
| **Backend** | Firebase Auth + Cloud Firestore (plan **Spark** gratuito) |
| **Mapa** | Google Maps Compose |
| **Ubicación** | FusedLocationProviderClient |
| **IA on-device** | ML Kit Image Labeling + TensorFlow Lite (MobileNet V3 small) |
| **Imágenes** | Coil 2.7 (decoder de base64 vía `ByteBuffer`) |
| **Build** | AGP 9.0.1 · KSP · Gradle Version Catalog (`libs.versions.toml`) |

`minSdk 26` · `targetSdk 36` · `compileSdk 36` · Java 11.

---

## 🏗 Arquitectura

Clean Architecture ligera en tres capas:

```
app/src/main/java/com/santiagoruiz/buscamascota/
├── ui/        # Composables, ViewModels, navegación, theme
├── domain/    # Modelos de dominio, interfaces de repositorios, use cases
├── data/      # Implementaciones de repositorios, fuentes (Firebase, ML), DTOs y mappers
└── di/        # Módulos Hilt
```

- Los **ViewModels** exponen estado con `StateFlow`; los estados de UI son
  `sealed class` (loading / success / error / empty).
- El **dominio no conoce a Firebase**: los repositorios son interfaces y se
  implementan en `data/`. Hilt cablea las implementaciones (`@Binds`).
- Sin `LiveData`, sin `runBlocking`, sin `GlobalScope`.

---

## 🤖 Pipeline de IA on-device

Todo el procesamiento corre en el dispositivo para mantenerse en el plan
gratuito de Firebase (sin Vertex AI ni Vector Search):

1. **ML Kit Image Labeling** actúa como pre-filtro para detectar la
   especie del animal en la foto.
2. **MobileNet V3 small (TF Lite)** extrae un **embedding visual de 1024
   dimensiones** de la foto.
3. El embedding se guarda en Firestore como parte del documento del reporte.
4. Para buscar coincidencias se consultan los reportes **dentro de una
   zona geográfica** (filtro por prefijo de **geohash** de precisión 7) y
   se calcula la **similitud coseno del lado del cliente**.

> Decisión consciente: no se usa Firestore Vector Search — requeriría plan
> Blaze y no tiene SDK nativo de Kotlin para Android.

---

## 📦 Modelo de datos

**Cloud Firestore** (no Realtime Database):

- `reports/{reportId}` — tipo (`LOST` / `SIGHTING` / `ABUSE`), datos del
  animal (especie, color, embedding visual), ubicación (`lat`, `lng`,
  `geoHash`), foto en **base64**, `ownerId`, `createdAt`, `status`.
- `users/{uid}` — perfil básico (nombre, contacto).

**Las fotos viven en el mismo documento como base64** (≤ 1 MB), comprimidas
a JPEG q60 y máximo ~800 px. Esto evita habilitar Firebase Storage (que
ahora exige plan Blaze en proyectos nuevos).

**Índice compuesto necesario** (declarado en `firestore.indexes.json`):

```
reports: status (ASC) + createdAt (DESC)
```

**Reglas de seguridad** (`firestore.rules`) versionadas en la raíz:

- Cualquier sesión autenticada (incluido invitado anónimo) puede **leer**
  reportes.
- Solo cuentas **reales** (no anónimas) pueden crear/editar/borrar sus
  propios reportes (`ownerId == request.auth.uid`).

---

## 🚀 Cómo correr el proyecto

### Requisitos

- **Android Studio** Iguana o superior (AGP 9 requiere una versión
  reciente).
- **JDK 17+** (lo trae Android Studio).
- **Cuenta de Firebase** con un proyecto en plan Spark (gratis).
- **API Key de Google Maps** (Maps SDK for Android habilitado).

### 1. Clonar

```bash
git clone https://github.com/santiagoRuiz1314/Busca_a_tu_Mascota_App.git
cd Busca_a_tu_Mascota_App
```

### 2. Configurar Firebase

1. Crea un proyecto en <https://console.firebase.google.com/>.
2. Registra una app Android con el package
   `com.santiagoruiz.buscamascota`.
3. Descarga `google-services.json` y colócalo en `app/`.
4. En **Authentication → Sign-in method** habilita:
   - **Email/Password** (cuentas reales).
   - **Anonymous** (modo invitado).
5. En **Firestore Database**:
   - Crea la base en modo producción.
   - Pega el contenido de `firestore.rules` en la pestaña **Rules** y
     publícalo.
   - Crea el índice compuesto descrito en `firestore.indexes.json` (o
     simplemente abre el feed en la app y sigue el link que aparece en
     `logcat` al primer fallo).

### 3. Configurar Google Maps

Crea `local.properties` en la raíz (ya está en `.gitignore`) con tu API key:

```properties
MAPS_API_KEY=AIza...
```

### 4. Modelo de IA

Coloca el archivo del modelo MobileNet V3 small en
`app/src/main/assets/` con el nombre que el código espera (el `.tflite`
está en `.gitignore` por ser binario pesado). Tensor de salida esperado:
`float32[-1, 1024]`.

### 5. Compilar y correr

```bash
./gradlew assembleDebug
```

O simplemente ▶️ desde Android Studio. Probado en emulador
`Pixel 6 API 34` y dispositivo físico.

> ⚠️ **Tip de emulador:** la prioridad `BALANCED` de FusedLocation devuelve
> `null` en el emulador — la app usa `HIGH_ACCURACY` por defecto.

---

## 📁 Estructura del proyecto

```
BuscaMascota/
├── app/
│   ├── src/main/
│   │   ├── java/com/santiagoruiz/buscamascota/
│   │   │   ├── ui/
│   │   │   │   ├── auth/         # Login, registro, splash
│   │   │   │   ├── feed/         # Feed cronológico
│   │   │   │   ├── map/          # Mapa de reportes
│   │   │   │   ├── alerts/       # Alertas por tipo
│   │   │   │   ├── search/       # Búsqueda
│   │   │   │   ├── report/       # Asistente de 2 pasos para crear
│   │   │   │   ├── detail/       # Detalle del reporte
│   │   │   │   ├── matching/     # Coincidencias visuales
│   │   │   │   ├── profile/      # Perfil del usuario
│   │   │   │   ├── navigation/   # NavGraphs type-safe + bottom nav
│   │   │   │   ├── session/      # Estado global de sesión
│   │   │   │   ├── common/       # Componentes reutilizables (ReportCard, etc.)
│   │   │   │   └── theme/        # Color, Type, Shape, Theme (Stitch tokens)
│   │   │   ├── domain/
│   │   │   │   ├── repository/   # Interfaces
│   │   │   │   └── util/         # GeoHash, etc.
│   │   │   ├── data/
│   │   │   │   ├── auth/         # FirebaseAuthDataSource
│   │   │   │   ├── report/       # FirestoreReportDataSource, DTOs
│   │   │   │   ├── location/     # FusedLocation, Geocoder
│   │   │   │   ├── image/        # Compresión + base64
│   │   │   │   ├── ml/           # ML Kit + TFLite (embedding + similitud)
│   │   │   │   └── user/         # Perfiles
│   │   │   └── di/               # Módulos Hilt
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── firestore.rules               # Reglas de seguridad
├── firestore.indexes.json        # Índices compuestos
├── gradle/libs.versions.toml     # Version catalog
└── CLAUDE.md                     # Contexto para el asistente
```

---

## 📅 Estado de desarrollo

El proyecto se construyó por **fases incrementales** (walking skeleton +
vertical slices). Todas las fases planeadas están terminadas:

- ✅ **Fase 1** — Fundación: Gradle, Hilt, navegación, theme, placeholders.
- ✅ **Fase 2** — Autenticación con Firebase Auth.
- ✅ **Fase 3** — Crear reporte (foto base64, ubicación, datos).
- ✅ **Fase 4** — Lectura de reportes (feed, alertas, detalle).
- ✅ **Fase 5** — Mapa del feed con marcadores por tipo.
- ✅ **Fase 6** — Pipeline de IA on-device (especie + embedding + matching).
- ✅ **Fase 7** — Perfil de usuario + endurecimiento de reglas Firestore.
- ✅ **Extra** — Modo invitado (sesión anónima) y rediseño UI completo
  (tema Stitch "Premium Indigo / Community Pet Rescue").

---

## 🎨 Decisiones de diseño

- **Diseño visual:** tema **Stitch — Premium Indigo / Community Pet
  Rescue**. Los tokens (colores, tipografía, spacing, shapes) se mapearon
  semánticamente por uso en `ui/theme/` (`primaryAction`, `surfaceMuted`,
  etc., no descriptivos del color).
- **"Nuevo Reporte"** es un asistente de 2 pasos sobre un único
  `submit()` del ViewModel.
- **Sin Firebase Storage:** las fotos viven como base64 en el documento del
  reporte (≤ 1 MB/doc), comprimidas a JPEG q60.
- **Sin Google Places API:** el buscador de dirección usa el **Geocoder
  integrado de Android** (gratis, sin billing). En API 33+ se usa el
  `GeocodeListener` asíncrono; por debajo, llamada bloqueante en `IO`.
- **Controles sin backend** del diseño (Google Sign-In, "Contactar",
  estadísticas) se muestran pero **deshabilitados** mediante un helper
  `Modifier.comingSoon()`, para mantener la fidelidad visual sin prometer
  funcionalidad que no existe.

---

## 📐 Convenciones

- Identificadores en código (clases, funciones, variables, paquetes) en
  **inglés**.
- Strings de UI y comentarios en **español**.
- Jetpack Compose + Material 3 como única tecnología de UI (sin XML
  layouts).
- En el theme los nombres son **semánticos por uso**, no descriptivos del
  color.
- Sin `LiveData`, sin `runBlocking`, sin `GlobalScope`.

---

## 👤 Autor

**Santiago Ruiz** — Estudiante de Ingeniería de Sistemas, UNAB.
📧 davidl.ruizc@gmail.com
🐙 [@santiagoRuiz1314](https://github.com/santiagoRuiz1314)

---

## 📄 Licencia

Proyecto académico sin licencia formal. Si quieres reutilizar parte del
código, escríbeme.
