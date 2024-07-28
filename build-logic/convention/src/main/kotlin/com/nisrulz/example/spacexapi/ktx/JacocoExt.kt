package com.nisrulz.example.spacexapi.ktx


import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Locale

private val coverageExclusions = listOf(
    // android
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    // dagger
    "**/*_MembersInjector.class",
    "**/Dagger*Component.class",
    "**/Dagger*Subcomponent*.class",
    "**/*Module_*Factory.class",
    "**/di/module/*",
    "**/*_Factory*.*",
    "**/*Module*.*",
    "**/*Dagger*.*",
    "**/*Hilt*.*",
    // kotlin
    "**/*MapperImpl*.*",
    "**/BuildConfig.*",
    "**/*Component*.*",
    "**/*BR*.*",
    "**/Manifest*.*",
    "**/*Companion*.*",
    "**/*Module*.*",
    "**/*Dagger*.*",
    "**/*Hilt*.*",
    "**/*MembersInjector*.*",
    "**/*_MembersInjector.class",
    "**/*_Factory*.*",
    "**/*_Provide*Factory*.*",
    "**/*Extensions*.*",
    // sealed and data classes
    "**/*$Result.*",
    "**/*$Result$*.*",
    // adapters generated by moshi
    "**/*JsonAdapter.*",
)

private fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

internal fun Project.configureJacoco() {
    val androidComponentsExtension = extensions.getByType(AndroidComponentsExtension::class.java)

    configure<JacocoPluginExtension> {
        toolVersion = catalogVersion("jacoco")
    }
    val jacocoTestReport = tasks.create("jacocoTestReport") {
        group = "reporting"
    }

    androidComponentsExtension.onVariants { variant ->
        val testTaskName = "test${variant.name.capitalize()}UnitTest"
        val reportTask =
            tasks.register("jacoco${testTaskName.capitalize()}Report", JacocoReport::class.java) {
                group = "reporting"
                dependsOn(testTaskName)
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }

                val filesFilters = layout.buildDirectory.dir("tmp/kotlin-classes/${variant.name}")
                    .get().asFileTree.matching {
                        exclude(coverageExclusions)
                    }
                classDirectories.setFrom(filesFilters)
                sourceDirectories.setFrom(
                    files(
                        "$projectDir/src/main/java", "$projectDir/src/main/kotlin"
                    )
                )
                val executionDataVariant =
                    layout.buildDirectory.file("/outputs/unit_test_code_coverage/${variant.name}UnitTest/${testTaskName}.exec")
                        .get().asFile
                executionData.setFrom("${layout.buildDirectory.get()}$executionDataVariant")
            }

        jacocoTestReport.dependsOn(reportTask)
    }

    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            // Required for JaCoCo + Robolectric
            // https://github.com/robolectric/robolectric/issues/2230
            // Consider removing if not we don't add Robolectric
            isIncludeNoLocationClasses = true

            // Required for JDK 11 with the above
            // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
            excludes = listOf("jdk.internal.*")
        }
    }

}