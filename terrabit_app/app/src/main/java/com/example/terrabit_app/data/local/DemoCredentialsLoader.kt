package com.example.terrabit_app.data.local

import android.content.Context
import android.util.Log
import com.example.terrabit_app.BuildConfig
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoCredentialsLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class DemoCredentials(
        val nif: String,
        val password: String,
        val codiMO: String
    )

    private val gson = Gson()

    fun isAvailable(): Boolean {
        return try {
            val list = context.assets.list("") ?: return false
            if (!list.contains(ASSET_NAME)) {
                Log.w(TAG, "isAvailable: asset $ASSET_NAME no existe")
                return false
            }
            val hasContent = context.assets.open(ASSET_NAME).use { it.read() != -1 }
            Log.d(TAG, "isAvailable: $hasContent")
            hasContent
        } catch (e: Exception) {
            Log.e(TAG, "isAvailable error: ${e.message}", e)
            false
        }
    }

    suspend fun load(): DemoCredentials? = withContext(Dispatchers.IO) {
        try {
            if (BuildConfig.DEMO_PASSPHRASE.isEmpty()) {
                Log.e(TAG, "❌ BuildConfig.DEMO_PASSPHRASE está VACÍO. " +
                        "Revisa local.properties y haz Sync Gradle + Rebuild.")
                return@withContext null
            }
            Log.d(TAG, "✓ DEMO_PASSPHRASE presente (${BuildConfig.DEMO_PASSPHRASE.length} chars)")

            val list = context.assets.list("") ?: emptyArray()
            if (!list.contains(ASSET_NAME)) {
                Log.e(TAG, "❌ Asset '$ASSET_NAME' NO existe en el APK. " +
                        "Ejecuta: ./gradlew :app:encryptDemoCredentials")
                return@withContext null
            }

            val blob = context.assets.open(ASSET_NAME).use { it.readBytes() }
            Log.d(TAG, "✓ Blob leído: ${blob.size} bytes")

            if (blob.size <= SALT_LEN + IV_LEN) {
                Log.e(TAG, "❌ Blob demasiado pequeño (${blob.size} bytes). " +
                        "El archivo .enc está vacío o corrupto. Mira el output de la task " +
                        "encryptDemoCredentials en la consola de Gradle.")
                return@withContext null
            }

            val salt = blob.copyOfRange(0, SALT_LEN)
            val iv = blob.copyOfRange(SALT_LEN, SALT_LEN + IV_LEN)
            val ciphertext = blob.copyOfRange(SALT_LEN + IV_LEN, blob.size)

            val key = deriveKey(BuildConfig.DEMO_PASSPHRASE.toCharArray(), salt)
            val plaintext = decrypt(key, iv, ciphertext)
            val json = String(plaintext, Charsets.UTF_8)
            Log.d(TAG, "✓ Descifrado OK, JSON length=${json.length}")

            val creds = gson.fromJson(json, DemoCredentials::class.java)
            Log.d(TAG, "✓ Parsed: nif='${creds.nif.take(2)}***' " +
                    "password=${creds.password.length}chars " +
                    "codiMO='${creds.codiMO.take(2)}***'")
            creds
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cargando credenciales demo: ${e.javaClass.simpleName} - ${e.message}", e)
            null
        }
    }

    private fun deriveKey(passphrase: CharArray, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, KEY_BITS)
        val keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun decrypt(key: SecretKey, iv: ByteArray, ciphertext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(ciphertext)
    }

    companion object {
        private const val TAG = "DemoLoader"
        private const val ASSET_NAME = "demo_credentials.enc"
        private const val SALT_LEN = 16
        private const val IV_LEN = 12
        private const val KEY_BITS = 256
        private const val GCM_TAG_BITS = 128
        private const val PBKDF2_ITERATIONS = 100_000
    }
}