# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Firebase Realtime Database
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.database.** { *; }

# Keep remote data classes for Firebase deserialization
-keep class com.adriantache.projecttracker.data.remote.model.RemoteProject { *; }
-keep class com.adriantache.projecttracker.data.remote.model.RemoteCategory { *; }
-keep class com.adriantache.projecttracker.data.remote.model.RemoteTask { *; }

# Also keep anything annotated with @Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <methods>;
}

# Hilt rules
-keep class dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep @dagger.hilt.internal.GeneratedEntryPoint class *
-keep @dagger.hilt.InstallIn class *
-keep @dagger.hilt.EntryPoint class *

# For Hilt view models
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
