@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.domain"
}

dependencies {
    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.mockk)
}
