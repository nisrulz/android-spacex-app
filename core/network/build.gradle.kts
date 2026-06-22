import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.network"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
