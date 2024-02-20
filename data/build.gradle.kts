@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")
    id("testing-convention")
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
    api(projects.domain)
    implementation(projects.core.networkRetrofit)
    implementation(projects.core.storageRoomdb)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
}
