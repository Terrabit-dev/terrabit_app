package com.example.terrabit_app.utils

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "terrabit_prefs"
        private const val KEY_NIF = "nif"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CODI_MO = "codi_mo"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    fun saveCredentials(nif: String, password: String, codiMO: String) {
        prefs.edit().apply {
            putString(KEY_NIF, nif)
            putString(KEY_PASSWORD, password)
            putString(KEY_CODI_MO, codiMO)
            putBoolean(KEY_REMEMBER_ME, true)
            apply()
        }
    }

    fun clearCredentials() {
        prefs.edit().apply {
            remove(KEY_NIF)
            remove(KEY_PASSWORD)
            remove(KEY_CODI_MO)
            putBoolean(KEY_REMEMBER_ME, false)
            apply()
        }
    }

    fun getNif(): String? = prefs.getString(KEY_NIF, null)

    fun getPassword(): String? = prefs.getString(KEY_PASSWORD, null)

    fun getCodiMO(): String? = prefs.getString(KEY_CODI_MO, null)

    fun getRememberMe(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)
}