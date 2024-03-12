import gradle.kotlin.dsl.accessors._1c35da307f1540a2fdd9273b146bf0a7.android
import gradle.kotlin.dsl.accessors._1c35da307f1540a2fdd9273b146bf0a7.debugImplementation
import gradle.kotlin.dsl.accessors._f498a892b084b6e1620083bdb51439d5.implementation
import org.gradle.kotlin.dsl.dependencies


android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.kotlinCompilerExtensionVersion
    }
}

applyBundle("compose") {
    dependencies {
        implementation(it)
    }
}

applyLibrary("compose-bom") {
    dependencies {
        implementation(platform(it))
    }
}

applyLibrary("compose-debug") {
    dependencies {
        debugImplementation(it)
    }
}
