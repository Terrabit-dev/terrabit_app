package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MaterialDuplicadoViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""
    val codiMo = userPreferences.getCodiMO() ?: ""

    private val _suggestionsBovinos = MutableLiveData<List<Animal>>(emptyList())
    val suggestionsBovinos = _suggestionsBovinos

    private val _isLoadingBovinos = MutableLiveData(false)
    val isLoadingBovinos = _isLoadingBovinos

    private val _bovinosCargados = MutableLiveData(false)
    val bovinosCargados = _bovinosCargados

    private val _activeFieldIndex = MutableLiveData<Int>(-1)
    val activeFieldIndex = _activeFieldIndex

    private val _listaAnimales = MutableLiveData<List<IdenSolicitudDupli>>(
        listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
    )
    val listaAnimales = _listaAnimales

    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora
    private var codigoEmpresaSubministradora = ""

    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento
    private var codigoTipoEnviamiento = ""

    private val _direccionEnvio = MutableLiveData("")
    val direccionEnvio = _direccionEnvio
    private var codigoDireccionEnvio = ""

    private val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal = _oficinaComarcal
    private var codigoOficinaComarcal = ""

    private val _dirrecionEnvio = MutableLiveData("")
    val dirrecionEnvio = _dirrecionEnvio

    private val _poblacion = MutableLiveData("")
    val poblacion = _poblacion

    private val _codigoPostal = MutableLiveData("")
    val codigoPostal = _codigoPostal

    private val _municipio = MutableLiveData("")
    val municipio = _municipio

    private val _telefonoContacto = MutableLiveData("")
    val telefonoContacto = _telefonoContacto

    private val _tipoMaterialExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipoMaterialExpandidoPorIndice = _tipoMaterialExpandidoPorIndice

    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida = _empresaExpandida

    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido = _tipoEnviamientoExpandido

    private val _direccionEnvioExpandido = MutableLiveData(false)
    val direccionEnvioExpandido = _direccionEnvioExpandido

    private val _oficinaComarcalExpandido = MutableLiveData(false)
    val oficinaComarcalExpandido = _oficinaComarcalExpandido

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    private val _cargando = MutableLiveData(false)
    val cargando = _cargando

    init {
        cargarBovinosEnCache()
    }

    private fun cargarBovinosEnCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingBovinos.postValue(true)
                repositorio.getBovinosWithCache(nif = nif, password = password, tipusVinculacio = "1", explotacio = codiMo, forceRefresh = false)
                _bovinosCargados.postValue(true)
                _isLoadingBovinos.postValue(false)
                Log.d("MaterialDuplicadoVM", "Bovinos cargados en caché")
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("MaterialDuplicadoVM", "Error al cargar bovinos: ${e.message}", e)
            }
        }
    }

    fun searchBovinos(index: Int, query: String) {
        _activeFieldIndex.value = index
        if (query.isBlank()) { _suggestionsBovinos.value = emptyList(); return }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultados = repositorio.searchBovinosLocal(query)
                _suggestionsBovinos.postValue(resultados)
                Log.d("MaterialDuplicadoVM", "Búsqueda en índice $index: '$query' - ${resultados.size} resultados")
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e("MaterialDuplicadoVM", "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    fun onBovinoSelected(index: Int, animal: Animal) {
        actualizarIdentificador(index, animal.identificador)
        _suggestionsBovinos.value = emptyList()
        _activeFieldIndex.value = -1
        Log.d("MaterialDuplicadoVM", "Bovino seleccionado en índice $index: ${animal.identificador}")
    }

    fun actualizarIdentificador(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = identificador) else animal
        }
    }

    fun seleccionarTipoMaterialIdentificador(indice: Int, codigoTipo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(tipusMaterial = codigoTipo) else animal
        }
        _tipoMaterialExpandidoPorIndice.value = (_tipoMaterialExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun toggleTipoMaterialExpandido(indice: Int) {
        val mapa = _tipoMaterialExpandidoPorIndice.value ?: emptyMap()
        _tipoMaterialExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun cerrarTipoMaterialMenu(indice: Int) {
        _tipoMaterialExpandidoPorIndice.value = (_tipoMaterialExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun agregarAnimal() {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()) + IdenSolicitudDupli(identificador = "", tipusMaterial = "")
    }

    fun eliminarAnimal(indice: Int) {
        val lista = _listaAnimales.value ?: emptyList()
        if (lista.size > 1) {
            _listaAnimales.value = lista.filterIndexed { index, _ -> index != indice }
            _tipoMaterialExpandidoPorIndice.value = _tipoMaterialExpandidoPorIndice.value?.minus(indice)
        }
    }

    fun toggleEmpresaExpandida() { _empresaExpandida.value = !(_empresaExpandida.value ?: false) }
    fun toggleTipoEnviamientoExpandido() { _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false) }
    fun toggleDireccionEnvioExpandido() { _direccionEnvioExpandido.value = !(_direccionEnvioExpandido.value ?: false) }
    fun toggleOficinaComarcalExpandido() { _oficinaComarcalExpandido.value = !(_oficinaComarcalExpandido.value ?: false) }
    fun cerrarEmpresaMenu() { _empresaExpandida.value = false }
    fun cerrarTipoEnviamientoMenu() { _tipoEnviamientoExpandido.value = false }
    fun cerrarDireccionEnvioMenu() { _direccionEnvioExpandido.value = false }
    fun cerrarOficinaComarcalMenu() { _oficinaComarcalExpandido.value = false }

    fun seleccionarEmpresa(codigo: String, nombre: String) { _empresaSubministradora.value = nombre; codigoEmpresaSubministradora = codigo; cerrarEmpresaMenu() }
    fun seleccionarTipoEnviamiento(codigo: String, nombre: String) { _tipoEnviamiento.value = nombre; codigoTipoEnviamiento = codigo; cerrarTipoEnviamientoMenu() }

    fun seleccionarDireccionEnvio(codigo: String, nombre: String) {
        _direccionEnvio.value = nombre; codigoDireccionEnvio = codigo; cerrarDireccionEnvioMenu()
        when (codigo) {
            "01" -> { _dirrecionEnvio.value = ""; _poblacion.value = ""; _codigoPostal.value = ""; _municipio.value = ""; _telefonoContacto.value = "" }
            "02", "03" -> { _oficinaComarcal.value = ""; codigoOficinaComarcal = "" }
        }
    }

    fun seleccionarOficinaComarcal(codigo: String, nombre: String) { _oficinaComarcal.value = nombre; codigoOficinaComarcal = codigo; cerrarOficinaComarcalMenu() }

    fun actualizarDireccionEnvio(valor: String) { _dirrecionEnvio.value = valor }
    fun actualizarPoblacion(valor: String) { _poblacion.value = valor }
    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) _codigoPostal.value = valor
    }
    fun actualizarMunicipio(valor: String) { _municipio.value = valor }
    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) _telefonoContacto.value = valor
    }

    fun getCodigoDirecioEnvio(): String = codigoDireccionEnvio

    fun esFormularioValido(): Boolean {
        if (codigoEmpresaSubministradora.isEmpty()) return false
        if (codigoTipoEnviamiento.isEmpty()) return false
        if (codigoDireccionEnvio.isEmpty()) return false
        when (codigoDireccionEnvio) {
            "01" -> if (codigoOficinaComarcal.isEmpty()) return false
            "02" -> { /* opcional */ }
            "03" -> {
                if (_dirrecionEnvio.value.isNullOrEmpty()) return false
                if (_poblacion.value.isNullOrEmpty()) return false
                if (_codigoPostal.value.isNullOrEmpty()) return false
                if (_municipio.value.isNullOrEmpty()) return false
                if (_telefonoContacto.value.isNullOrEmpty()) return false
            }
        }
        val identificadores = _listaAnimales.value ?: return false
        if (identificadores.isEmpty()) return false
        if (identificadores.any { it.identificador.isEmpty() || it.tipusMaterial.isEmpty() }) return false
        return true
    }

    fun solicitarDuplicado() {
        if (!esFormularioValido()) {
            _mensajeError.value = when {
                codigoEmpresaSubministradora.isEmpty() -> "Por favor, seleccione la empresa subministradora"
                codigoTipoEnviamiento.isEmpty() -> "Por favor, seleccione el tipo de envío"
                codigoDireccionEnvio.isEmpty() -> "Por favor, seleccione la dirección de envío"
                codigoDireccionEnvio == "01" && codigoOficinaComarcal.isEmpty() -> "Por favor, seleccione la oficina comarcal"
                codigoDireccionEnvio == "03" && _dirrecionEnvio.value.isNullOrEmpty() -> "Por favor, introduzca la dirección"
                codigoDireccionEnvio == "03" && _poblacion.value.isNullOrEmpty() -> "Por favor, introduzca la población"
                codigoDireccionEnvio == "03" && _codigoPostal.value.isNullOrEmpty() -> "Por favor, introduzca el código postal"
                codigoDireccionEnvio == "03" && _municipio.value.isNullOrEmpty() -> "Por favor, introduzca el municipio"
                codigoDireccionEnvio == "03" && _telefonoContacto.value.isNullOrEmpty() -> "Por favor, introduzca el teléfono de contacto"
                (_listaAnimales.value?.any { it.identificador.isEmpty() } == true) -> "Por favor, complete todos los identificadores"
                (_listaAnimales.value?.any { it.tipusMaterial.isEmpty() } == true) -> "Por favor, seleccione el tipo de material para cada identificador"
                else -> "Por favor, complete todos los campos obligatorios"
            }
            return
        }

        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val request = PetSolicitudDuplicado(
                    nif = nif, passwordMobilitat = password, especie = "01",
                    empresaSubministradora = codigoEmpresaSubministradora,
                    tipusEnviament = codigoTipoEnviamiento,
                    adrecaLliurament = codigoDireccionEnvio,
                    oc = if (codigoDireccionEnvio == "01") codigoOficinaComarcal else null,
                    adreca = if (codigoDireccionEnvio == "03") _dirrecionEnvio.value else null,
                    poblacio = if (codigoDireccionEnvio == "03") _poblacion.value else null,
                    cp = if (codigoDireccionEnvio == "03") _codigoPostal.value else null,
                    municipi = if (codigoDireccionEnvio == "03") _municipio.value else null,
                    telefonContacte = if (codigoDireccionEnvio == "03") _telefonoContacto.value else null,
                    identificadors = _listaAnimales.value ?: emptyList()
                )
                Log.d("Solicitud Duplicado", "Request: $request")
                val response = repositorio.putSolicitudDuplicado(request)
                withContext(Dispatchers.Main) {
                    _cargando.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true; _mensajeError.value = ""
                                Log.d("Solicitud Duplicado", "Exitoso: [${body.codi}] ${body.descripcio}")
                                limpiarFormulario()
                            } else {
                                _registroExitoso.value = false
                                _mensajeError.value = "Error: [${body.codi}] ${body.descripcio}"
                                Log.e("Solicitud Duplicado", "[${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
                                } catch (e: Exception) { _mensajeError.value = "Error al procesar respuesta" }
                            }
                            _registroExitoso.value = false
                            Log.e("Solicitud Duplicado", "HTTP ${response.code()} - $errorBody")
                        }
                        else -> { _registroExitoso.value = false; _mensajeError.value = "Error: Respuesta vacía del servidor" }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargando.value = false; _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. Verifique si la operación se completó."
                    Log.e("Solicitud Duplicado", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargando.value = false; _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Solicitud Duplicado", "IOException: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargando.value = false; _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Solicitud Duplicado", "Error general: ${e.message}", e)
                }
            }
        }
    }

    fun limpiarFormulario() {
        _empresaSubministradora.value = ""; codigoEmpresaSubministradora = ""
        _tipoEnviamiento.value = ""; codigoTipoEnviamiento = ""
        _direccionEnvio.value = ""; codigoDireccionEnvio = ""
        _oficinaComarcal.value = ""; codigoOficinaComarcal = ""
        _dirrecionEnvio.value = ""; _poblacion.value = ""; _codigoPostal.value = ""
        _municipio.value = ""; _telefonoContacto.value = ""
        _listaAnimales.value = listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
        _tipoMaterialExpandidoPorIndice.value = emptyMap()
    }

    fun resetearEstado() { _registroExitoso.value = false; _mensajeError.value = "" }
}