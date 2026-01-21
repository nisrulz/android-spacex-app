package com.nisrulz.example.spacexapi.ktx

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.nisrulz.example.spacexapi.info.ApplicationInfo
import com.nisrulz.example.spacexapi.info.BuildSdkInfo
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Set JVM toolchain
 */
private fun Project.configureKotlin() {
    extensions.configure(KotlinAndroidProjectExtension::class.java) {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(BuildSdkInfo.JVM_TARGET.toString()))
        }
        jvmToolchain(BuildSdkInfo.JVM_TARGET)
    }
}


/**
 * Configuration for Android Application
 */
internal fun Project.configureAndroidApp() = configure<ApplicationExtension> {

    compileSdk = BuildSdkInfo.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = BuildSdkInfo.MIN_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        targetSdk = BuildSdkInfo.TARGET_SDK_VERSION
        versionCode = ApplicationInfo.VERSION_CODE
        versionName = ApplicationInfo.VERSION_NAME
        vectorDrawables.useSupportLibrary = true
    }

    configureKotlin()

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

/**
 * Configuration for Android Library
 */
internal fun Project.configureAndroidLibrary() = configure<LibraryExtension> {
    compileSdk = BuildSdkInfo.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = BuildSdkInfo.MIN_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    configureKotlin()

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
