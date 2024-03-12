import gradle.kotlin.dsl.accessors._f498a892b084b6e1620083bdb51439d5.implementation

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
