import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.application)
    alias(libs.plugins.spacexapi.android.app.hilt)
}

android {
    namespace = ApplicationInfo.BASE_NAMESPACE
}

dependencies {
    // Module Dependency
    implementation(projects.presentation)
}
