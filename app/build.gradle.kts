// ============================================================
// app/build.gradle.kts
// ============================================================
// App-level Gradle file.
// Defines: compileSdk, dependencies, Hilt & Compose setup.
//
// KEY POINTS FOR AAOS:
//   - compileSdk 34 (Android 14, AAOS API 34)
//   - In a real AAOS project you'd also add:
//       useLibrary("android.car")  ← gives access to Car Services
//   - We use a FakeDataSource here so this runs on any phone/emulator
// ============================================================

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")   // Hilt code generation
    id("com.google.devtools.ksp")           // KSP for Hilt
}

android {
    namespace = "com.example.aaosdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aaosdemo"
        minSdk = 29        // Android 10 — AAOS baseline is API 29+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    // Enable Jetpack Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        // Must match the Kotlin version used
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // ── CORE ──────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // ── JETPACK COMPOSE ───────────────────────────────────────
    // BOM = Bill of Materials. It pins all Compose versions together.
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")   // @Preview support
    implementation("androidx.compose.material3:material3")     // Material 3 components
    implementation("androidx.compose.material:material-icons-extended") // Icons
    implementation("androidx.activity:activity-compose:1.8.2") // setContent {}

    debugImplementation("androidx.compose.ui:ui-tooling")      // Layout inspector

    // ── NAVIGATION ────────────────────────────────────────────
    // Compose Navigation — navigate between screens
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ── LIFECYCLE (StateFlow → Compose) ───────────────────────
    // collectAsStateWithLifecycle() — lifecycle-aware Flow collection
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ── HILT (Dependency Injection) ───────────────────────────
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")        // Code generation

    // Hilt + Navigation Compose integration
    // Gives us: hiltViewModel() in Composables
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ── COROUTINES ────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ── REAL AAOS (commented out — needs AAOS system image) ───
    // compileOnly("com.google.android.gms:play-services-car:20.0.0")
    // In AOSP: useLibrary("android.car") in android {} block
}
