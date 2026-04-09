package com.example.terrabit_app.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "terrabit_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveNif(nif: String) = encryptedPrefs.edit().putString(KEY_NIF, nif).apply()
    fun savePassword(password: String) = encryptedPrefs.edit().putString(KEY_PASSWORD, password).apply()

    fun saveCodiMO(CodiMO: String) = encryptedPrefs.edit().putString(KEY_CodiMO, CodiMO).apply()



    fun getNif(): String? = encryptedPrefs.getString(KEY_NIF, null)
    fun getPassword(): String? = encryptedPrefs.getString(KEY_PASSWORD, null)
    fun getCodiMO(): String? = encryptedPrefs.getString(KEY_CodiMO, null)

    fun saveDbKey(key: String) = encryptedPrefs.edit().putString(KEY_DB, key).apply()
    fun getDbKey(): String? = encryptedPrefs.getString(KEY_DB, null)

    fun clearCredentials() {
        encryptedPrefs.edit().remove(KEY_NIF).remove(KEY_PASSWORD).remove(KEY_CodiMO).apply()
    }

    fun clearAll() = encryptedPrefs.edit().clear().apply()

    companion object {
        private const val KEY_NIF = "nif"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CodiMO = "CodiMO"
        private const val KEY_DB = "db_encryption_key"
    }
}