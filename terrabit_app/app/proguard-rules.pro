# ============================================================
# TerrabitApp - ProGuard / R8 Rules
# ============================================================

# ---------- Kotlin ----------
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ---------- Hilt / Dagger ----------
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <fields>;
    @dagger.hilt.* <methods>;
}
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
}

# ---------- ViewModels ----------
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ---------- Room ----------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-dontwarn androidx.room.**

# ---------- Retrofit + OkHttp + Gson ----------
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# ---------- Modelos de API (data classes con Gson/Retrofit) ----------
-keep class com.example.terrabit_app.data.model.** { *; }
-keep class com.example.terrabit_app.data.remote.** { *; }
-keepclassmembers class com.example.terrabit_app.data.** {
    <fields>;
}

# ---------- DataStore ----------
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ---------- UsbSerial (felHR85) ----------
-keep class com.felhr.usbserial.** { *; }
-dontwarn com.felhr.**

# ---------- Google Play Services Location ----------
-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.**

# ---------- Jetpack Compose + Navigation ----------
-dontwarn androidx.compose.**
-dontwarn androidx.navigation.**

# ---------- Coroutines ----------
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ---------- Enums ----------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---------- Eliminar logs en release (seguridad) ----------
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# ---------- Stack traces legibles en crash reports ----------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile