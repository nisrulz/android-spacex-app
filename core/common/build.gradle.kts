@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.common"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.analytics)
    api(projects.core.logger)
}
