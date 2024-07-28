package com.nisrulz.example.spacexapi

import com.nisrulz.example.spacexapi.ktx.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project

class JacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("jacoco")
            }

            configureJacoco()
        }
    }
}
