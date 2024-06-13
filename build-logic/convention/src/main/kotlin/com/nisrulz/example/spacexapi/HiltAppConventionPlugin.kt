package com.nisrulz.example.spacexapi

import com.android.build.api.dsl.ApplicationExtension
import com.nisrulz.example.spacexapi.ktx.configureHilt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class HiltAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            extensions.configure<ApplicationExtension> {
                configureHilt(this)
            }
        }
    }
}
