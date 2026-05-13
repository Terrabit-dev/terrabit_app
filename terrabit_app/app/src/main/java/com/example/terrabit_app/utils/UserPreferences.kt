package com.example.terrabit_app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.terrabit_app.data.local.SecureStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "terrabit_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secureStorage: SecureStorage
) {
    private val gson = Gson()

    companion object {
        private val KEY_CODI_MO = stringPreferencesKey("codi_mo")
        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        private val KEY_ARDUINO_MAC = stringPreferencesKey("arduino_mac")
        private const val PREFIX_USER_MO_LIST = "mo_list_"
    }

    // ── Credenciales (delegadas a SecureStorage) ──────────────────────────

    /**
     * Guarda las credenciales en SecureStorage SIEMPRE (la sesión activa las necesita
     * para llamar a los endpoints), y registra en DataStore si el usuario quiere
     * "Recordarme" (para futuras aperturas / auto-rellenar tras logout).
     *
     * Nota: la implementación anterior llamaba a clearCredentials() cuando
     * rememberMe era false, lo que borraba las credenciales que acababa de
     * escribir y rompía la sesión activa (p. ej. al cambiar codiMO en pleno uso).
     */
    suspend fun saveCredentials(nif: String, password: String, codiMO: String, rememberMe: Boolean) {
        secureStorage.saveNif(nif)
        secureStorage.savePassword(password)
        secureStorage.saveCodiMO(codiMO)
        context.dataStore.edit { prefs ->
            prefs[KEY_CODI_MO] = codiMO
            prefs[KEY_REMEMBER_ME] = rememberMe
        }
        addMOToUserList(codiMO)
    }

    fun getNif(): String? = secureStorage.getNif()
    fun getPassword(): String? = secureStorage.getPassword()
    fun getCodiMO(): String? = secureStorage.getCodiMO()

    suspend fun getRememberMe(): Boolean =
        context.dataStore.data.map { it[KEY_REMEMBER_ME] ?: false }.first()

    /**
     * Indica si hay una sesión persistente activa que permite saltarse el login.
     * Solo es true si el usuario marcó "Recordarme" Y hay credenciales válidas.
     */
    suspend fun haySesionPersistente(): Boolean {
        val rememberMe = getRememberMe()
        return rememberMe && !getNif().isNullOrEmpty() && !getPassword().isNullOrEmpty()
    }

    // ── Dark theme ────────────────────────────────────────────────────────

    val darkThemeFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_THEME] ?: false }

    suspend fun getDarkTheme(): Boolean = darkThemeFlow.first()

    suspend fun saveDarkTheme(isDark: Boolean) {
        context.dataStore.edit { it[KEY_DARK_THEME] = isDark }
    }

    // ── Arduino MAC ───────────────────────────────────────────────────────

    suspend fun saveArduinoMac(mac: String?) {
        context.dataStore.edit { prefs ->
            if (mac != null) prefs[KEY_ARDUINO_MAC] = mac
            else prefs.remove(KEY_ARDUINO_MAC)
        }
    }

    suspend fun deleteArduinoMac() {
        context.dataStore.edit { it.remove(KEY_ARDUINO_MAC) }
    }

    suspend fun getArduinoMac(): String? =
        context.dataStore.data.map { it[KEY_ARDUINO_MAC] }.first()

    // ── Lista de MOs por usuario ──────────────────────────────────────────

    suspend fun getUserMOList(): ArrayList<String> {
        val currentNif = getNif() ?: return ArrayList()
        val key = stringPreferencesKey("$PREFIX_USER_MO_LIST$currentNif")
        val json = context.dataStore.data.map { it[key] }.first() ?: return ArrayList()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    suspend fun saveUserMOList(list: ArrayList<String>) {
        val currentNif = getNif() ?: return
        val key = stringPreferencesKey("$PREFIX_USER_MO_LIST$currentNif")
        val json = gson.toJson(list)
        context.dataStore.edit { it[key] = json }
    }

    suspend fun addMOToUserList(newMO: String) {
        val list = getUserMOList()
        if (!list.contains(newMO)) {
            list.add(newMO)
            saveUserMOList(list)
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────

    /**
     * Cierra la sesión activa.
     *
     * Distingue dos casos según el flag "Recordarme":
     *  - rememberMe = true:  conserva NIF/password/codiMO en SecureStorage para
     *                        que el LoginViewModel pueda pre-rellenar el formulario
     *                        en el próximo arranque. Borra el flag REMEMBER_ME para
     *                        que NO se haga auto-login (el usuario debe pulsar Acceder).
     *  - rememberMe = false: borra todo. Próximo login mostrará el formulario vacío.
     *
     * En ambos casos conservamos KEY_DARK_THEME para no perder la preferencia de tema.
     */
    suspend fun logout() {
        val rememberMe = getRememberMe()
        val darkTheme = getDarkTheme()

        if (rememberMe) {
            // Conservar credenciales en SecureStorage; solo invalidar la sesión activa
            context.dataStore.edit { prefs ->
                prefs.clear()
                prefs[KEY_DARK_THEME] = darkTheme
                // No persistimos REMEMBER_ME=true; al volver al login se vuelve a
                // marcar manualmente si el usuario quiere mantener la preferencia.
            }
        } else {
            secureStorage.clearAll()
            context.dataStore.edit { prefs ->
                prefs.clear()
                prefs[KEY_DARK_THEME] = darkTheme
            }
        }
    }
}