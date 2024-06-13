import com.nisrulz.example.spacexapi.info.ApplicationInfo

plugins {
    alias(libs.plugins.spacexapi.android.library)
    alias(libs.plugins.spacexapi.android.lib.hilt)
    alias(libs.plugins.spacexapi.android.testing)
}

android {
    namespace = "${ApplicationInfo.BASE_NAMESPACE}.domain"
}
