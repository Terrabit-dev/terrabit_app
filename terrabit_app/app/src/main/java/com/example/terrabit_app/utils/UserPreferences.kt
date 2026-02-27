package com.example.terrabit_app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "terrabit_prefs"
        private const val KEY_NIF = "nif"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CODI_MO = "codi_mo"
        private const val KEY_REMEMBER_ME = "remember_me"

        private const val KEY_SESSION_NIF = "session_nif"
        private const val KEY_SESSION_PASSWORD = "session_password"
        private const val KEY_SESSION_CODI_MO = "session_codi_mo"
        private const val KEY_SESSION_ACTIVE = "session_active"
        private const val ARDUINO_MAC_KEY = "arduino_mac"
    }

    fun saveCredentials(nif: String, password: String, codiMO: String,rememberMe: Boolean) {
        prefs.edit().apply {
            putString(KEY_SESSION_NIF, nif)
            putString(KEY_SESSION_PASSWORD, password)
            putString(KEY_SESSION_CODI_MO, codiMO)
            putBoolean(KEY_SESSION_ACTIVE, true)
            if (rememberMe) {
                // Guardar también persistente para el próximo arranque
                putString(KEY_NIF, nif)
                putString(KEY_PASSWORD, password)
                putString(KEY_CODI_MO, codiMO)
                putBoolean(KEY_REMEMBER_ME, true)
            } else {
                // Borrar persistente si desmarcó "Recordarme"
                remove(KEY_NIF)
                remove(KEY_PASSWORD)
                remove(KEY_CODI_MO)
                putBoolean(KEY_REMEMBER_ME, false)
            }
            apply()
        }
    }

    fun saveArduinoMac(mac: String?) {
        prefs.edit().apply {
            if (mac != null) putString(ARDUINO_MAC_KEY, mac)
            else remove(ARDUINO_MAC_KEY)
            apply()
        }
    }

    fun getArduinoMac(): String? {
        return prefs.getString(ARDUINO_MAC_KEY, null)
    }


    fun getNif(): String? = prefs.getString(KEY_SESSION_NIF, null)
        ?: prefs.getString(KEY_NIF, null)

    fun getPassword(): String? =  prefs.getString(KEY_SESSION_PASSWORD, null)
        ?: prefs.getString(KEY_PASSWORD, null)

    fun getCodiMO(): String? =  prefs.getString(KEY_SESSION_CODI_MO, null)
        ?: prefs.getString(KEY_CODI_MO, null)

    fun getRememberMe(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)

    fun haySesionPersistente(): Boolean =
        prefs.getBoolean(KEY_REMEMBER_ME, false) &&
                !prefs.getString(KEY_NIF, null).isNullOrEmpty() &&
                !prefs.getString(KEY_PASSWORD, null).isNullOrEmpty()
    fun logout() {
        prefs.edit { clear() }
    }


    fun getDarkTheme(): Boolean = prefs.getBoolean("dark_theme", false)
    fun saveDarkTheme(isDark: Boolean) = prefs.edit().putBoolean("dark_theme", isDark).apply()





}