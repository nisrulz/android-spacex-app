import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val VersionCatalog.compileSdk
    get() = findVersionOrThrow("compileSdk").toInt()

internal val VersionCatalog.minSdk
    get() = findVersionOrThrow("minSdk").toInt()

internal val VersionCatalog.targetSdk
    get() = findVersionOrThrow("targetSdk").toInt()

private fun VersionCatalog.findPluginOrThrow(name: String) =
    findPlugin(name)
        .orElseThrow { NoSuchElementException("Plugin $name not found in version catalog") }

private fun VersionCatalog.findBundleOrThrow(name: String) =
    findBundle(name)
        .orElseThrow { NoSuchElementException("Bundle $name not found in version catalog") }

private fun VersionCatalog.findLibraryOrThrow(name: String) =
    findLibrary(name)
        .orElseThrow { NoSuchElementException("Library $name not found in version catalog") }

private fun VersionCatalog.findVersionOrThrow(name: String) =
    findVersion(name)
        .orElseThrow { NoSuchElementException("Version $name not found in version catalog") }
        .requiredVersion
