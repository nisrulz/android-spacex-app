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
    implementation(projects.presentation)

    /**
     * @TODO: Remove this workaround when below issues are solved with AGP 8.3.0
     * https://github.com/google/guava/issues/6618
     * https://github.com/android/nowinandroid/pull/1140#issuecomment-1979431658
     */
    modules {
        module("com.google.guava:listenablefuture") {
            replacedBy("com.google.guava:guava", "listenablefuture is part of guava")
        }
    }
}
