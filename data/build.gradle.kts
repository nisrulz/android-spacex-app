@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nisrulz.example.spacexapi.data"

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
        buildConfig = true
    }

    sourceSets {
        named("test") {
            resources.srcDirs("src/test/resources")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Module Dependency
    implementation(project(":domain"))

    // Dagger-Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.retrofit)
    testImplementation(libs.okhttp.mockwebserver)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)

    // Room
    api(libs.bundles.room)
    ksp(libs.room.compiler)

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.mockk)
    androidTestImplementation(libs.bundles.android.testing)
}
