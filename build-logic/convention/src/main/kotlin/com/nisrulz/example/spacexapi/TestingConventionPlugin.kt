package com.nisrulz.example.spacexapi

import com.nisrulz.example.spacexapi.ktx.configureTesting
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            configureTesting()
        }
    }
}
