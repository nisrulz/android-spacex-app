@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("library-convention")
    id("hilt-convention")
    id("testing-convention")
}

android {
    namespace = "com.nisrulz.example.spacexapi.domain"
}
