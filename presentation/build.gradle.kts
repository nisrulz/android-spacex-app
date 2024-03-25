plugins {
    id("library-convention")
    id("hilt-convention")
    id("testing-convention")
    id("jetpack-compose-convention")
}
android {
    namespace = "com.nisrulz.example.spacexapi.presentation"
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
