package com.nisrulz.example.spacexapi

import com.nisrulz.example.spacexapi.ktx.configureAndroidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            configureAndroidLibrary()
        }
    }
}
