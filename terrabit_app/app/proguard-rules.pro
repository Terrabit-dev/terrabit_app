# ============================================================
# TerrabitApp - ProGuard / R8 Rules (Release)
# ============================================================

# Atributos imprescindibles para Gson, Retrofit y stack traces
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# ---------- Kotlin ----------
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Metadata { public <methods>; }
# Kotlin reflection (usado por Gson para data classes)
-keep class kotlin.reflect.** { *; }
-keep class kotlin.jvm.internal.** { *; }

# ---------- Coroutines ----------
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**
# Necesario para Retrofit + suspend functions con R8 full-mode
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ---------- Hilt / Dagger ----------
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <fields>;
    @dagger.hilt.* <methods>;
}
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
}

# ---------- ViewModels ----------
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }
-keepclassmembers class * extends androidx.lifecycle.ViewModel { <init>(...); }

# ---------- Room ----------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-dontwarn androidx.room.**

# ---------- Retrofit (reglas oficiales para R8 full-mode) ----------
# https://github.com/square/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking interface retrofit2.http.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Mantén tu interface de API y todos sus métodos
-keep interface com.example.terrabit_app.data.network.ApiInterface { *; }
-dontwarn retrofit2.**

# ---------- OkHttp ----------
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ---------- Gson ----------
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# Mantén tipos genéricos parametrizados de Gson (TypeToken)
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# ---------- Modelos de API (TODO el paquete data.network) ----------
# CRÍTICO: estos son los DTOs que Gson tiene que deserializar.
# Hay que mantener clases + miembros + constructores.
-keep class com.example.terrabit_app.data.network.** { *; }
-keepclassmembers class com.example.terrabit_app.data.network.** {
    <init>(...);
    <fields>;
}
# Por si quedan modelos en otros subpaquetes de data
-keepclassmembers class com.example.terrabit_app.data.** {
    <init>(...);
    <fields>;
}

# ---------- Modelos locales de Room (entities) ----------
-keep class com.example.terrabit_app.data.local.** { *; }

# ---------- AndroidX Security (EncryptedSharedPreferences) ----------
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

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

# ---------- Enums ----------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---------- Eliminar logs en release (seguridad) ----------
# IMPORTANTE: dejamos Log.e activo TEMPORALMENTE para diagnosticar el primer
# release. Cuando confirmes que todo funciona, descomenta también Log.e.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}