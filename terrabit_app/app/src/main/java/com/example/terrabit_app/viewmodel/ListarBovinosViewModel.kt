package com.example.terrabit_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import kotlinx.coroutines.launch

class ListarBovinosViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = Repositorio(application)

    private val _listaBovinos = MutableLiveData<List<Animal>>()
    val listaBovinos = _listaBovinos

    private val _listaFiltrada = MutableLiveData<List<Animal>>()
    val listaFiltrada = _listaFiltrada

    private val _cargando = MutableLiveData(false)
    val cargando = _cargando

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

        if (query.isEmpty()) {
            _listaFiltrada.value = lista
        } else {
            _listaFiltrada.value = lista.filter { animal ->
                animal.identificador.contains(query, ignoreCase = true) ||
                        animal.identificadorMare?.contains(query, ignoreCase = true) == true
            }
        }
    }

    fun cargarBovinos(
        nif: String = "S0800608B",
        password: String = "L1855m58",
        tipusVinculacio: String = "1",
        explotacio: String = "1410AK"
    ) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val response = repositorio.getListaBovinos(nif, password, tipusVinculacio, explotacio)

                Log.d("PARSEO", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()

                    Log.d("PARSEO", "Body: $body")
                    Log.d("PARSEO", "Codi: ${body?.codi}")
                    Log.d("PARSEO", "Identificadors size: ${body?.identificadors?.size}")

                    if (body != null && !body.identificadors.isNullOrEmpty()) {
                        _listaBovinos.value = body.identificadors
                        _listaFiltrada.value = body.identificadors
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
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}