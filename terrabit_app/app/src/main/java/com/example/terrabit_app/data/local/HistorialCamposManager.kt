package com.example.terrabit_app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.historialDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "historial_campos")

@Singleton
class HistorialCamposManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.historialDataStore
    private val separador = "||"
    private val maxEntradas = 10

    // Lee el historial de una clave específica (suspending)
    suspend fun obtenerHistorial(clave: String): List<String> {
        val prefKey = stringPreferencesKey(clave)
        val raw = dataStore.data.map { prefs ->
            prefs[prefKey] ?: ""
        }.first()
        return raw.split(separador).filter { it.isNotBlank() }
    }

    // Guarda un nuevo valor al inicio del historial
    suspend fun guardarValor(clave: String, valor: String) {
        if (valor.isBlank()) return
        val prefKey = stringPreferencesKey(clave)
        dataStore.edit { prefs ->
            val actual = (prefs[prefKey] ?: "")
                .split(separador)
                .filter { it.isNotBlank() }
                .toMutableList()
            actual.remove(valor)           // Evita duplicados
            actual.add(0, valor)           // Más reciente primero
            prefs[prefKey] = actual.take(maxEntradas).joinToString(separador)
        }
    }

    // Elimina una entrada concreta del historial
    suspend fun eliminarEntrada(clave: String, valor: String) {
        val prefKey = stringPreferencesKey(clave)
        dataStore.edit { prefs ->
            val actual = (prefs[prefKey] ?: "")
                .split(separador)
                .filter { it.isNotBlank() && it != valor }
            prefs[prefKey] = actual.joinToString(separador)
        }
    }

    // Limpia todo el historial de una clave
    suspend fun limpiarHistorial(clave: String) {
        val prefKey = stringPreferencesKey(clave)
        dataStore.edit { prefs -> prefs.remove(prefKey) }
    }
}