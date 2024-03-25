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
