plugins {
    `kotlin-dsl`
}

// Note: Replace with your package name
group = "com.nisrulz.example.spacexapi"

// https://kotlinlang.org/docs/gradle-configure-project.html#gradle-java-toolchains-support
// Note: Setting a toolchain via the kotlin extension updates the toolchain for Java compile
// tasks as well.
kotlin {
    jvmToolchain(17)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    // Android
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle.plugin)

    // Compose
    compileOnly(libs.compose.gradle.plugin)

    // Hilt
    compileOnly(libs.symbol.processing.gradle.plugin)
}

// Register Convention Plugins
gradlePlugin {
    plugins {

        register("androidApplication") {
            id = "spacexapi.android.application"
            implementationClass = "com.nisrulz.example.spacexapi.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "spacexapi.android.library"
            implementationClass = "com.nisrulz.example.spacexapi.AndroidLibraryConventionPlugin"
        }

        register("testing") {
            id = "spacexapi.android.testing"
            implementationClass =
                "com.nisrulz.example.spacexapi.TestingConventionPlugin"
        }

        register("jacoco") {
            id = "spacexapi.android.jacoco"
            implementationClass =
                "com.nisrulz.example.spacexapi.JacocoConventionPlugin"
        }

        register("hiltApp") {
            id = "spacexapi.android.app.hilt"
            implementationClass =
                "com.nisrulz.example.spacexapi.HiltAppConventionPlugin"
        }

        register("hiltLib") {
            id = "spacexapi.android.lib.hilt"
            implementationClass =
                "com.nisrulz.example.spacexapi.HiltLibConventionPlugin"
        }

        register("androidCompose") {
            id = "spacexapi.android.compose"
            implementationClass =
                "com.nisrulz.example.spacexapi.AndroidComposeConventionPlugin"
        }

        register("androidComposeNav") {
            id = "spacexapi.android.compose.navigation"
            implementationClass =
                "com.nisrulz.example.spacexapi.AndroidComposeNavigationConventionPlugin"
        }
    }
}
