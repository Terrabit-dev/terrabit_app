package com.example.terrabit_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.borradorDataStore: DataStore<Preferences> by preferencesDataStore(name = "terrabit_borradores")

@Singleton
class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    companion object {
        private val KEY_BORRADORES = stringPreferencesKey("borradores_list")
    }

    suspend fun guardarBorrador(borrador: Borrador) {
        val borradores = obtenerBorradores().toMutableList()
        val index = borradores.indexOfFirst { it.id == borrador.id }
        if (index != -1) borradores[index] = borrador
        else borradores.add(borrador)
        val json = gson.toJson(borradores)
        context.borradorDataStore.edit { it[KEY_BORRADORES] = json }
    }

    suspend fun obtenerBorradores(): List<Borrador> {
        val json = context.borradorDataStore.data.map { it[KEY_BORRADORES] }.first()
            ?: return emptyList()
        val type = object : TypeToken<List<Borrador>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun eliminarBorrador(id: String) {
        val borradores = obtenerBorradores().toMutableList()
        borradores.removeAll { it.id == id }
        val json = gson.toJson(borradores)
        context.borradorDataStore.edit { it[KEY_BORRADORES] = json }
    }

    suspend fun limpiarBorradores() {
        context.borradorDataStore.edit { it.remove(KEY_BORRADORES) }
    }
}