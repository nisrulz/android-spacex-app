import gradle.kotlin.dsl.accessors._1c35da307f1540a2fdd9273b146bf0a7.androidTestImplementation
import gradle.kotlin.dsl.accessors._1c35da307f1540a2fdd9273b146bf0a7.testImplementation

applyBundle("testing") {
    dependencies {
        testImplementation(it)
    }
}

applyBundle("mockk") {
    dependencies {
        testImplementation(it)
    }
}

applyBundle("android-testing") {
    dependencies {
        androidTestImplementation(it)
    }
}
