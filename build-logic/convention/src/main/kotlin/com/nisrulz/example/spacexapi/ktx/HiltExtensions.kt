package com.nisrulz.example.spacexapi.ktx

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies


internal fun Project.configureHiltApp() = configure<ApplicationExtension> {
    dependencies {
        add("implementation", catalogBundle("hilt"))
        add("ksp", catalogLibrary("hilt-compiler"))
    }
}

internal fun Project.configureHiltLibrary() = configure<LibraryExtension> {
    dependencies {
        add("implementation", catalogBundle("hilt"))
        add("ksp", catalogLibrary("hilt-compiler"))
    }
}
