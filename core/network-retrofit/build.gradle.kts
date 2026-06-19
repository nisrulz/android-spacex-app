import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)

    alias(libs.plugins.spacexapi.android.testing)
    alias(libs.plugins.spacexapi.android.jacoco)

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.network.retrofit"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "API_BASE_URL",
            "\"${project.findProperty("API_BASE_URL") ?: "https://localhost:8443/"}\"")
    }
}

dependencies {
    // Retrofit
    implementation(platform(libs.okhttp.bom))
    implementation(platform(libs.retrofit.bom))
    implementation(libs.bundles.retrofit)
    testImplementation(libs.okhttp.mockwebserver)
}
