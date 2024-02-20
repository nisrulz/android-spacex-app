import gradle.kotlin.dsl.accessors._624aae704a5c30b505ab3598db099943.androidTestImplementation
import gradle.kotlin.dsl.accessors._624aae704a5c30b505ab3598db099943.testImplementation

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
