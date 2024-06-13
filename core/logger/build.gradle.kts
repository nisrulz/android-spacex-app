import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.logger"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.timber)
}
