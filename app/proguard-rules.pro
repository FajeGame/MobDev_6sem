-keep class com.example.semka_6sem.data.remote.dto.** { *; }
-keep class com.example.semka_6sem.data.local.entity.** { *; }
-keepattributes *Annotation*
-keepclasseswithmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep class com.yandex.** { *; }
