@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    id("app-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        applicationId = "com.nisrulz.example.spacexapi"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Module Dependency
    implementation(project(":presentation"))

    // Dagger-Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)
}
