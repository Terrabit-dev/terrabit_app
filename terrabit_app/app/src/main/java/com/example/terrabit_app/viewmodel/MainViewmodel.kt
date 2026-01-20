package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewmodel : ViewModel() {
    private val repositorio = Repositorio()

    // ============================================
    // SECCIÓN: IDENTIFICADORES
    // ============================================
    private val _identificadores = MutableLiveData<Identificadores>()
    val identificadores = _identificadores

    fun getIdentificadores(nif: String, password: String, codiMO: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repositorio.getIdentificadoresDisponibles(nif, password, codiMO)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _identificadores.value = response.body()
                } else {
                    Log.e("Error identificadores:", response.message())
                }
            }
        }
    }

    // ============================================
    // SECCIÓN: NACIMIENTO
    // ============================================

    // Estados del formulario de nacimiento
    private val _idMadre = MutableLiveData("")
    val idMadre = _idMadre

    private val _codigoRaza = MutableLiveData("")
    private val _fechaIdentificacion = MutableLiveData("")
    val fechaIdentificacion = _fechaIdentificacion

    private val _idCria = MutableLiveData("")
    val idCria = _idCria

    private val _fechaNacimiento = MutableLiveData("")
    val fechaNacimiento = _fechaNacimiento

    private val _sexoSeleccionado = MutableLiveData("")
    val sexoSeleccionado = _sexoSeleccionado

    private val _razaSeleccionada = MutableLiveData("")
    val razaSeleccionada = _razaSeleccionada

    private val _aptitudSeleccionada = MutableLiveData("")
    val aptitudSeleccionada = _aptitudSeleccionada

    // Estados de expansión de menús desplegables - Nacimiento
    private val _sexoExpandido = MutableLiveData(false)
    val sexoExpandido = _sexoExpandido

    private val _razaExpandida = MutableLiveData(false)
    val razaExpandida = _razaExpandida

    private val _aptitudExpandida = MutableLiveData(false)
    val aptitudExpandida = _aptitudExpandida

    // Estado para mostrar el DatePicker - Nacimiento
    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker = _mostrarDatePicker

    private val _mostrarDatePickerIdentificacion = MutableLiveData(false)
    val mostrarDatePickerIdentificacion = _mostrarDatePickerIdentificacion

    // Estados para feedback del registro - Nacimiento
    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    // Listas de opciones - Nacimiento
    val listaSexos = listOf("Macho", "Hembra")
    val razasBovinas = listOf(
        Razas("1111", "Holstein (Frisona)"),
        Razas("1116", "Angus"),
        Razas("1114", "Hereford"),
        Razas("9907", "Simmental"),
        Razas("1113", "Charolais (Xarolesa)"),
        Razas("1115", "Jersey"),
        Razas("1117", "Limousin (Limusina)"),
        Razas("0000", "Mestizo")

    )
    val listaAptitudes = listOf("Carne", "Leche", "Doble propósito")

    // Funciones para actualizar los campos - Nacimiento
    fun actualizarIdMadre(nuevoId: String) {
        _idMadre.value = nuevoId
    }

    fun actualizarIdCria(nuevoId: String) {
        _idCria.value = nuevoId
    }

    fun actualizarFechaNacimiento(nuevaFecha: String) {
        _fechaNacimiento.value = nuevaFecha
    }

    fun seleccionarSexo(sexo: String) {
        _sexoSeleccionado.value = sexo
        _sexoExpandido.value = false
    }

    fun seleccionarRaza(raza: String, codigo: String) {
        _razaSeleccionada.value = raza
        _codigoRaza.value = codigo
        _razaExpandida.value = false
    }

    fun seleccionarAptitud(aptitud: String) {
        _aptitudSeleccionada.value = aptitud
        _aptitudExpandida.value = false
    }

    // Funciones para controlar la expansión de menús - Nacimiento
    fun toggleSexoExpandido() {
        _sexoExpandido.value = !(_sexoExpandido.value ?: false)
    }

    fun toggleRazaExpandida() {
        _razaExpandida.value = !(_razaExpandida.value ?: false)
    }

    fun toggleAptitudExpandida() {
        _aptitudExpandida.value = !(_aptitudExpandida.value ?: false)
    }

    fun cerrarSexoMenu() {
        _sexoExpandido.value = false
    }

    fun cerrarRazaMenu() {
        _razaExpandida.value = false
    }

    fun cerrarAptitudMenu() {
        _aptitudExpandida.value = false
    }

    // Funciones para controlar el DatePicker - Nacimiento
    fun mostrarDatePicker() {
        _mostrarDatePicker.value = true
    }

    fun ocultarDatePicker() {
        _mostrarDatePicker.value = false
    }

    // Funciones para controlar el DatePicker - Identificacion
    fun mostrarDatePickerIdentificacion() {
        _mostrarDatePickerIdentificacion.value = true
    }

    fun ocultarDatePickerIdentificacion() {
        _mostrarDatePickerIdentificacion.value = false
    }

    // Funcion selecionar fecha - Nacimiento
    fun seleccionarFecha(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _fechaNacimiento.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePicker.value = false
    }

    // Funcion selecionar fecha - Identificacion
    fun seleccionarFechaIdentificacion(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _fechaIdentificacion.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePickerIdentificacion.value = false
    }

    // Función para validar el formulario - Nacimiento
    fun esFormularioNacimientoValido(): Boolean {
        val idMadreValido = !_idMadre.value.isNullOrEmpty()
        val idCriaValido = !_idCria.value.isNullOrEmpty()
        val fechaNacimientoValida = !_fechaNacimiento.value.isNullOrEmpty()
        val sexoValido = !_sexoSeleccionado.value.isNullOrEmpty()
        val razaValida = !_razaSeleccionada.value.isNullOrEmpty()
        val aptitudValida = !_aptitudSeleccionada.value.isNullOrEmpty()

        return idMadreValido && idCriaValido && fechaNacimientoValida &&
                sexoValido && razaValida && aptitudValida
    }

    // Función para registrar un nacimiento con gestión mejorada de errores
    fun registrarNacimiento() {
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioNacimientoValido()) {
            val mensajeError = when {
                _idMadre.value.isNullOrEmpty() ->
                    "Por favor, introduzca el ID de la madre"
                _idCria.value.isNullOrEmpty() ->
                    "Por favor, introduzca el ID de la cría"
                _fechaNacimiento.value.isNullOrEmpty() ->
                    "Por favor, seleccione la fecha de nacimiento"
                _sexoSeleccionado.value.isNullOrEmpty() ->
                    "Por favor, seleccione el sexo del animal"
                _razaSeleccionada.value.isNullOrEmpty() ->
                    "Por favor, seleccione la raza"
                _aptitudSeleccionada.value.isNullOrEmpty() ->
                    "Por favor, seleccione la aptitud"
                else ->
                    "Por favor, complete todos los campos obligatorios marcados con *"
            }
            _mensajeError.value = mensajeError
            Log.e("Validación Nacimiento", mensajeError)
            return
        }

        viewModelScope.launch {
            try {
                // Convertir fechas a formato API (yyyymmdd)
                val fechaNacimientoAPI = convertirFechaAFormatoAPI(_fechaNacimiento.value ?: "")
                val fechaIdentificacionAPI = convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")

                // Convertir sexo al formato de la API
                val sexoAPI = when (_sexoSeleccionado.value) {
                    "Macho" -> "02"
                    "Hembra" -> "01"
                    else -> ""
                }

                // Convertir aptitud al formato de la API
                val aptitudAPI = when (_aptitudSeleccionada.value) {
                    "Carne" -> "02"
                    "Leche" -> "01"
                    "Doble propósito" -> "03"
                    else -> ""
                }

                // Crear objeto de petición
                val request = RegistroNacimientoBovi(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    identificador = _idCria.value ?: "",
                    identificadorMare = _idMadre.value ?: "",
                    dataNaixement = fechaNacimientoAPI,
                    dataIdentificacio = fechaIdentificacionAPI,
                    sexe = sexoAPI,
                    raca = _codigoRaza.value ?: "",
                    aptitud = aptitudAPI
                )
                Log.d("Registro Nacimiento", "Request: $request")

                // Llamar a la API
                val response = repositorio.putRegistrarNacimiento(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    when {
                        // Caso 1: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // Verificar si hay errores en el body
                            if (body.errors != null && body.errors.isNotEmpty()) {
                                // La API devolvió errores
                                val erroresTexto = body.errors.joinToString("\n") { error ->
                                    "• [${error.codi}] ${error.descripcio}"
                                }
                                _registroExitoso.value = false
                                _mensajeError.value = "Error al registrar nacimiento:\n$erroresTexto"
                                body.errors.forEach { error ->
                                    Log.e("Error Registro Nacimiento", "  - [${error.codi}] ${error.descripcio}")
                                }
                            }
                            // Verificar si es respuesta exitosa (codi = "0")
                            else if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true
                                _mensajeError.value = ""

                                Log.d("Registro Nacimiento", "Nacimiento reportado exitosamente")
                                Log.d("Registro Nacimiento", "Respuesta: [${body.codi}] ${body.descripcio}")

                                // Limpiar formulario después de registrar exitosamente
                                limpiarFormularioNacimiento()
                            }
                            // Caso inesperado: respuesta exitosa pero sin código 0 ni errores
                            else {
                                _registroExitoso.value = false
                                _mensajeError.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Registro Nacimiento", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            _registroExitoso.value = false
                            _mensajeError.value = "Error HTTP ${response.code()}: ${response.message()}"

                            Log.e("Error Registro Nacimiento", "HTTP ${response.code()}")
                            Log.e("Error Registro Nacimiento", "Mensaje: ${response.message()}")
                            if (errorBody != null) {
                                Log.e("Error Registro Nacimiento", "Body: $errorBody")
                            }
                        }

                        // Caso 3: Respuesta exitosa pero sin body
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Registro Nacimiento", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                // Manejo específico de timeout
                withContext(Dispatchers.Main) {
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Nacimiento", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                // Error de red
                withContext(Dispatchers.Main) {
                    _registroExitoso.value = false
                    _mensajeError.value = "📡 Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Nacimiento", " Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                // Otros errores
                withContext(Dispatchers.Main) {
                    _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Registro Nacimiento", " Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    // Función para limpiar el formulario - Nacimiento
    fun limpiarFormularioNacimiento() {
        _idMadre.value = ""
        _idCria.value = ""
        _fechaNacimiento.value = ""
        _fechaIdentificacion.value = ""
        _sexoSeleccionado.value = ""
        _razaSeleccionada.value = ""
        _codigoRaza.value = ""
        _aptitudSeleccionada.value = ""
    }

    // Función para resetear el estado de registro
    fun resetearEstadoRegistro() {
        _registroExitoso.value = false
        _mensajeError.value = ""
    }

    // Función para validar formato de identificador
    fun validarIdentificador(id: String): Boolean {
        // Implementa tu lógica de validación aquí
        // Por ejemplo: verificar longitud, formato, etc.
        return id.length >= 5 // Ejemplo simple
    }



    // ============================================
    // SECCIÓN: FALLECIMIENTO / MUERTE
    // ============================================

    // Estados del formulario de muerte/avortament
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

    // Lista de tipos de muerte
    val listaTiposMuerte = listOf("01 - Mort", "02 - Avortament")

    // Funciones para actualizar los campos - Muerte
    fun seleccionarTipoMuerte(tipo: String) {
        _tipoMuerte.value = tipo
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
            try {
                // Extraer código de tipo: "01 - Mort" -> "01"
                val tipoCodigo = _tipoMuerte.value?.substring(0, 2) ?: ""

                // Convertir fecha a formato API (yyyymmdd)
                val fechaAPI = convertirFechaAFormatoAPI(_fechaMuerte.value ?: "")

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
                    when {
                        // Caso 1: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // Verificar si hay errores en el body
                            if (body.errors != null && body.errors.isNotEmpty()){
                                // La API devolvió errores
                                val erroresTexto = body.errors.joinToString("\n") { error ->
                                    "• [${error.codi}] ${error.descripcio}"
                                }

                                _registroMuerteExitoso.value = false
                                _mensajeErrorMuerte.value = "Error al registrar muerte:\n$erroresTexto"

                                Log.e("Error Registro Muerte", "Errores de la API:")
                                body.errors.forEach { error ->
                                    Log.e("Error Registro Muerte", "  - [${error.codi}] ${error.descripcio}")
                                }
                            }
                            // Verificar si es respuesta exitosa (codi = "0")
                            else if (body.codi == "0" || body.descripcio == "OK") {
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
                                _mensajeErrorMuerte.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Registro Muerte", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            _registroMuerteExitoso.value = false
                            _mensajeErrorMuerte.value = "Error HTTP ${response.code()}: ${response.message()}"

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
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Muerte", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                // Error de red
                withContext(Dispatchers.Main) {
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Muerte", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                // Otros errores
                withContext(Dispatchers.Main) {
                    _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Registro Muerte", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    // ============================================
    // FUNCIONES AUXILIARES
    // ============================================

    /**
     * Convierte una fecha de formato "dd/MM/yyyy" a "yyyymmdd"
     */
    private fun convertirFechaAFormatoAPI(fecha: String): String {
        return try {
            val partes = fecha.split("/")
            if (partes.size == 3) {
                val dia = partes[0]
                val mes = partes[1]
                val anio = partes[2]
                "$anio$mes$dia"
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("Error conversión fecha", e.message ?: "Error desconocido")
            ""
        }
    }

    /**
     * Convierte una fecha de formato "yyyymmdd" a "dd/MM/yyyy"
     */
    private fun convertirFechaDesdeAPI(fechaAPI: String): String {
        return try {
            if (fechaAPI.length == 8) {
                val anio = fechaAPI.substring(0, 4)
                val mes = fechaAPI.substring(4, 6)
                val dia = fechaAPI.substring(6, 8)
                "$dia/$mes/$anio"
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("Error conversión fecha", e.message ?: "Error desconocido")
            ""
        }
    }
}