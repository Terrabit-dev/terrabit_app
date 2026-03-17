package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.Historial
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.local.database.toHistorial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val historialDao: HistorialDao
) : ViewModel() {

    private val _historial = MutableLiveData<List<Historial>>()
    val historial = _historial

    private val _historialFiltrado = MutableLiveData<List<Historial>>()
    val historialFiltrado = _historialFiltrado

    private val _textoBusqueda = MutableLiveData("")
    val textoBusqueda = _textoBusqueda

    fun cargarHistorial() {
        viewModelScope.launch {
            try {
                val lista = historialDao.getAll()
                    .map { it.toHistorial() }
                    .sortedByDescending { "${it.fecha.split("/").reversed().joinToString("")}${it.hora}" }
                _historial.postValue(lista)
                val texto = _textoBusqueda.value ?: ""
                _historialFiltrado.postValue(if (texto.isBlank()) lista else lista.filter { filtrar(it, texto) })
            } catch (e: Exception) {
                Log.e("HistorialVM", "Error al cargar: ${e.message}", e)
                _historial.postValue(emptyList())
                _historialFiltrado.postValue(emptyList())
            }
        }
    }

    fun actualizarBusqueda(texto: String) {
        _textoBusqueda.value = texto
        val lista = _historial.value ?: emptyList()
        _historialFiltrado.value = if (texto.isBlank()) lista else lista.filter { filtrar(it, texto) }
    }

    private fun filtrar(historial: Historial, texto: String): Boolean {
        return obtenerNombreTipo(historial.tipo).contains(texto, ignoreCase = true) ||
                historial.fecha.contains(texto, ignoreCase = true) ||
                historial.hora.contains(texto, ignoreCase = true) ||
                historial.resumen.contains(texto, ignoreCase = true)
    }

    private fun obtenerNombreTipo(tipo: String): String = when (tipo) {
        "MUERTE" -> "Muerte"
        "MATERIAL" -> "Material"
        "NACIMIENTO" -> "Nacimiento"
        "CORRECCION_SEXO" -> "Corrección Sexo"
        "IDENTIFICACION_APLAZADA" -> "ID Aplazada"
        "MATERIAL_DUPLICADO" -> "Material Duplicado"
        "MOVIMIENTO" -> "Movimiento"
        "GUIA" -> "Guía"
        "GUIA_PORCINOS" -> "Guía Porcinos"
        else -> tipo
    }

    fun eliminarRegistro(id: String) {
        viewModelScope.launch {
            try {
                historialDao.deleteById(id)
                cargarHistorial()
            } catch (e: Exception) {
                Log.e("HistorialVM", "Error al eliminar: ${e.message}", e)
            }
        }
    }

    suspend fun limpiarHistorial() {
        try {
            historialDao.deleteAll()
            _historial.postValue(emptyList())
            _historialFiltrado.postValue(emptyList())
        } catch (e: Exception) {
            Log.e("HistorialVM", "Error al limpiar: ${e.message}", e)
        }
    }
}