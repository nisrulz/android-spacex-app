import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.testing)
    alias(libs.plugins.spacexapi.android.jacoco)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.storage.sqldelight"
}

sqldelight {
    databases {
        create("SpaceXDatabase") {
            packageName.set("${ApplicationInfo.BASE_NAMESPACE}.storage.sqldelight")
        }
    }
}

dependencies {
    // Shared storage interface
    api(projects.core.storage)

    // SQLDelight
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.testing.core)
}
