package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import kotlinx.coroutines.launch

class ListarBovinosViewModel : ViewModel() {

    private val repositorio = Repositorio()

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
                Log.d("ListarBovinos", "Iniciando petición...")
                Log.d("ListarBovinos", "NIF: $nif")
                Log.d("ListarBovinos", "Password: $password")
                Log.d("ListarBovinos", "TipusVinculacio: $tipusVinculacio")
                Log.d("ListarBovinos", "Explotacio: $explotacio")

                val response = repositorio.getListaBovinos(nif, password, tipusVinculacio, explotacio)

                Log.d("ListarBovinos", "Response code: ${response.code()}")
                Log.d("ListarBovinos", "Response successful: ${response.isSuccessful}")
                Log.d("ListarBovinos", "Response body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d("ListarBovinos", "Body codi: ${body.codi}")
                    Log.d("ListarBovinos", "Body identificadors: ${body.identificadors}")

                    if (body.codi == "OK") {
                        val lista = body.identificadors ?: emptyList()
                        _listaBovinos.value = lista
                        _listaFiltrada.value = lista
                        Log.d("ListarBovinos", "Bovinos cargados exitosamente: ${lista.size} animales")

                        // Mostrar primeros 3 para debug
                        lista.take(3).forEach {
                            Log.d("ListarBovinos", "Animal: ${it.identificador}")
                        }
                    } else {
                        _error.value = "Error del servidor: ${body.codi}"
                        Log.e("ListarBovinos", "Error codi: ${body.codi}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error HTTP ${response.code()}"
                    Log.e("ListarBovinos", "Error HTTP: ${response.code()}")
                    Log.e("ListarBovinos", "Error body: $errorBody")
                }
            } catch (e: java.net.SocketTimeoutException) {
                _error.value = "Tiempo de espera agotado"
                Log.e("ListarBovinos", "Timeout: ${e.message}", e)
            } catch (e: java.io.IOException) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("ListarBovinos", "IOException: ${e.message}", e)
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("ListarBovinos", "Exception: ${e.message}", e)
                e.printStackTrace()
            } finally {
                _cargando.value = false
                Log.d("ListarBovinos", "Petición finalizada")
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}