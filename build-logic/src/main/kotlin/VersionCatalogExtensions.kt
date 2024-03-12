import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.use.PluginDependency

private val Project.catalog
    get() = extensions.getByType<VersionCatalogsExtension>()

internal val Project.libs: VersionCatalog
    get() = catalog.named("libs")

internal val VersionCatalog.compileSdk
    get() = findVersionOrThrow("compileSdk").toInt()

internal val VersionCatalog.minSdk
    get() = findVersionOrThrow("minSdk").toInt()

internal val VersionCatalog.targetSdk
    get() = findVersionOrThrow("targetSdk").toInt()

internal val VersionCatalog.kotlinCompilerExtensionVersion
    get() = findVersionOrThrow("composeCompiler").toString()

private fun VersionCatalog.findVersionOrThrow(name: String) = findVersion(name)
    .orElseThrow { NoSuchElementException("Version $name not found in version catalog") }
    .requiredVersion

internal fun Project.applyPlugin(alias: String, block: (Provider<PluginDependency>) -> Unit) {
    libs.findPlugin(alias).ifPresent { plugin ->
        block(plugin)
    }
}

internal fun Project.applyBundle(
    alias: String,
    block: (Provider<ExternalModuleDependencyBundle>) -> Unit
) {
    libs.findBundle(alias).ifPresent { bundle ->
        block(bundle)
    }
}

internal fun Project.applyLibrary(
    alias: String,
    block: (Provider<MinimalExternalModuleDependency>) -> Unit
) {
    libs.findLibrary(alias).ifPresent { lib ->
        block(lib)
    }
}
