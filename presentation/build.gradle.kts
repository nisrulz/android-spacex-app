@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}
android {
    namespace = "com.nisrulz.example.spacexapi.presentation"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Module Dependency
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(project(":data"))

    // Dagger-Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Material
    implementation(libs.bundles.material)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil
    api(libs.bundles.coil)

    // Kotlin Extensions
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.core)
    implementation(libs.bundles.ktx)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Debug
    debugImplementation(libs.bundles.compose.debug)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.mockk)
    androidTestImplementation(libs.bundles.android.testing)
}
