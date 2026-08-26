// ============================================================
// ROOT build.gradle.kts
// ============================================================
// This is the project-level Gradle file.
// It defines plugin VERSIONS used by all sub-modules.
// We use the "version catalog" approach via plugins block.
// ============================================================

plugins {
    // Android application plugin — applied in app/build.gradle.kts
    id("com.android.application") version "8.3.0" apply false

    // Kotlin Android plugin
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // Hilt plugin — needed for annotation processing
    id("com.google.dagger.hilt.android") version "2.50" apply false

    // KSP (Kotlin Symbol Processing) — replaces kapt, faster
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
