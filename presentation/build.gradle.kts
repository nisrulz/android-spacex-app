import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
    
    alias(libs.plugins.spacexapi.android.testing)
    alias(libs.plugins.spacexapi.android.jacoco)

    alias(libs.plugins.spacexapi.android.compose)
    alias(libs.plugins.spacexapi.android.compose.navigation)
}
android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.presentation"
}

dependencies {
    // Module Dependency
    implementation(projects.core.common)
    implementation(projects.data)

    // Material
    implementation(libs.bundles.material)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil
    api(libs.bundles.coil)

    // Kotlin Extensions
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.core)
    implementation(libs.bundles.ktx)
}
