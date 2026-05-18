import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

// Clave de Google Maps fuera del control de versiones (local.properties está
// en .gitignore). Se inyecta en el manifest vía manifestPlaceholders.
val mapsApiKey: String = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}.getProperty("MAPS_API_KEY", "")

android {
    namespace = "com.santiagoruiz.buscamascota"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.santiagoruiz.buscamascota"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    androidResources {
        // El modelo TFLite debe quedar SIN comprimir en el APK para poder
        // mapearlo en memoria (MappedByteBuffer) al cargar el Interpreter.
        noCompress += "tflite"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Navegación
    implementation(libs.androidx.navigation.compose)

    // Hilt (inyección de dependencias)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // ViewModel en Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Asincronía
    implementation(libs.kotlinx.coroutines.android)

    // Serialización (rutas type-safe de navegación)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Ubicación
    implementation(libs.play.services.location)

    // Mapa (selector de ubicación del reporte)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    // Interop corrutinas <-> Tasks de Play Services (Task.await())
    implementation(libs.kotlinx.coroutines.play.services)

    // Carga de imágenes (foto del reporte: base64 → ByteBuffer)
    implementation(libs.coil.compose)

    // IA on-device (Fase 6): especie con ML Kit, embedding con TFLite.
    implementation(libs.mlkit.image.labeling)
    implementation(libs.tensorflow.lite)
}