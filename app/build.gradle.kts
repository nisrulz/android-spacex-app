@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("app-convention")
    id("hilt-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi"

    defaultConfig {
        applicationId = "com.nisrulz.example.spacexapi"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Module Dependency
    implementation(project(":presentation"))
}
