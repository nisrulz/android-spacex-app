pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// For accessing modules as type safe values
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Spacex-API"
include(":app")
include(":common")
include(":data")
include(":domain")
include(":presentation")
