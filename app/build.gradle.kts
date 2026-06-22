import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.application)
    alias(libs.plugins.spacexapi.android.app.hilt)
}

android {
    namespace = ApplicationInfo.BASE_NAMESPACE

    defaultConfig {
        // BuildConfig flags for swapping implementations
        // Usage: ./gradlew assembleDebug -PNETWORK_IMPL=ktor -PSTORAGE_IMPL=sqldelight
        buildConfigField("String", "NETWORK_IMPL",
            "\"${project.findProperty("NETWORK_IMPL") ?: "retrofit"}\"")
        buildConfigField("String", "STORAGE_IMPL",
            "\"${project.findProperty("STORAGE_IMPL") ?: "room"}\"")
    }
}

dependencies {
    // Module Dependency
    implementation(projects.presentation)

    // Network implementation (BuildConfig-based selection)
    val networkImpl = project.findProperty("NETWORK_IMPL") as? String ?: "retrofit"
    when (networkImpl) {
        "retrofit" -> implementation(projects.core.networkRetrofit)
        "ktor" -> implementation(projects.core.networkKtor)
    }

    // Storage implementation (BuildConfig-based selection)
    val storageImpl = project.findProperty("STORAGE_IMPL") as? String ?: "room"
    when (storageImpl) {
        "room" -> implementation(projects.core.storageRoomdb)
        "sqldelight" -> implementation(projects.core.storageSqldelightHilt)
    }

    // Compose BOM (for versionless compose dependencies in test configs)
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(platform(libs.compose.bom))

    // App tests
    androidTestImplementation(libs.bundles.app.testing)
    debugImplementation(libs.ui.test.manifest)
    kspAndroidTest(libs.hilt.ext.compiler)

    // Keep Hilt's processor aligned with Kotlin 2.4.0 metadata.
    ksp(libs.kotlin.metadata.jvm)
}
