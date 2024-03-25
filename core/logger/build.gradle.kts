plugins {
    id("library-convention")
    id("hilt-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.logger"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.timber)
}
