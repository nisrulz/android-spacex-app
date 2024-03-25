plugins {
    id("library-convention")
    id("hilt-convention")
    id("testing-convention")

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nisrulz.example.spacexapi.network.retrofit"
}

dependencies {
    // Retrofit
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.retrofit)
    testImplementation(libs.okhttp.mockwebserver)
}
