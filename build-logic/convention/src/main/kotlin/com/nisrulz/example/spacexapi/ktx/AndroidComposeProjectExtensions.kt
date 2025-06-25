package com.nisrulz.example.spacexapi.ktx

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose() = configure<LibraryExtension> {
    buildFeatures {
        compose = true
    }

    dependencies {
        val bom = catalogLibrary("compose-bom")
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))
        add("implementation", catalogLibrary("ui-tooling-preview"))
        add("debugImplementation", catalogLibrary("ui-tooling"))
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        includeSourceInformation.set(true)
        featureFlags.set(
            setOf(
                ComposeFeatureFlag.OptimizeNonSkippingGroups
            )
        )
    }
}

/**
 * Configure Compose Navigation options
 */
internal fun Project.configureAndroidComposeNavigation() = configure<LibraryExtension> {
    dependencies {
        add("implementation", catalogLibrary("navigation-compose"))
        add("implementation", catalogLibrary("kotlinx-serialization-json"))
    }
}
