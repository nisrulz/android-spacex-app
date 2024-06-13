import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.common"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.analytics)
    api(projects.core.logger)
}
