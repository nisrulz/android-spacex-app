package com.nisrulz.example.spacexapi

import com.nisrulz.example.spacexapi.ktx.configureHiltLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            configureHiltLibrary()
        }
    }
}
