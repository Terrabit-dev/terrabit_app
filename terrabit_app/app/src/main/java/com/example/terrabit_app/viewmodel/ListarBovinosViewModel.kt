package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarBovinosViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""
    val codiMo = userPreferences.getCodiMO() ?: ""

    private val _listaBovinos = MutableLiveData<List<Animal>>()
    val listaBovinos = _listaBovinos

    private val _listaFiltrada = MutableLiveData<List<Animal>>()
    val listaFiltrada = _listaFiltrada

    private val _cargando = MutableLiveData(false)
    val cargando = _cargando

    private val _refrescando = MutableLiveData(false)
    val refrescando = _refrescando

    private val _error = MutableLiveData<String?>()
    val error = _error

    private val _busqueda = MutableLiveData("")
    val busqueda = _busqueda

    fun actualizarBusqueda(texto: String) {
        _busqueda.value = texto
        filtrarBovinos(texto)
    }

    private fun filtrarBovinos(query: String) {
        val lista = _listaBovinos.value ?: emptyList()
        _listaFiltrada.value = if (query.isEmpty()) {
            lista
        } else {
            lista.filter { animal ->
                animal.identificador.contains(query, ignoreCase = true) ||
                        animal.identificadorMare?.contains(query, ignoreCase = true) == true
            }
        }
    }

    fun refrescar() {
        cargarBovinos(esRefresh = true)
    }

    fun cargarBovinos(esRefresh: Boolean = false) {
        viewModelScope.launch {
            if (esRefresh) _refrescando.value = true else _cargando.value = true
            _error.value = null
            try {
                val response = repositorio.getListaBovinos(nif, password, "1", codiMo)
                Log.d("PARSEO", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("PARSEO", "Body: $body")
                    Log.d("PARSEO", "Codi: ${body?.codi}")
                    Log.d("PARSEO", "Identificadors size: ${body?.identificadors?.size}")
                    if (body != null && !body.identificadors.isNullOrEmpty()) {
                        _listaBovinos.value = body.identificadors
                        filtrarBovinos(_busqueda.value ?: "")
                        Log.d("PARSEO", "✅ Lista cargada: ${body.identificadors.size} bovinos")
                    } else {
                        _error.value = "Lista vacía"
                        Log.e("PARSEO", "❌ Lista vacía o null")
                    }
                } else {
                    _error.value = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("PARSEO", "Error: ${e.message}", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _cargando.value = false
                _refrescando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}