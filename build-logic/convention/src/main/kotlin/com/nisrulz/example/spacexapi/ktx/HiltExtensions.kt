package com.nisrulz.example.spacexapi.ktx

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureHilt(commonExtension: CommonExtension<*, *, *, *, *, *>) =
    commonExtension.apply {
        dependencies {
            add("implementation", catalogBundle("hilt"))
            add("ksp", catalogLibrary("hilt-compiler"))
        }
    }
