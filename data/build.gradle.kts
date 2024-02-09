@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nisrulz.example.spacexapi.data"

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        named("test") {
            resources.srcDirs("src/test/resources")
        }
    }
}

dependencies {
    // Module Dependency
    implementation(project(":domain"))

    // Retrofit
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.retrofit)
    testImplementation(libs.okhttp.mockwebserver)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)

    // Room
    api(libs.bundles.room)
    ksp(libs.room.compiler)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.mockk)
    androidTestImplementation(libs.bundles.android.testing)
}
