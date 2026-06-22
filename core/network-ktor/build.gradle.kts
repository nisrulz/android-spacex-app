import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
    alias(libs.plugins.spacexapi.android.testing)
    alias(libs.plugins.spacexapi.android.jacoco)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.network.ktor"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "API_BASE_URL",
            "\"${project.findProperty("API_BASE_URL") ?: "https://localhost:8443/"}\"")
    }
}

dependencies {
    // Shared network interface and DTOs
    api(projects.core.network)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.json)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.testing.core)
    testImplementation(libs.ktor.client.mock)
}
