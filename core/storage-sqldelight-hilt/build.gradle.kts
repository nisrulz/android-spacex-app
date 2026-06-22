import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.storage.sqldelight.hilt"
}

dependencies {
    api(projects.core.storageSqldelight)
    implementation(libs.sqldelight.android.driver)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
