import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
    alias(libs.plugins.spacexapi.android.testing)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.data"

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
