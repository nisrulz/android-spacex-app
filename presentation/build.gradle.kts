@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")
}
android {
    namespace = "com.nisrulz.example.spacexapi.presentation"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Module Dependency
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(project(":data"))

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

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Debug
    debugImplementation(libs.bundles.compose.debug)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.mockk)
    androidTestImplementation(libs.bundles.android.testing)
}
