import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.storage"
}

dependencies {
    api(projects.domain)
    implementation(libs.kotlinx.coroutines.android)
}
