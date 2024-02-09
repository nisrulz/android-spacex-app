import gradle.kotlin.dsl.accessors._624aae704a5c30b505ab3598db099943.implementation

plugins {
    id("com.google.devtools.ksp")
}

applyPlugin("ksp") { plugins { alias(it) } }
applyPlugin("hilt") { plugins { alias(it) } }

applyBundle("hilt") {
    dependencies {
        implementation(it)
    }
}

applyLibrary("hilt-compiler") {
    dependencies {
        ksp(it)
    }
}
