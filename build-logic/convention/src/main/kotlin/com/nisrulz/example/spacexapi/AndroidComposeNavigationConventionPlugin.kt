package com.nisrulz.example.spacexapi

import com.nisrulz.example.spacexapi.ktx.configureAndroidComposeNavigation
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposeNavigationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureAndroidComposeNavigation()
        }
    }
}
