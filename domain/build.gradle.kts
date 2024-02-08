@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    id("library-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.domain"
}

dependencies {
    // Dagger-Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.mockk)
}
