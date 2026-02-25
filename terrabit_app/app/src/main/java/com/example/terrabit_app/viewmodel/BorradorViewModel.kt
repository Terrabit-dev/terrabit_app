package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import kotlinx.coroutines.launch


class BorradorViewModel : ViewModel() {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)
    }

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
                if (!::sharedPreferencesManager.isInitialized) {
                    Log.e("BorradorViewModel", "SharedPreferencesManager no inicializado")
                    _borradores.postValue(emptyList())
                    _borradoresFiltrados.postValue(emptyList())
                    return@launch
                }
                val listaBorradores = sharedPreferencesManager.obtenerBorradores()
                _borradores.postValue(listaBorradores)

                val texto = _textoBusqueda.value ?: ""
                if (texto.isBlank()) {
                    _borradoresFiltrados.postValue(listaBorradores)
                } else {
                    _borradoresFiltrados.postValue(listaBorradores.filter { borrador ->
                        obtenerNombreTipo(borrador.tipo).contains(texto, ignoreCase = true) ||
                                borrador.fecha.contains(texto, ignoreCase = true) ||
                                (borrador.hora ?: "").contains(texto, ignoreCase = true) ||
                                obtenerEstadoLegible(borrador.estado).contains(texto, ignoreCase = true)
                    })
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
        filtrarBorradores(texto)
    }

    private fun filtrarBorradores(texto: String) {
        val lista = _borradores.value ?: emptyList()
        if (texto.isBlank()) {
            _borradoresFiltrados.value = lista
        } else {
            _borradoresFiltrados.value = lista.filter { borrador ->
                obtenerNombreTipo(borrador.tipo).contains(texto, ignoreCase = true) ||
                        borrador.fecha.contains(texto, ignoreCase = true) ||
                        (borrador.hora ?: "").contains(texto, ignoreCase = true) ||
                        obtenerEstadoLegible(borrador.estado).contains(texto, ignoreCase = true)
            }
        }
    }

    private fun obtenerNombreTipo(tipo: String): String {
        return when (tipo) {
            "MUERTE" -> "Muerte"
            "MATERIAL" -> "Material"
            "NACIMIENTO" -> "Nacimiento"
            "CORRECCION_SEXO" -> "Corrección Sexo"
            "IDENTIFICACION_APLAZADA" -> "ID Aplazada"
            else -> tipo
        }
    }

    private fun obtenerEstadoLegible(estado: String): String {
        return when (estado) {
            "BORRADOR_AUTO" -> "Guardado"
            "PENDIENTE" -> "Pendiente"
            "ENVIANDO" -> "Enviando"
            "ERROR" -> "Error"
            else -> estado
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


    fun seleccionarBorradorParaEditar(id: String) {
        _borradorIdParaEditar.value = id
    }

    fun limpiarBorradorParaEditar() {
        _borradorIdParaEditar.value = null
    }

    suspend fun eliminarTodosBorradores() {
        try {
            val listaBorradores = sharedPreferencesManager.obtenerBorradores()
            listaBorradores.forEach { borrador ->
                sharedPreferencesManager.eliminarBorrador(borrador.id)
            }
            val listaVacia = sharedPreferencesManager.obtenerBorradores()
            _borradores.postValue(listaVacia)
            _borradoresFiltrados.postValue(listaVacia)
            Log.d("Borrador", "Todos los borradores eliminados")
        } catch (e: Exception) {
            Log.e("Error Borrador", "Error al eliminar todos: ${e.message}", e)
        }
    }

}