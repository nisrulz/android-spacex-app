package com.nisrulz.example.spacexapi

import com.android.build.api.dsl.LibraryExtension
import com.nisrulz.example.spacexapi.ktx.configureHilt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class HiltLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            extensions.configure<LibraryExtension> {
                configureHilt(this)
            }
        }
    }
}
