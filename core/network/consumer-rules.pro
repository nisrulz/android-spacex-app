# Keep @Serializable DTOs for kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep serializable classes in the network DTO package
-keepclassmembers class com.nisrulz.example.spacexapi.network.dto.** {
    *** Companion;
}
-keepclasseswithmembers class com.nisrulz.example.spacexapi.network.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}
