// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false

    // Kotlin
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false

    // Hilt
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false

    // Serialization
    alias(libs.plugins.kotlin.serialization) apply false

    // Compose
    alias(libs.plugins.compose.compiler) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
