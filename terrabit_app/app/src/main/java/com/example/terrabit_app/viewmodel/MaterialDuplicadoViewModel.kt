package com.example.terrabit_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaterialDuplicadoViewModel(application: Application) : AndroidViewModel(application) {

    private var repositorio = Repositorio(application)
    private val userPreferences = UserPreferences(application)

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""

    // ============================================
    // ESTADOS DEL FORMULARIO
    // ============================================

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

    // ============================================
    // LISTA DE IDENTIFICADORES (soporte múltiple)
    // ============================================

    private val _listaIdentificadores = MutableLiveData<List<IdenSolicitudDupli>>(
        listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
    )
    val listaIdentificadores = _listaIdentificadores

    // Mapa para controlar la expansión del dropdown de tipo material por índice
    private val _tipoMaterialExpandido = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipoMaterialExpandido = _tipoMaterialExpandido

    fun agregarIdentificador() {
        val listaActual = _listaIdentificadores.value?.toMutableList() ?: mutableListOf()
        listaActual.add(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
        _listaIdentificadores.value = listaActual
    }

    fun eliminarIdentificador(indice: Int) {
        val listaActual = _listaIdentificadores.value?.toMutableList() ?: return
        if (listaActual.size > 1) {
            listaActual.removeAt(indice)
            // Limpiar estado de expansión del índice eliminado
            val mapaActual = _tipoMaterialExpandido.value?.toMutableMap() ?: mutableMapOf()
            mapaActual.remove(indice)
            _tipoMaterialExpandido.value = mapaActual
            _listaIdentificadores.value = listaActual
        }
    }

    fun actualizarIdentificador(indice: Int, valor: String) {
        val listaActual = _listaIdentificadores.value?.toMutableList() ?: return
        if (indice < listaActual.size) {
            listaActual[indice] = listaActual[indice].copy(identificador = valor)
            _listaIdentificadores.value = listaActual
        }
    }

    fun seleccionarTipoMaterialIdentificador(indice: Int, codigoTipo: String) {
        val listaActual = _listaIdentificadores.value?.toMutableList() ?: return
        if (indice < listaActual.size) {
            listaActual[indice] = listaActual[indice].copy(tipusMaterial = codigoTipo)
            _listaIdentificadores.value = listaActual
            cerrarTipoMaterialMenu(indice)
        }
    }

    fun toggleTipoMaterialExpandido(indice: Int) {
        val mapaActual = _tipoMaterialExpandido.value ?: emptyMap()
        val valorActual = mapaActual[indice] ?: false
        _tipoMaterialExpandido.value = mapaActual + (indice to !valorActual)
    }

    fun cerrarTipoMaterialMenu(indice: Int) {
        val mapaActual = _tipoMaterialExpandido.value ?: emptyMap()
        _tipoMaterialExpandido.value = mapaActual + (indice to false)
    }

    // ============================================
    // ESTADOS DE EXPANSIÓN DE MENÚS
    // ============================================

    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida = _empresaExpandida

    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido = _tipoEnviamientoExpandido

    private val _direccionEnvioExpandido = MutableLiveData(false)
    val direccionEnvioExpandido = _direccionEnvioExpandido

    private val _oficinaComarcalExpandido = MutableLiveData(false)
    val oficinaComarcalExpandido = _oficinaComarcalExpandido

    // ============================================
    // ESTADOS DE FEEDBACK
    // ============================================

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    private val _cargando = MutableLiveData(false)
    val cargando = _cargando

    // ============================================
    // FUNCIONES DE EXPANSIÓN DE MENÚS
    // ============================================

    fun toggleEmpresaExpandida() {
        _empresaExpandida.value = !(_empresaExpandida.value ?: false)
    }

    fun toggleTipoEnviamientoExpandido() {
        _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false)
    }

    fun toggleDireccionEnvioExpandido() {
        _direccionEnvioExpandido.value = !(_direccionEnvioExpandido.value ?: false)
    }

    fun toggleOficinaComarcalExpandido() {
        _oficinaComarcalExpandido.value = !(_oficinaComarcalExpandido.value ?: false)
    }

    fun cerrarEmpresaMenu() { _empresaExpandida.value = false }
    fun cerrarTipoEnviamientoMenu() { _tipoEnviamientoExpandido.value = false }
    fun cerrarDireccionEnvioMenu() { _direccionEnvioExpandido.value = false }
    fun cerrarOficinaComarcalMenu() { _oficinaComarcalExpandido.value = false }

    // ============================================
    // FUNCIONES DE SELECCIÓN
    // ============================================

    fun seleccionarEmpresa(codigo: String, nombre: String) {
        _empresaSubministradora.value = nombre
        codigoEmpresaSubministradora = codigo
        cerrarEmpresaMenu()
    }

    fun seleccionarTipoEnviamiento(codigo: String, nombre: String) {
        _tipoEnviamiento.value = nombre
        codigoTipoEnviamiento = codigo
        cerrarTipoEnviamientoMenu()
    }

    fun seleccionarDireccionEnvio(codigo: String, nombre: String) {
        _direccionEnvio.value = nombre
        codigoDireccionEnvio = codigo
        cerrarDireccionEnvioMenu()
        // Limpiar campos condicionales al cambiar destino
        when (codigo) {
            "01" -> {
                _dirrecionEnvio.value = ""
                _poblacion.value = ""
                _codigoPostal.value = ""
                _municipio.value = ""
                _telefonoContacto.value = ""
            }
            "02" -> {
                _oficinaComarcal.value = ""
                codigoOficinaComarcal = ""
            }
            "03" -> {
                _oficinaComarcal.value = ""
                codigoOficinaComarcal = ""
            }
        }
    }

    fun seleccionarOficinaComarcal(codigo: String, nombre: String) {
        _oficinaComarcal.value = nombre
        codigoOficinaComarcal = codigo
        cerrarOficinaComarcalMenu()
    }

    fun actualizarDireccionEnvio(valor: String) { _dirrecionEnvio.value = valor }
    fun actualizarPoblacion(valor: String) { _poblacion.value = valor }
    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) {
            _codigoPostal.value = valor
        }
    }
    fun actualizarMunicipio(valor: String) { _municipio.value = valor }
    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) {
            _telefonoContacto.value = valor
        }
    }

    // ============================================
    // VALIDACIÓN
    // ============================================

    fun esFormularioValido(): Boolean {
        if (codigoEmpresaSubministradora.isEmpty()) return false
        if (codigoTipoEnviamiento.isEmpty()) return false
        if (codigoDireccionEnvio.isEmpty()) return false

        when (codigoDireccionEnvio) {
            // OC: requiere oficina comarcal obligatoriamente
            "01" -> if (codigoOficinaComarcal.isEmpty()) return false
            // Explotación ("02"): los campos de dirección son opcionales, no se valida nada más
            "02" -> { /* campos opcionales, siempre válido */ }
            // Dirección alternativa ("03"): todos los campos de dirección son obligatorios
            "03" -> {
                if (_dirrecionEnvio.value.isNullOrEmpty()) return false
                if (_poblacion.value.isNullOrEmpty()) return false
                if (_codigoPostal.value.isNullOrEmpty()) return false
                if (_municipio.value.isNullOrEmpty()) return false
                if (_telefonoContacto.value.isNullOrEmpty()) return false
            }
        }

        val identificadores = _listaIdentificadores.value ?: return false
        if (identificadores.isEmpty()) return false
        if (identificadores.any { it.identificador.isEmpty() || it.tipusMaterial.isEmpty() }) return false

        return true
    }

    // ============================================
    // LLAMADA A LA API
    // ============================================

    fun solicitarDuplicado() {
        if (!esFormularioValido()) {
            val mensajeError = when {
                codigoEmpresaSubministradora.isEmpty() -> "Por favor, seleccione la empresa subministradora"
                codigoTipoEnviamiento.isEmpty() -> "Por favor, seleccione el tipo de envío"
                codigoDireccionEnvio.isEmpty() -> "Por favor, seleccione la dirección de envío"
                codigoDireccionEnvio == "01" && codigoOficinaComarcal.isEmpty() -> "Por favor, seleccione la oficina comarcal"
                // "02": campos opcionales, nunca llega aquí por dirección
                codigoDireccionEnvio == "03" && _dirrecionEnvio.value.isNullOrEmpty() -> "Por favor, introduzca la dirección"
                codigoDireccionEnvio == "03" && _poblacion.value.isNullOrEmpty() -> "Por favor, introduzca la población"
                codigoDireccionEnvio == "03" && _codigoPostal.value.isNullOrEmpty() -> "Por favor, introduzca el código postal"
                codigoDireccionEnvio == "03" && _municipio.value.isNullOrEmpty() -> "Por favor, introduzca el municipio"
                codigoDireccionEnvio == "03" && _telefonoContacto.value.isNullOrEmpty() -> "Por favor, introduzca el teléfono de contacto"
                (_listaIdentificadores.value?.any { it.identificador.isEmpty() } == true) -> "Por favor, complete todos los identificadores"
                (_listaIdentificadores.value?.any { it.tipusMaterial.isEmpty() } == true) -> "Por favor, seleccione el tipo de material para cada identificador"
                else -> "Por favor, complete todos los campos obligatorios"
            }
            _mensajeError.value = mensajeError
            return
        }

        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val adrecaFinal = if (codigoDireccionEnvio == "03") _dirrecionEnvio.value else null
                val poblacionFinal = if (codigoDireccionEnvio == "03") _poblacion.value else null
                val cpFinal = if (codigoDireccionEnvio == "03") _codigoPostal.value else null
                val municipioFinal = if (codigoDireccionEnvio == "03") _municipio.value else null
                val telefonoFinal = if (codigoDireccionEnvio == "03") _telefonoContacto.value else null
                val ocFinal = if (codigoDireccionEnvio == "01") codigoOficinaComarcal else null

                val request = PetSolicitudDuplicado(
                    nif = nif,
                    passwordMobilitat = password,
                    especie = "01",
                    empresaSubministradora = codigoEmpresaSubministradora,
                    tipusEnviament = codigoTipoEnviamiento,
                    adrecaLliurament = codigoDireccionEnvio,
                    oc = ocFinal,
                    adreca = adrecaFinal,
                    poblacio = poblacionFinal,
                    cp = cpFinal,
                    municipi = municipioFinal,
                    telefonContacte = telefonoFinal,
                    identificadors = _listaIdentificadores.value ?: emptyList()
                )

                Log.d("Solicitud Duplicado", "Request: $request")

                val response = repositorio.putSolicitudDuplicado(request)

                withContext(Dispatchers.Main) {
                    _cargando.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true
                                _mensajeError.value = ""
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
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                }
                            }
                            _registroExitoso.value = false
                            Log.e("Solicitud Duplicado", "HTTP ${response.code()} - $errorBody")
                        }
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargando.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. Verifique si la operación se completó."
                    Log.e("Solicitud Duplicado", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargando.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Solicitud Duplicado", "IOException: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargando.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Solicitud Duplicado", "Error general: ${e.message}", e)
                }
            }
        }
    }

    fun limpiarFormulario() {
        _empresaSubministradora.value = ""
        codigoEmpresaSubministradora = ""
        _tipoEnviamiento.value = ""
        codigoTipoEnviamiento = ""
        _direccionEnvio.value = ""
        codigoDireccionEnvio = ""
        _oficinaComarcal.value = ""
        codigoOficinaComarcal = ""
        _dirrecionEnvio.value = ""
        _poblacion.value = ""
        _codigoPostal.value = ""
        _municipio.value = ""
        _telefonoContacto.value = ""
        _listaIdentificadores.value = listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
        _tipoMaterialExpandido.value = emptyMap()
    }

    fun resetearEstado() {
        _registroExitoso.value = false
        _mensajeError.value = ""
    }

    fun getCodigoDirecioEnvio(): String {
        return codigoDireccionEnvio
    }
}
