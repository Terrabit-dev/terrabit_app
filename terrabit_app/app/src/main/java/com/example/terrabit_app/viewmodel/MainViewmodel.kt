package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.material.Unitat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
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
                        // Caso: HTTP 200 OK
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
// SECCIÓN: SOLICITUD DE MATERIAL
// ============================================

    // Estados del formulario de material
    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora

    private val _codigoEmpresa = MutableLiveData("")

    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento

    private val _destinoLliurament = MutableLiveData("")
    val destinoLliurament = _destinoLliurament

    private val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal = _oficinaComarcal

    private val _codigoOC = MutableLiveData("")

    private val _direccion = MutableLiveData("")
    val direccion = _direccion

    private val _poblacion = MutableLiveData("")
    val poblacion = _poblacion

    private val _codigoPostal = MutableLiveData("")
    val codigoPostal = _codigoPostal

    private val _municipio = MutableLiveData("")
    val municipio = _municipio

    private val _telefonoContacto = MutableLiveData("")
    val telefonoContacto = _telefonoContacto

    private val _identificadorMaterial = MutableLiveData("")
    val identificadorMaterial = _identificadorMaterial

    private val _tipoMaterial = MutableLiveData("")
    val tipoMaterial = _tipoMaterial

    private val _codigoTipoMaterial = MutableLiveData("")

    private val _numeroUnidades = MutableLiveData("1")
    val numeroUnidades = _numeroUnidades

    private val _codigoExplotacion = MutableLiveData("")
    val codigoExplotacion = _codigoExplotacion

    // Estados de expansión de menús desplegables - Material
    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida = _empresaExpandida

    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido = _tipoEnviamientoExpandido

    private val _destinoExpandido = MutableLiveData(false)
    val destinoExpandido = _destinoExpandido

    private val _oficinaComarcalExpandida = MutableLiveData(false)
    val oficinaComarcalExpandida = _oficinaComarcalExpandida

    private val _tipoMaterialExpandido = MutableLiveData(false)
    val tipoMaterialExpandido = _tipoMaterialExpandido

    // Estados para feedback del registro - Material
    private val _registroMaterialExitoso = MutableLiveData<Boolean>()
    val registroMaterialExitoso = _registroMaterialExitoso

    private val _mensajeErrorMaterial = MutableLiveData<String>()
    val mensajeErrorMaterial = _mensajeErrorMaterial

    // Data classes para opciones UI
    data class EmpresaSubministradora(val nif: String, val nombre: String)
    data class OficinaComarcal(val codigo: String, val nombre: String)
    data class TipoMaterial(val codigo: String, val nombre: String)

    // Listas de opciones - Material
    val listaEmpresas = listOf(
        EmpresaSubministradora("A60229508", "Tecnología Agrícola S.L."),
        EmpresaSubministradora("B65432109", "Ganadera del Norte S.A."),
        EmpresaSubministradora("C78945612", "Suministros Ganaderos Catalunya")
    )

    val listaTiposEnviamiento = listOf(
        "01 - Correo ordinario",
        "04 - Correo certificado"
    )

    val listaDestinos = listOf(
        "01 - Oficina Comarcal (OC)",
        "02 - Ramader/ER",
        "03 - Dirección alternativa"
    )

    val listaOficinasComarcales = listOf(
        OficinaComarcal("OC001", "Barcelona"),
        OficinaComarcal("OC002", "Girona"),
        OficinaComarcal("OC003", "Lleida"),
        OficinaComarcal("OC004", "Tarragona")
    )

    val listaTiposMaterial = listOf(
        TipoMaterial("07", "Crotal"),
        TipoMaterial("20", "Crotal electrónico"),
        TipoMaterial("21", "Injectable electrónico"),
        TipoMaterial("22", "Bol ruminal")
    )

    // Funciones para actualizar los campos - Material
    fun seleccionarEmpresa(nombre: String, nif: String) {
        _empresaSubministradora.value = nombre
        _codigoEmpresa.value = nif
        _empresaExpandida.value = false
    }

    fun seleccionarTipoEnviamiento(tipo: String) {
        _tipoEnviamiento.value = tipo
        _tipoEnviamientoExpandido.value = false
    }

    fun seleccionarDestino(destino: String) {
        _destinoLliurament.value = destino
        _destinoExpandido.value = false

        when {
            destino.startsWith("01") -> {
                _direccion.value = ""
                _poblacion.value = ""
                _codigoPostal.value = ""
                _municipio.value = ""
                _telefonoContacto.value = ""
            }
            destino.startsWith("02") -> {
                _oficinaComarcal.value = ""
                _codigoOC.value = ""
            }
            destino.startsWith("03") -> {
                _oficinaComarcal.value = ""
                _codigoOC.value = ""
            }
        }
    }

    fun seleccionarOficinaComarcal(nombre: String, codigo: String) {
        _oficinaComarcal.value = nombre
        _codigoOC.value = codigo
        _oficinaComarcalExpandida.value = false
    }

    fun seleccionarTipoMaterial(nombre: String, codigo: String) {
        _tipoMaterial.value = nombre
        _codigoTipoMaterial.value = codigo
        _tipoMaterialExpandido.value = false
    }

    fun actualizarDireccion(valor: String) {
        _direccion.value = valor
    }

    fun actualizarPoblacion(valor: String) {
        _poblacion.value = valor
    }

    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) {
            _codigoPostal.value = valor
        }
    }

    fun actualizarMunicipio(valor: String) {
        _municipio.value = valor
    }

    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) {
            _telefonoContacto.value = valor
        }
    }

    fun actualizarIdentificadorMaterial(valor: String) {
        _identificadorMaterial.value = valor
    }

    fun actualizarNumeroUnidades(valor: String) {
        if (valor.isEmpty() || valor.all { it.isDigit() }) {
            _numeroUnidades.value = valor
        }
    }

    fun actualizarCodigoExplotacion(valor: String) {
        _codigoExplotacion.value = valor
    }

    // Funciones para controlar la expansión de menús - Material
    fun toggleEmpresaExpandida() {
        _empresaExpandida.value = !(_empresaExpandida.value ?: false)
    }

    fun toggleTipoEnviamientoExpandido() {
        _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false)
    }

    fun toggleDestinoExpandido() {
        _destinoExpandido.value = !(_destinoExpandido.value ?: false)
    }

    fun toggleOficinaComarcalExpandida() {
        _oficinaComarcalExpandida.value = !(_oficinaComarcalExpandida.value ?: false)
    }

    fun toggleTipoMaterialExpandido() {
        _tipoMaterialExpandido.value = !(_tipoMaterialExpandido.value ?: false)
    }

    fun cerrarEmpresaMenu() {
        _empresaExpandida.value = false
    }

    fun cerrarTipoEnviamientoMenu() {
        _tipoEnviamientoExpandido.value = false
    }

    fun cerrarDestinoMenu() {
        _destinoExpandido.value = false
    }

    fun cerrarOficinaComarcalMenu() {
        _oficinaComarcalExpandida.value = false
    }

    fun cerrarTipoMaterialMenu() {
        _tipoMaterialExpandido.value = false
    }

    // Función para validar el formulario - Material
    fun esFormularioMaterialValido(): Boolean {
        val empresaValida = !_empresaSubministradora.value.isNullOrEmpty()
        val tipoEnviamientoValido = !_tipoEnviamiento.value.isNullOrEmpty()
        val destinoValido = !_destinoLliurament.value.isNullOrEmpty()
        val identificadorValido = !_identificadorMaterial.value.isNullOrEmpty()
        val tipoMaterialValido = !_tipoMaterial.value.isNullOrEmpty()

        val camposDestinoValidos = when {
            _destinoLliurament.value?.startsWith("01") == true -> {
                !_oficinaComarcal.value.isNullOrEmpty()
            }
            _destinoLliurament.value?.startsWith("03") == true -> {
                !_direccion.value.isNullOrEmpty() &&
                        !_poblacion.value.isNullOrEmpty() &&
                        !_codigoPostal.value.isNullOrEmpty() &&
                        !_municipio.value.isNullOrEmpty() &&
                        !_telefonoContacto.value.isNullOrEmpty()
            }
            else -> true
        }

        return empresaValida && tipoEnviamientoValido && destinoValido &&
                camposDestinoValidos && identificadorValido && tipoMaterialValido
    }

    // Función para solicitar material
// Función para solicitar material
    fun solicitarMaterial() {
        if (!esFormularioMaterialValido()) {
            val mensajeError = when {
                _empresaSubministradora.value.isNullOrEmpty() ->
                    "Por favor, seleccione la empresa subministradora"
                _tipoEnviamiento.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo de envío"
                _destinoLliurament.value.isNullOrEmpty() ->
                    "Por favor, seleccione el destino de entrega"
                _destinoLliurament.value?.startsWith("01") == true && _oficinaComarcal.value.isNullOrEmpty() ->
                    "Por favor, seleccione la oficina comarcal"
                _destinoLliurament.value?.startsWith("03") == true && _direccion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la dirección"
                _destinoLliurament.value?.startsWith("03") == true && _poblacion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la población"
                _destinoLliurament.value?.startsWith("03") == true && _codigoPostal.value.isNullOrEmpty() ->
                    "Por favor, introduzca el código postal"
                _destinoLliurament.value?.startsWith("03") == true && _municipio.value.isNullOrEmpty() ->
                    "Por favor, introduzca el municipio"
                _destinoLliurament.value?.startsWith("03") == true && _telefonoContacto.value.isNullOrEmpty() ->
                    "Por favor, introduzca el teléfono de contacto"
                _identificadorMaterial.value.isNullOrEmpty() ->
                    "Por favor, introduzca el identificador"
                _tipoMaterial.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo de material"
                else ->
                    "Por favor, complete todos los campos obligatorios"
            }
            _mensajeErrorMaterial.value = mensajeError
            Log.e("Validación Material", mensajeError)
            return
        }

        viewModelScope.launch {
            try {
                val codigoTipoEnvio = _tipoEnviamiento.value?.substring(0, 2) ?: ""
                val codigoDestino = _destinoLliurament.value?.substring(0, 2) ?: ""

                val adrecaFinal = if (codigoDestino == "03") _direccion.value else null
                val poblacionFinal = if (codigoDestino == "03") _poblacion.value else null
                val cpFinal = if (codigoDestino == "03") _codigoPostal.value else null
                val municipioFinal = if (codigoDestino == "03") _municipio.value else null
                val telefonoFinal = if (codigoDestino == "03") _telefonoContacto.value else null
                val ocFinal = if (codigoDestino == "01") _codigoOC.value else null

                val unidades = listOf(
                    Unitat(
                        codiExplotacio = _codigoExplotacion.value?.takeIf { it.isNotEmpty() },
                        nombreUnitats = _numeroUnidades.value ?: "1"
                    )
                )

                val request = PetSolicitudMaterial(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    especie = "01",
                    empresaSubministradora = _codigoEmpresa.value ?: "",
                    tipusEnviament = codigoTipoEnvio,
                    adrecaLliurament = codigoDestino,
                    oc = ocFinal,
                    adreca = adrecaFinal,
                    poblacio = poblacionFinal,
                    cp = cpFinal,
                    municipi = municipioFinal,
                    telefonContacte = telefonoFinal,
                    tipusMaterial = _codigoTipoMaterial.value ?: "",
                    unitats = unidades
                )

                Log.d("Solicitud Material", "Request: $request")

                val response = repositorio.putSolicitudMaterial(request)

                withContext(Dispatchers.Main) {
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // ResBasica probablemente tiene: codi, descripcio (sin errors)
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroMaterialExitoso.value = true
                                _mensajeErrorMaterial.value = ""

                                Log.d("Solicitud Material", "Material solicitado exitosamente")
                                Log.d("Solicitud Material", "Respuesta: [${body.codi}] ${body.descripcio}")

                                limpiarFormularioMaterial()
                            } else {
                                _registroMaterialExitoso.value = false
                                _mensajeErrorMaterial.value = "Error: [${body.codi}] ${body.descripcio}"
                                Log.e("Error Solicitud Material", "[${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error HTTP ${response.code()}: ${response.message()}"

                            Log.e("Error Solicitud Material", "HTTP ${response.code()}")
                            Log.e("Error Solicitud Material", "Mensaje: ${response.message()}")
                            if (errorBody != null) {
                                Log.e("Error Solicitud Material", "Body: $errorBody")
                            }
                        }
                        else -> {
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Solicitud Material", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Tiempo de espera agotado"
                    Log.e("Error Solicitud Material", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error de conexión"
                    Log.e("Error Solicitud Material", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error inesperado: ${e.message}"
                    Log.e("Error Solicitud Material", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun limpiarFormularioMaterial() {
        _empresaSubministradora.value = ""
        _codigoEmpresa.value = ""
        _tipoEnviamiento.value = ""
        _destinoLliurament.value = ""
        _oficinaComarcal.value = ""
        _codigoOC.value = ""
        _direccion.value = ""
        _poblacion.value = ""
        _codigoPostal.value = ""
        _municipio.value = ""
        _telefonoContacto.value = ""
        _identificadorMaterial.value = ""
        _tipoMaterial.value = ""
        _codigoTipoMaterial.value = ""
        _numeroUnidades.value = "1"
        _codigoExplotacion.value = ""
    }

    fun resetearEstadoRegistroMaterial() {
        _registroMaterialExitoso.value = false
        _mensajeErrorMaterial.value = ""
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


    // ============================================
// SECCIÓN: BORRADORES
// ============================================

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)
    }

    private val _borradores = MutableLiveData<List<Borrador>>()
    val borradores = _borradores

    fun cargarBorradores() {
        viewModelScope.launch {
            try {
                val listaBorradores = sharedPreferencesManager.obtenerBorradores()
                _borradores.postValue(listaBorradores)
            } catch (e: Exception) {
                Log.e("Error Borradores", "Error al cargar: ${e.message}", e)
                _borradores.postValue(emptyList())
            }
        }
    }

    fun guardarBorradorMuerte() {
        try {
            val datosMuerte = mapOf(
                "tipo" to _tipoMuerte.value,
                "identificador" to _identificadorMuerte.value,
                "fecha" to _fechaMuerte.value,
                "mesesGestacion" to _mesesGestacion.value,
                "cadaverInaccesible" to _cadaverInaccesible.value,
                "coordenadaX" to _coordenadaX.value,
                "coordenadaY" to _coordenadaY.value
            )

            val borrador = Borrador(
                id = "muerte_${System.currentTimeMillis()}",
                tipo = "MUERTE",
                fecha = _fechaMuerte.value ?: "",
                datos = Gson().toJson(datosMuerte),
                estado = "PENDIENTE"
            )

            sharedPreferencesManager.guardarBorrador(borrador)
            cargarBorradores()

            Log.d("Borrador Muerte", "Guardado exitosamente")
        } catch (e: Exception) {
            Log.e("Error Borrador Muerte", "Error al guardar: ${e.message}", e)
        }
    }

    fun guardarBorradorMaterial() {
        try {
            val datosMaterial = mapOf(
                "empresaSubministradora" to _empresaSubministradora.value,
                "codigoEmpresa" to _codigoEmpresa.value,
                "tipoEnviamiento" to _tipoEnviamiento.value,
                "destinoLliurament" to _destinoLliurament.value,
                "oficinaComarcal" to _oficinaComarcal.value,
                "direccion" to _direccion.value,
                "poblacion" to _poblacion.value,
                "codigoPostal" to _codigoPostal.value,
                "municipio" to _municipio.value,
                "telefonoContacto" to _telefonoContacto.value,
                "identificadorMaterial" to _identificadorMaterial.value,
                "tipoMaterial" to _tipoMaterial.value,
                "numeroUnidades" to _numeroUnidades.value,
                "codigoExplotacion" to _codigoExplotacion.value
            )

            val borrador = Borrador(
                id = "material_${System.currentTimeMillis()}",
                tipo = "MATERIAL",
                fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                datos = Gson().toJson(datosMaterial),
                estado = "PENDIENTE"
            )

            sharedPreferencesManager.guardarBorrador(borrador)
            cargarBorradores()

            Log.d("Borrador Material", "Guardado exitosamente")
        } catch (e: Exception) {
            Log.e("Error Borrador Material", "Error al guardar: ${e.message}", e)
        }
    }

    fun eliminarBorrador(id: String) {
        viewModelScope.launch {
            try {
                sharedPreferencesManager.eliminarBorrador(id)
                cargarBorradores()
            } catch (e: Exception) {
                Log.e("Error Borrador", "Error al eliminar: ${e.message}", e)
            }
        }
    }

    fun reintentarEnvioBorrador(borrador: Borrador) {
        viewModelScope.launch {
            try {
                // Actualizar estado a "ENVIANDO"
                val borradorActualizado = borrador.copy(estado = "ENVIANDO")
                sharedPreferencesManager.guardarBorrador(borradorActualizado)
                cargarBorradores()

                // Parsear datos
                val gson = Gson()
                val datos: Map<String, Any?> = gson.fromJson(
                    borrador.datos,
                    object : TypeToken<Map<String, Any?>>() {}.type
                )

                when (borrador.tipo) {
                    "MUERTE" -> {
                        // Restaurar datos en el ViewModel
                        _tipoMuerte.value = datos["tipo"] as? String ?: ""
                        _identificadorMuerte.value = datos["identificador"] as? String ?: ""
                        _fechaMuerte.value = datos["fecha"] as? String ?: ""
                        _mesesGestacion.value = datos["mesesGestacion"] as? String ?: ""
                        _cadaverInaccesible.value = datos["cadaverInaccesible"] as? Boolean ?: false
                        _coordenadaX.value = datos["coordenadaX"] as? String ?: ""
                        _coordenadaY.value = datos["coordenadaY"] as? String ?: ""

                        // Intentar enviar
                        putMuerteBovino()

                        // Si fue exitoso, eliminar borrador
                        if (_registroMuerteExitoso.value == true) {
                            eliminarBorrador(borrador.id)
                        }
                    }
                    "MATERIAL" -> {
                        // Restaurar datos
                        _empresaSubministradora.value = datos["empresaSubministradora"] as? String ?: ""
                        _codigoEmpresa.value = datos["codigoEmpresa"] as? String ?: ""
                        _tipoEnviamiento.value = datos["tipoEnviamiento"] as? String ?: ""
                        _destinoLliurament.value = datos["destinoLliurament"] as? String ?: ""
                        _oficinaComarcal.value = datos["oficinaComarcal"] as? String ?: ""
                        _direccion.value = datos["direccion"] as? String ?: ""
                        _poblacion.value = datos["poblacion"] as? String ?: ""
                        _codigoPostal.value = datos["codigoPostal"] as? String ?: ""
                        _municipio.value = datos["municipio"] as? String ?: ""
                        _telefonoContacto.value = datos["telefonoContacto"] as? String ?: ""
                        _identificadorMaterial.value = datos["identificadorMaterial"] as? String ?: ""
                        _tipoMaterial.value = datos["tipoMaterial"] as? String ?: ""
                        _numeroUnidades.value = datos["numeroUnidades"] as? String ?: "1"
                        _codigoExplotacion.value = datos["codigoExplotacion"] as? String ?: ""

                        // Intentar enviar
                        solicitarMaterial()

                        // Si fue exitoso, eliminar borrador
                        if (_registroMaterialExitoso.value == true) {
                            eliminarBorrador(borrador.id)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Reintento", "Error al reintentar envío: ${e.message}", e)

                // Actualizar estado a ERROR
                val borradorError = borrador.copy(estado = "ERROR")
                sharedPreferencesManager.guardarBorrador(borradorError)
                cargarBorradores()
            }
        }
    }


}