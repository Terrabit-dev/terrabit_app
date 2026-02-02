package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ViewModelMuerteBovi : ViewModel() {


    // Estados del formulario de muerte/avortament
    private val repositorio = Repositorio()
    private val _tipoMuerte = MutableLiveData("") // "01 - Mort" o "02 - Avortament"
    val tipoMuerte = _tipoMuerte

    private val _identificadorMuerte = MutableLiveData("")
    val identificadorMuerte = _identificadorMuerte

    private val _fechaMuerte = MutableLiveData("")
    val fechaMuerte = _fechaMuerte

    private val _mesesGestacion = MutableLiveData("")
    val mesesGestacion = _mesesGestacion

    private val _cadaverInaccesible = MutableLiveData(false)
    val cadaverInaccesible = _cadaverInaccesible

    private val _coordenadaX = MutableLiveData("")
    val coordenadaX = _coordenadaX

    private val _coordenadaY = MutableLiveData("")
    val coordenadaY = _coordenadaY

    // Estados de expansión de menús desplegables - Muerte
    private val _tipoMuerteExpandido = MutableLiveData(false)
    val tipoMuerteExpandido = _tipoMuerteExpandido

    // Estado para mostrar el DatePicker - Muerte
    private val _mostrarDatePickerMuerte = MutableLiveData(false)
    val mostrarDatePickerMuerte = _mostrarDatePickerMuerte

    // Estados para feedback del registro - Muerte
    private val _registroMuerteExitoso = MutableLiveData<Boolean>()
    val registroMuerteExitoso = _registroMuerteExitoso

    private val _mensajeErrorMuerte = MutableLiveData<String>()
    val mensajeErrorMuerte = _mensajeErrorMuerte

    // Estado de carga
    private val _cargandoMuerte = MutableLiveData(false)
    val cargandoMuerte = _cargandoMuerte

    // codigo de tipo de muerte

    val _codigoTipoMuerte = MutableLiveData<String>()
    val codigoTipoMuerte = _codigoTipoMuerte


    // Funciones para actualizar los campos - Muerte
    fun seleccionarTipoMuerte(tipo: String, codigo: String) {
        _tipoMuerte.value = tipo
        _codigoTipoMuerte.value = codigo
        _tipoMuerteExpandido.value = false

        // Limpiar meses gestación si cambia a Mort
        if (tipo.contains("Mort")) {
            _mesesGestacion.value = ""
        }
    }

    fun actualizarIdentificadorMuerte(nuevoId: String) {
        _identificadorMuerte.value = nuevoId
    }

    fun actualizarMesesGestacion(valor: String) {
        // Solo permitir números de 1-9
        if (valor.isEmpty() || (valor.toIntOrNull() in 1..9)) {
            _mesesGestacion.value = valor
        }
    }

    fun toggleCadaverInaccesible() {
        val nuevoValor = !(_cadaverInaccesible.value ?: false)
        _cadaverInaccesible.value = nuevoValor

        // Limpiar coordenadas si se desactiva
        if (!nuevoValor) {
            _coordenadaX.value = ""
            _coordenadaY.value = ""
        }
    }

    fun actualizarCoordenadaX(valor: String) {
        _coordenadaX.value = valor
    }

    fun actualizarCoordenadaY(valor: String) {
        _coordenadaY.value = valor
    }

    // Funciones para controlar la expansión de menús - Muerte
    fun toggleTipoMuerteExpandido() {
        _tipoMuerteExpandido.value = !(_tipoMuerteExpandido.value ?: false)
    }

    fun cerrarTipoMuerteMenu() {
        _tipoMuerteExpandido.value = false
    }

    // Funciones para controlar el DatePicker - Muerte
    fun mostrarDatePickerMuerte() {
        _mostrarDatePickerMuerte.value = true
    }

    fun ocultarDatePickerMuerte() {
        _mostrarDatePickerMuerte.value = false
    }

    fun seleccionarFechaMuerte(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _fechaMuerte.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePickerMuerte.value = false
    }

    // Función para obtener ubicación GPS actual
    fun obtenerUbicacionActual() {
        // Por ahora valores de ejemplo en formato UTM
        _coordenadaX.value = "123456,12"
        _coordenadaY.value = "1234567,12"
        Log.d("GPS", "Ubicación obtenida - X: ${_coordenadaX.value}, Y: ${_coordenadaY.value}")
    }

    // Función para validar el formulario - Muerte
    fun esFormularioMuerteValido(): Boolean {
        // Validaciones básicas obligatorias
        val tipoValido = !_tipoMuerte.value.isNullOrEmpty()
        val identificadorValido = !_identificadorMuerte.value.isNullOrEmpty()
        val fechaValida = !_fechaMuerte.value.isNullOrEmpty()

        // Si es avortament, validar meses gestación
        val mesesValidos = if (_tipoMuerte.value?.contains("Avortament") == true) {
            !_mesesGestacion.value.isNullOrEmpty() && _mesesGestacion.value?.toIntOrNull() in 1..9
        } else {
            true // Si no es avortament, no se requiere
        }

        // Si cadáver inaccesible, validar coordenadas
        val coordenadasValidas = if (_cadaverInaccesible.value == true) {
            !_coordenadaX.value.isNullOrEmpty() && !_coordenadaY.value.isNullOrEmpty()
        } else {
            true // Si no está marcado, no se requieren coordenadas
        }

        // Log para debug
        Log.d("Validación Muerte", "Tipo: '${_tipoMuerte.value}' - válido: $tipoValido")
        Log.d("Validación Muerte", "ID: '${_identificadorMuerte.value}' - válido: $identificadorValido")
        Log.d("Validación Muerte", "Fecha: '${_fechaMuerte.value}' - válida: $fechaValida")
        Log.d("Validación Muerte", "Meses: '${_mesesGestacion.value}' - válidos: $mesesValidos")
        Log.d("Validación Muerte", "Cadáver inaccesible: ${_cadaverInaccesible.value}")
        Log.d("Validación Muerte", "Coordenadas - válidas: $coordenadasValidas")
        Log.d("Validación Muerte", "RESULTADO FINAL: ${tipoValido && identificadorValido && fechaValida && mesesValidos && coordenadasValidas}")

        return tipoValido && identificadorValido && fechaValida && mesesValidos && coordenadasValidas
    }


    // Función para limpiar el formulario - Muerte
    fun limpiarFormularioMuerte() {
        _tipoMuerte.value = ""
        _identificadorMuerte.value = ""
        _fechaMuerte.value = ""
        _mesesGestacion.value = ""
        _cadaverInaccesible.value = false
        _coordenadaX.value = ""
        _coordenadaY.value = ""
    }

    // Función para resetear el estado de registro - Muerte
    fun resetearEstadoRegistroMuerte() {
        _registroMuerteExitoso.value = false
        _mensajeErrorMuerte.value = ""
    }


    // Funcionamiento de la API
    // Funcionamiento de la API
    fun putMuerteBovino() {
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioMuerteValido()) {
            val mensajeError = when {
                _tipoMuerte.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo (Mort o Avortament)"
                _identificadorMuerte.value.isNullOrEmpty() ->
                    "Por favor, introduzca el ID del animal${if (_tipoMuerte.value?.contains("Avortament") == true) " (madre)" else ""}"
                _fechaMuerte.value.isNullOrEmpty() ->
                    "Por favor, seleccione la fecha de muerte"
                _tipoMuerte.value?.contains("Avortament") == true && _mesesGestacion.value.isNullOrEmpty() ->
                    "Por favor, introduzca los meses de gestación (1-9)"
                _tipoMuerte.value?.contains("Avortament") == true && (_mesesGestacion.value?.toIntOrNull() !in 1..9) ->
                    "Los meses de gestación deben estar entre 1 y 9"
                _cadaverInaccesible.value == true && _coordenadaX.value.isNullOrEmpty() ->
                    "Por favor, introduzca la coordenada X (Latitud)"
                _cadaverInaccesible.value == true && _coordenadaY.value.isNullOrEmpty() ->
                    "Por favor, introduzca la coordenada Y (Longitud)"
                else ->
                    "Por favor, complete todos los campos obligatorios marcados con *"
            }
            _mensajeErrorMuerte.value = mensajeError
            Log.e("Validación Muerte", mensajeError)
            return
        }

        viewModelScope.launch {
            // Activar indicador de carga
            _cargandoMuerte.postValue(true)

            try {
                // Extraer código de tipo: "01 - Mort" -> "01"
                val tipoCodigo = _tipoMuerte.value?.substring(0, 2) ?: ""

                // Convertir fecha a formato API (yyyymmdd)
                val fechaAPI = DateUtils.convertirFechaAFormatoAPI(_fechaMuerte.value ?: "")

                // Preparar coordenadas (null si cadáver no es inaccesible)
                val coordX = if (_cadaverInaccesible.value == true) _coordenadaX.value else null
                val coordY = if (_cadaverInaccesible.value == true) _coordenadaY.value else null

                // Preparar meses gestación (null si no es avortament)
                val mesesGest = if (_tipoMuerte.value?.contains("Avortament") == true) {
                    _mesesGestacion.value
                } else {
                    null
                }

                // Crear objeto de petición
                val request = RegistroMuerteBovi(
                    cadaverInaccesible = if (_cadaverInaccesible.value == true) "SI" else "NO",
                    coordenadaX = coordX,
                    coordenadaY = coordY,
                    dataMort = fechaAPI,
                    identificador = _identificadorMuerte.value,
                    mesosGestacio = mesesGest,
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    tipus = tipoCodigo
                )
                Log.d("Registro Muerte", "Request: $request")

                // Llamar a la API
                val response = repositorio.putRegistrarMuerte(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    // Desactivar indicador de carga
                    _cargandoMuerte.value = false

                    when {
                        // Caso: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroMuerteExitoso.value = true
                                _mensajeErrorMuerte.value = ""

                                Log.d("Registro Muerte", "Muerte reportada exitosamente")
                                Log.d("Registro Muerte", "Respuesta: [${body.codi}] ${body.descripcio}")

                                // Limpiar formulario después de registrar exitosamente
                                limpiarFormularioMuerte()
                            }
                            // Caso inesperado: respuesta exitosa pero sin código 0 ni errores
                            else {
                                _registroMuerteExitoso.value = false
                                _mensajeErrorMuerte.value = "Respuesta inesperada del servidor: ${body.descripcio ?: "Sin descripción"}"
                                Log.w("Registro Muerte", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    // Cogemos la descripción del primer error, o un mensaje por defecto si está vacía
                                    _mensajeErrorMuerte.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeErrorMuerte.value = "Error al procesar respuesta"
                                }
                            }
                            _registroMuerteExitoso.value = false
                            Log.e("Error Registro Muerte", "HTTP ${response.code()}")
                            Log.e("Error Registro Muerte", "Mensaje: ${response.message()}")
                            if (errorBody != null) {
                                Log.e("Error Registro Muerte", "Body: $errorBody")
                            }
                        }

                        // Caso 3: Respuesta exitosa pero sin body
                        else -> {
                            _registroMuerteExitoso.value = false
                            _mensajeErrorMuerte.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Registro Muerte", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                // Manejo específico de timeout
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Muerte", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                // Error de red
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Muerte", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                // Otros errores
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Registro Muerte", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }
}