package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.database.toBorrador
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorradorViewModel @Inject constructor(
    private val borradorDao: BorradorDao
) : ViewModel() {

    private val _borradores = MutableLiveData<List<Borrador>>()
    val borradores = _borradores

    private val _textoBusqueda = MutableLiveData("")
    val textoBusqueda = _textoBusqueda

    private val _borradoresFiltrados = MutableLiveData<List<Borrador>>()
    val borradoresFiltrados = _borradoresFiltrados

    private val _borradorIdParaEditar = MutableLiveData<String?>(null)
    val borradorIdParaEditar = _borradorIdParaEditar

    fun cargarBorradores() {
        viewModelScope.launch {
            try {
                val listaBorradores = borradorDao.getAll()
                    .map { it.toBorrador() }
                    .sortedByDescending { "${it.fecha.split("/").reversed().joinToString("")}${it.hora}" }

                _borradores.postValue(listaBorradores)
                val texto = _textoBusqueda.value ?: ""
                if (texto.isBlank()) {
                    _borradoresFiltrados.postValue(listaBorradores)
                } else {
                    _borradoresFiltrados.postValue(listaBorradores.filter { filtrar(it, texto) })
                }
            } catch (e: Exception) {
                Log.e("Error Borradores", "Error al cargar: ${e.message}", e)
                _borradores.postValue(emptyList())
                _borradoresFiltrados.postValue(emptyList())
            }
        }
    }

    fun actualizarBusqueda(texto: String) {
        _textoBusqueda.value = texto
        val lista = _borradores.value ?: emptyList()
        _borradoresFiltrados.value = if (texto.isBlank()) lista else lista.filter { filtrar(it, texto) }
    }

    private fun filtrar(borrador: Borrador, texto: String): Boolean {
        return obtenerNombreTipo(borrador.tipo).contains(texto, ignoreCase = true) ||
                borrador.fecha.contains(texto, ignoreCase = true) ||
                (borrador.hora).contains(texto, ignoreCase = true) ||
                obtenerEstadoLegible(borrador.estado).contains(texto, ignoreCase = true)
    }

    private fun obtenerNombreTipo(tipo: String): String = when (tipo) {
        "MUERTE" -> "Muerte"
        "MATERIAL" -> "Material"
        "NACIMIENTO" -> "Nacimiento"
        "CORRECCION_SEXO" -> "Corrección Sexo"
        "IDENTIFICACION_APLAZADA" -> "ID Aplazada"
        "MATERIAL_DUPLICADO" -> "Material Duplicado"
        "MOVIMIENTO" -> "Movimiento"
        "GUIA" -> "Guia"
        else -> tipo
    }

    private fun obtenerEstadoLegible(estado: String): String = when (estado) {
        "BORRADOR_AUTO" -> "Guardado"
        "PENDIENTE" -> "Pendiente"
        "ENVIANDO" -> "Enviando"
        "ERROR" -> "Error"
        else -> estado
    }

    fun eliminarBorrador(id: String) {
        viewModelScope.launch {
            try {
                borradorDao.deleteById(id)
                cargarBorradores()
                Log.d("Borrador", "Eliminado exitosamente: $id")
            } catch (e: Exception) {
                Log.e("Error Borrador", "Error al eliminar: ${e.message}", e)
            }
        }
    }

    fun eliminarBorradores(ids: Set<String>) {
        viewModelScope.launch {
            try {
                ids.forEach { borradorDao.deleteById(it) }
                cargarBorradores()
                Log.d("Borrador", "Eliminados ${ids.size} borradores")
            } catch (e: Exception) {
                Log.e("Error Borrador", "Error al eliminar seleccionados: ${e.message}", e)
            }
        }
    }

    fun seleccionarBorradorParaEditar(id: String) { _borradorIdParaEditar.value = id }
    fun limpiarBorradorParaEditar() { _borradorIdParaEditar.value = null }

    suspend fun eliminarTodosBorradores() {
        try {
            borradorDao.deleteAll()
            _borradores.postValue(emptyList())
            _borradoresFiltrados.postValue(emptyList())
            Log.d("Borrador", "Todos los borradores eliminados")
        } catch (e: Exception) {
            Log.e("Error Borrador", "Error al eliminar todos: ${e.message}", e)
        }
    }
}