# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Room Database & Entities
-keep class com.rootminusone8004.bazarnote.data.** { *; }
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# OpenCSV
-keep class com.opencsv.** { *; }
-dontwarn com.opencsv.**
-dontwarn org.apache.commons.beanutils.**
-dontwarn org.apache.commons.collections.**
-dontwarn org.apache.commons.collections4.**
-dontwarn org.apache.commons.logging.**

# TapTargetView
-keep class com.getkeepsafe.taptargetview.** { *; }

# ViewBinding
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static * inflate(...);
    public static * bind(...);
}
