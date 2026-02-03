package com.example.terrabit_app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("terrabit_borradores", Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val KEY_BORRADORES = "borradores_list"
    }

    fun guardarBorrador(borrador: Borrador) {
        val borradores = obtenerBorradores().toMutableList()

        // Si ya existe, actualizar
        val index = borradores.indexOfFirst { it.id == borrador.id }
        if (index != -1) {
            borradores[index] = borrador
        } else {
            borradores.add(borrador)
        }

        val json = gson.toJson(borradores)
        sharedPreferences.edit().putString(KEY_BORRADORES, json).apply()
    }

    fun obtenerBorradores(): List<Borrador> {
        val json = sharedPreferences.getString(KEY_BORRADORES, null) ?: return emptyList()
        val type = object : TypeToken<List<Borrador>>() {}.type
        return gson.fromJson(json, type)
    }

    fun eliminarBorrador(id: String) {
        val borradores = obtenerBorradores().toMutableList()
        borradores.removeAll { it.id == id }

        val json = gson.toJson(borradores)
        sharedPreferences.edit().putString(KEY_BORRADORES, json).apply()
    }

    fun limpiarBorradores() {
        sharedPreferences.edit().remove(KEY_BORRADORES).apply()
    }
}