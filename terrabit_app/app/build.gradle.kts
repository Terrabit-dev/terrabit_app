import java.security.SecureRandom
import java.util.Properties
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// ── Helper: lee una propiedad de local.properties ─────────────────────────
fun loadLocalProp(key: String, default: String = ""): String {
    val f = rootProject.file("local.properties")
    if (!f.exists()) return default
    val props = Properties().apply { f.inputStream().use { load(it) } }
    return props.getProperty(key, default)
}

android {
    namespace = "com.example.terrabit_app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.terrabit_app"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyecta la passphrase de cifrado del local.properties como BuildConfig
        buildConfigField("String", "DEMO_PASSPHRASE", "\"${loadLocalProp("DEMO_PASSPHRASE")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            //ofuscacion de codigo
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// ── Task: cifra demo_credentials.properties → assets/demo_credentials.enc ─
tasks.register("encryptDemoCredentials") {
    group = "terrabit"
    description = "Cifra las credenciales demo y las embebe en assets/demo_credentials.enc"

    val plainFile = rootProject.file("demo_credentials.properties")
    val outFile = file("src/main/assets/demo_credentials.enc")

    inputs.file(plainFile).optional()
    inputs.property("passphrase_present", loadLocalProp("DEMO_PASSPHRASE").isNotEmpty())
    outputs.file(outFile)

    doLast {
        outFile.parentFile.mkdirs()

        if (!plainFile.exists()) {
            outFile.writeBytes(byteArrayOf())
            println("⚠️  demo_credentials.properties no encontrado → botón demo desactivado en este build.")
            return@doLast
        }

        val passphrase = loadLocalProp("DEMO_PASSPHRASE")
        if (passphrase.isEmpty()) {
            outFile.writeBytes(byteArrayOf())
            println("⚠️  DEMO_PASSPHRASE no definido en local.properties → botón demo desactivado.")
            return@doLast
        }

        val demoProps = Properties().apply { plainFile.inputStream().use { load(it) } }
        val nif = demoProps.getProperty("nif").orEmpty()
        val password = demoProps.getProperty("password").orEmpty()
        val codiMO = demoProps.getProperty("codiMO").orEmpty()

        require(nif.isNotEmpty() && password.isNotEmpty() && codiMO.isNotEmpty()) {
            "demo_credentials.properties debe contener: nif, password, codiMO"
        }

        fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
        val json = """{"nif":"${esc(nif)}","password":"${esc(password)}","codiMO":"${esc(codiMO)}"}"""

        val rng = SecureRandom()
        val salt = ByteArray(16).also { rng.nextBytes(it) }
        val iv = ByteArray(12).also { rng.nextBytes(it) }

        val keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(passphrase.toCharArray(), salt, 100_000, 256))
            .encoded
        val secretKey = SecretKeySpec(keyBytes, "AES")

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val ciphertext = cipher.doFinal(json.toByteArray(Charsets.UTF_8))

        // Formato del blob: [salt(16)][iv(12)][ciphertext+tag(16)]
        outFile.writeBytes(salt + iv + ciphertext)
        println("✅ ${outFile.relativeTo(rootProject.rootDir)} generado (${outFile.length()} bytes)")
    }
}

// Encadena encryptDemoCredentials a TODAS las tasks que leen de src/main/assets
// para evitar errores de "implicit dependency" en Gradle 8.13+ (lint, merge, package, etc.)
tasks.matching { task ->
    val n = task.name
    (n.startsWith("merge") && n.endsWith("Assets")) ||
            n.startsWith("lintAnalyze") ||
            n.startsWith("lintVitalAnalyze") ||
            n.startsWith("lintReport") ||
            n.startsWith("lintVitalReport") ||
            n.startsWith("generate") && n.endsWith("Assets") ||
            n.startsWith("package") && n.endsWith("Resources")
}.configureEach { dependsOn("encryptDemoCredentials") }

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation(libs.androidx.appcompat)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // UI & Activity
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.text)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.0")

    // Foundation
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)

    // Material 3 & Icons
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Navigation & LiveData
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")

    // Room
    implementation(libs.androidx.room.ktx)
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit, OkHttp & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.code.gson:gson:2.10.1")

    // Hardware, Ubicación y Datos locales
    implementation(libs.play.services.location)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.github.felHR85:UsbSerial:6.1.0")

    // Tests & Debug
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Seguridad y Encriptacion
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}