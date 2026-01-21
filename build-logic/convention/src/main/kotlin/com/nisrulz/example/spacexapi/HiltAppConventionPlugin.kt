package com.nisrulz.example.spacexapi

import com.nisrulz.example.spacexapi.ktx.configureHiltApp
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            configureHiltApp()
        }
    }
}
