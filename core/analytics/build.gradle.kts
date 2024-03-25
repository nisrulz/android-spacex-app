plugins {
    id("library-convention")
    id("hilt-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.analytics"

    buildFeatures {
        buildConfig = true
    }
}
