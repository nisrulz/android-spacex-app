package com.nisrulz.example.spacexapi.ktx

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureTesting() = configure<LibraryExtension> {
    dependencies {
        add("testImplementation", catalogBundle("testing"))
        add("testImplementation", catalogBundle("mockk"))
        add("androidTestImplementation", catalogBundle("android-testing"))
    }
}
