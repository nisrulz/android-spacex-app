plugins {
    id("library-convention")
    id("hilt-convention")
    id("testing-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.storage.roomdb"
}

dependencies {
    // Room
    api(libs.bundles.room)
    ksp(libs.room.compiler)
}
