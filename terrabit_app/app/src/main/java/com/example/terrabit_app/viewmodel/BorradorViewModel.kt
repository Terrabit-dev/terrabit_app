package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BorradorViewModel : ViewModel() {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)
    }

    private val _borradores = MutableLiveData<List<Borrador>>()
    val borradores = _borradores

    fun cargarBorradores() {
        viewModelScope.launch {
            try {
                val listaBorradores = sharedPreferencesManager.obtenerBorradores()
                _borradores.postValue(listaBorradores)
            } catch (e: Exception) {
                Log.e("Error Borradores", "Error al cargar: ${e.message}", e)
                _borradores.postValue(emptyList())
            }
        }
    }

    fun guardarBorradorMuerte(
        tipo: String?,
        identificador: String?,
        fecha: String?,
        mesesGestacion: String?,
        cadaverInaccesible: Boolean?,
        coordenadaX: String?,
        coordenadaY: String?
    ) {
        try {
            val datosMuerte = mapOf(
                "tipo" to tipo,
                "identificador" to identificador,
                "fecha" to fecha,
                "mesesGestacion" to mesesGestacion,
                "cadaverInaccesible" to cadaverInaccesible,
                "coordenadaX" to coordenadaX,
                "coordenadaY" to coordenadaY
            )

            val borrador = Borrador(
                id = "muerte_${System.currentTimeMillis()}",
                tipo = "MUERTE",
                fecha = fecha ?: "",
                datos = Gson().toJson(datosMuerte),
                estado = "BORRADOR_AUTO"
            )

            sharedPreferencesManager.guardarBorrador(borrador)
            cargarBorradores()

            Log.d("Borrador Muerte", "Guardado exitosamente")
        } catch (e: Exception) {
            Log.e("Error Borrador Muerte", "Error al guardar: ${e.message}", e)
        }
    }

    fun guardarBorradorMaterial(
        empresaSubministradora: String?,
        codigoEmpresa: String?,
        tipoEnviamiento: String?,
        destinoLliurament: String?,
        oficinaComarcal: String?,
        direccion: String?,
        poblacion: String?,
        codigoPostal: String?,
        municipio: String?,
        telefonoContacto: String?,
        identificadorMaterial: String?,
        tipoMaterial: String?,
        numeroUnidades: String?,
        codigoExplotacion: String?
    ) {
        try {
            val datosMaterial = mapOf(
                "empresaSubministradora" to empresaSubministradora,
                "codigoEmpresa" to codigoEmpresa,
                "tipoEnviamiento" to tipoEnviamiento,
                "destinoLliurament" to destinoLliurament,
                "oficinaComarcal" to oficinaComarcal,
                "direccion" to direccion,
                "poblacion" to poblacion,
                "codigoPostal" to codigoPostal,
                "municipio" to municipio,
                "telefonoContacto" to telefonoContacto,
                "identificadorMaterial" to identificadorMaterial,
                "tipoMaterial" to tipoMaterial,
                "numeroUnidades" to numeroUnidades,
                "codigoExplotacion" to codigoExplotacion
            )

            val borrador = Borrador(
                id = "material_${System.currentTimeMillis()}",
                tipo = "MATERIAL",
                fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                datos = Gson().toJson(datosMaterial),
                estado = "BORRADOR_AUTO"
            )

            sharedPreferencesManager.guardarBorrador(borrador)
            cargarBorradores()

            Log.d("Borrador Material", "Guardado exitosamente")
        } catch (e: Exception) {
            Log.e("Error Borrador Material", "Error al guardar: ${e.message}", e)
        }
    }

    fun eliminarBorrador(id: String) {
        viewModelScope.launch {
            try {
                sharedPreferencesManager.eliminarBorrador(id)
                cargarBorradores()
                Log.d("Borrador", "Eliminado exitosamente: $id")
            } catch (e: Exception) {
                Log.e("Error Borrador", "Error al eliminar: ${e.message}", e)
            }
        }
    }

    fun actualizarEstadoBorrador(id: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                val borradorActual = sharedPreferencesManager.obtenerBorradores()
                    .find { it.id == id }

                borradorActual?.let {
                    val borradorActualizado = it.copy(estado = nuevoEstado)
                    sharedPreferencesManager.guardarBorrador(borradorActualizado)
                    cargarBorradores()
                    Log.d("Borrador", "Estado actualizado: $id -> $nuevoEstado")
                }
            } catch (e: Exception) {
                Log.e("Error Borrador", "Error al actualizar estado: ${e.message}", e)
            }
        }
    }

    fun obtenerBorrador(id: String): Borrador? {
        return try {
            sharedPreferencesManager.obtenerBorradores().find { it.id == id }
        } catch (e: Exception) {
            Log.e("Error Borrador", "Error al obtener borrador: ${e.message}", e)
            null
        }
    }
}