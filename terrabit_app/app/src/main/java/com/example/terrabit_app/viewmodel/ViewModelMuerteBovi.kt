package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ViewModelMuerteBovi : ViewModel() {

    private lateinit var repositorio: Repositorio
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ID único para la sesión actual del formulario
    private var borradorSesionId: String = ""

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        // Generar nuevo ID de sesión si no existe
        if (borradorSesionId.isEmpty()) {
            borradorSesionId = "muerte_auto_${System.currentTimeMillis()}"
        }
    }

    fun tieneContenido(): Boolean {
        return !_tipoMuerte.value.isNullOrEmpty() ||
                !_identificadorMuerte.value.isNullOrEmpty() ||
                !_fechaMuerte.value.isNullOrEmpty() ||
                !_mesesGestacion.value.isNullOrEmpty() ||
                _cadaverInaccesible.value == true ||
                !_coordenadaX.value.isNullOrEmpty() ||
                !_coordenadaY.value.isNullOrEmpty()
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) {
            Log.d("Autoguardado Muerte", "No hay contenido para guardar")
            return
        }

        try {
            val datosMuerte = mapOf(
                "tipo" to _tipoMuerte.value,
                "codigoTipo" to _codigoTipoMuerte.value,
                "identificador" to _identificadorMuerte.value,
                "fecha" to _fechaMuerte.value,
                "mesesGestacion" to _mesesGestacion.value,
                "cadaverInaccesible" to _cadaverInaccesible.value,
                "coordenadaX" to _coordenadaX.value,
                "coordenadaY" to _coordenadaY.value
            )

            // Buscar si ya existe este borrador específico de la sesión actual
            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == borradorSesionId }

            val borrador = if (borradorExistente != null) {
                // Actualizar borrador de esta sesión
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosMuerte)
                )
            } else {
                // Crear nuevo borrador con ID de sesión
                Borrador(
                    id = borradorSesionId,
                    tipo = "MUERTE",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosMuerte),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Muerte", "Borrador guardado: $borradorSesionId")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Muerte", "Error al guardar: ${e.message}", e)
        }
    }

    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()

            // Buscar cualquier borrador de tipo MUERTE con estado BORRADOR_AUTO
            val borradoresMuerte = borradores.filter {
                it.tipo == "MUERTE" && it.estado == "BORRADOR_AUTO"
            }

            if (borradoresMuerte.isNotEmpty()) {
                // Tomar el más reciente (último guardado)
                val borradorMuerte = borradoresMuerte.maxByOrNull {
                    it.id.substringAfter("muerte_auto_").toLongOrNull() ?: 0L
                }

                if (borradorMuerte != null) {
                    // Asignar este ID a la sesión actual
                    borradorSesionId = borradorMuerte.id

                    val gson = Gson()
                    val datos: Map<String, Any?> = gson.fromJson(
                        borradorMuerte.datos,
                        object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                    )

                    // Restaurar datos
                    _tipoMuerte.value = datos["tipo"] as? String ?: ""
                    _codigoTipoMuerte.value = datos["codigoTipo"] as? String ?: ""
                    _identificadorMuerte.value = datos["identificador"] as? String ?: ""
                    _fechaMuerte.value = datos["fecha"] as? String ?: ""
                    _mesesGestacion.value = datos["mesesGestacion"] as? String ?: ""
                    _cadaverInaccesible.value = datos["cadaverInaccesible"] as? Boolean ?: false
                    _coordenadaX.value = datos["coordenadaX"] as? String ?: ""
                    _coordenadaY.value = datos["coordenadaY"] as? String ?: ""

                    Log.d("Cargar Borrador", "Borrador cargado: $borradorSesionId")
                }
            }
        } catch (e: Exception) {
            Log.e("Error Cargar Borrador", "Error al cargar: ${e.message}", e)
        }
    }

    fun eliminarBorradorAutomatico() {
        try {
            if (borradorSesionId.isNotEmpty()) {
                sharedPreferencesManager.eliminarBorrador(borradorSesionId)
                Log.d("Eliminar Borrador", "Borrador eliminado: $borradorSesionId")
                borradorSesionId = "" // Resetear el ID de sesión
            }
        } catch (e: Exception) {
            Log.e("Error Eliminar Borrador", "Error: ${e.message}", e)
        }
    }

    private val _tipoMuerte = MutableLiveData("")
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

    private val _tipoMuerteExpandido = MutableLiveData(false)
    val tipoMuerteExpandido = _tipoMuerteExpandido

    private val _mostrarDatePickerMuerte = MutableLiveData(false)
    val mostrarDatePickerMuerte = _mostrarDatePickerMuerte

    private val _registroMuerteExitoso = MutableLiveData<Boolean>()
    val registroMuerteExitoso = _registroMuerteExitoso

    private val _mensajeErrorMuerte = MutableLiveData<String>()
    val mensajeErrorMuerte = _mensajeErrorMuerte

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    private val _cargandoMuerte = MutableLiveData(false)
    val cargandoMuerte = _cargandoMuerte

    private val _codigoTipoMuerte = MutableLiveData<String>()
    val codigoTipoMuerte = _codigoTipoMuerte

    fun seleccionarTipoMuerte(tipo: String, codigo: String) {
        _tipoMuerte.value = tipo
        _codigoTipoMuerte.value = codigo
        _tipoMuerteExpandido.value = false

        if (tipo.contains("Mort")) {
            _mesesGestacion.value = ""
        }
    }

    fun actualizarIdentificadorMuerte(nuevoId: String) {
        _identificadorMuerte.value = nuevoId
    }

    fun actualizarMesesGestacion(valor: String) {
        if (valor.isEmpty() || (valor.toIntOrNull() in 1..9)) {
            _mesesGestacion.value = valor
        }
    }

    fun toggleCadaverInaccesible() {
        val nuevoValor = !(_cadaverInaccesible.value ?: false)
        _cadaverInaccesible.value = nuevoValor

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

    fun toggleTipoMuerteExpandido() {
        _tipoMuerteExpandido.value = !(_tipoMuerteExpandido.value ?: false)
    }

    fun cerrarTipoMuerteMenu() {
        _tipoMuerteExpandido.value = false
    }

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

    fun obtenerUbicacionActual() {
        _coordenadaX.value = "123456,12"
        _coordenadaY.value = "1234567,12"
        Log.d("GPS", "Ubicación obtenida - X: ${_coordenadaX.value}, Y: ${_coordenadaY.value}")
    }

    fun esFormularioMuerteValido(): Boolean {
        val tipoValido = !_tipoMuerte.value.isNullOrEmpty()
        val identificadorValido = !_identificadorMuerte.value.isNullOrEmpty()
        val fechaValida = !_fechaMuerte.value.isNullOrEmpty()

        val mesesValidos = if (_tipoMuerte.value?.contains("Avortament") == true) {
            !_mesesGestacion.value.isNullOrEmpty() && _mesesGestacion.value?.toIntOrNull() in 1..9
        } else {
            true
        }

        val coordenadasValidas = if (_cadaverInaccesible.value == true) {
            !_coordenadaX.value.isNullOrEmpty() && !_coordenadaY.value.isNullOrEmpty()
        } else {
            true
        }

        return tipoValido && identificadorValido && fechaValida && mesesValidos && coordenadasValidas
    }

    fun limpiarFormularioMuerte() {
        _tipoMuerte.value = ""
        _codigoTipoMuerte.value = ""
        _identificadorMuerte.value = ""
        _fechaMuerte.value = ""
        _mesesGestacion.value = ""
        _cadaverInaccesible.value = false
        _coordenadaX.value = ""
        _coordenadaY.value = ""

        // Generar nuevo ID de sesión para el próximo formulario
        borradorSesionId = ""
    }


    fun obtenerBorradoresMuerte(): List<Borrador> {
        return try {
            sharedPreferencesManager.obtenerBorradores()
                .filter { it.tipo == "MUERTE" && it.estado == "BORRADOR_AUTO" }
        } catch (e: Exception) {
            Log.e("Error", "Error al obtener borradores: ${e.message}", e)
            emptyList()
        }
    }
    fun resetearEstadoRegistroMuerte() {
        _registroMuerteExitoso.value = false
        _mensajeErrorMuerte.value = ""
        _codiError.value = null
    }

    fun putMuerteBovino() {
        _codiError.value = null

        if (!esFormularioMuerteValido()) {
            val mensajeError = when {
                _tipoMuerte.value.isNullOrEmpty() -> 7
                _identificadorMuerte.value.isNullOrEmpty() -> 0
                _fechaMuerte.value.isNullOrEmpty() -> 8
                _tipoMuerte.value?.contains("Avortament") == true && _mesesGestacion.value.isNullOrEmpty() -> 9
                _cadaverInaccesible.value == true && _coordenadaX.value.isNullOrEmpty() -> 10
                _cadaverInaccesible.value == true && _coordenadaY.value.isNullOrEmpty() -> 11
                else -> 0
            }
            _codiError.value = mensajeError
            Log.e("Validación Muerte", "Error: $mensajeError")
            return
        }

        viewModelScope.launch {
            _cargandoMuerte.postValue(true)

            try {
                val tipoCodigo = _codigoTipoMuerte.value?.substring(0, 2) ?: ""
                val fechaAPI = DateUtils.convertirFechaAFormatoAPI(_fechaMuerte.value ?: "")
                val coordX = if (_cadaverInaccesible.value == true) _coordenadaX.value else null
                val coordY = if (_cadaverInaccesible.value == true) _coordenadaY.value else null
                val mesesGest = if (_tipoMuerte.value?.contains("Avortament") == true) {
                    _mesesGestacion.value
                } else {
                    null
                }

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

                val response = repositorio.putRegistrarMuerte(request)

                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false

                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroMuerteExitoso.value = true
                                _mensajeErrorMuerte.value = ""

                                Log.d("Registro Muerte", "Muerte reportada exitosamente")

                                eliminarBorradorAutomatico()
                                limpiarFormularioMuerte()
                            } else {
                                _registroMuerteExitoso.value = false
                                _mensajeErrorMuerte.value = "Respuesta inesperada del servidor: ${body.descripcio ?: "Sin descripción"}"
                                Log.w("Registro Muerte", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeErrorMuerte.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeErrorMuerte.value = "Error al procesar respuesta"
                                }
                            }
                            _registroMuerteExitoso.value = false
                            Log.e("Error Registro Muerte", "HTTP ${response.code()}")
                        }
                        else -> {
                            _registroMuerteExitoso.value = false
                            _mensajeErrorMuerte.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Registro Muerte", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Muerte", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Muerte", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
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