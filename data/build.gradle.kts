@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")
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
    implementation(projects.domain)
    implementation(projects.core.networkRetrofit)

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
