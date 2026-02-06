package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.R
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NacimientoViewmodel : ViewModel() {

    // Instancia del repositorio
    private val repositorio = Repositorio()

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ============================================
    // SECCIÓN: AUTOGUARDADO
    // ============================================

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)
    }

    fun tieneContenido(): Boolean {
        return !_idMadre.value.isNullOrEmpty() ||
                !_idCria.value.isNullOrEmpty() ||
                !_fechaNacimiento.value.isNullOrEmpty() ||
                !_fechaIdentificacion.value.isNullOrEmpty() ||
                !_sexoSeleccionado.value.isNullOrEmpty() ||
                !_razaSeleccionada.value.isNullOrEmpty() ||
                !_aptitudSeleccionada.value.isNullOrEmpty()
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) {
            Log.d("Autoguardado Nacimiento", "No hay contenido para guardar")
            return
        }

        try {
            val datosNacimiento = mapOf(
                "idMadre" to _idMadre.value,
                "idCria" to _idCria.value,
                "fechaNacimiento" to _fechaNacimiento.value,
                "fechaIdentificacion" to _fechaIdentificacion.value,
                "sexoSeleccionado" to _sexoSeleccionado.value,
                "razaSeleccionada" to _razaSeleccionada.value,
                "aptitudSeleccionada" to _aptitudSeleccionada.value
            )

            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.tipo == "NACIMIENTO" && it.estado == "BORRADOR_AUTO" }

            val borrador = if (borradorExistente != null) {
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosNacimiento)
                )
            } else {
                Borrador(
                    id = "nacimiento_auto_${System.currentTimeMillis()}",
                    tipo = "NACIMIENTO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosNacimiento),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Nacimiento", "Borrador guardado automáticamente")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Nacimiento", "Error al guardar: ${e.message}", e)
        }
    }

    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()
            val borradorNacimiento = borradores.find {
                it.tipo == "NACIMIENTO" && it.estado == "BORRADOR_AUTO"
            }

            if (borradorNacimiento != null) {
                val gson = Gson()
                val datos: Map<String, Any?> = gson.fromJson(
                    borradorNacimiento.datos,
                    object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                )

                _idMadre.value = datos["idMadre"] as? String ?: ""
                _idCria.value = datos["idCria"] as? String ?: ""
                _fechaNacimiento.value = datos["fechaNacimiento"] as? String ?: ""
                _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""
                _sexoSeleccionado.value = datos["sexoSeleccionado"] as? String ?: ""
                _razaSeleccionada.value = datos["razaSeleccionada"] as? String ?: ""
                _aptitudSeleccionada.value = datos["aptitudSeleccionada"] as? String ?: ""

                Log.d("Cargar Borrador", "Borrador de nacimiento cargado")
            }
        } catch (e: Exception) {
            Log.e("Error Cargar Borrador", "Error al cargar: ${e.message}", e)
        }
    }

    fun eliminarBorradorAutomatico() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()
            val borradorNacimiento = borradores.find {
                it.tipo == "NACIMIENTO" && it.estado == "BORRADOR_AUTO"
            }

            if (borradorNacimiento != null) {
                sharedPreferencesManager.eliminarBorrador(borradorNacimiento.id)
                Log.d("Eliminar Borrador", "Borrador automático eliminado")
            }
        } catch (e: Exception) {
            Log.e("Error Eliminar Borrador", "Error: ${e.message}", e)
        }
    }

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

    private var sexoApiSeleccionado = "0"

    private var codigoAptitud = "0"
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

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    // Estado de carga - NUEVO
    private val _cargandoNacimiento = MutableLiveData(false)
    val cargandoNacimiento = _cargandoNacimiento

    // Data class para Razas
    data class Razas(val codigo: String, val nombre: String)

    // Listas de opciones - Nacimiento

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

    fun seleccionarSexo(sexo: String, codigo: String) {
        _sexoSeleccionado.value = sexo
        sexoApiSeleccionado = codigo
        _sexoExpandido.value = false
    }

    fun seleccionarRaza(raza: String, codigo: String) {
        _razaSeleccionada.value = raza
        _codigoRaza.value = codigo
        _razaExpandida.value = false
    }

    fun seleccionarAptitud(aptitud: String, codigo: String) {
        _aptitudSeleccionada.value = aptitud
        codigoAptitud = codigo
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
        // Resetear mensaje de error
        _codiError.value = null
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioNacimientoValido()) {
            val mensajeError = when {
                _idMadre.value.isNullOrEmpty() ->
                    1
                _idCria.value.isNullOrEmpty() ->
                    2
                _fechaNacimiento.value.isNullOrEmpty() ->
                    3
                _sexoSeleccionado.value.isNullOrEmpty() ->
                    4
                _razaSeleccionada.value.isNullOrEmpty() ->
                    5
                _aptitudSeleccionada.value.isNullOrEmpty() ->
                    6
                else ->
                    0
            }
            _codiError.value = mensajeError
            Log.e("Validación Nacimiento", "Error: $mensajeError")
            return
        }

        viewModelScope.launch {
            // Activar indicador de carga
            _cargandoNacimiento.postValue(true)

            try {
                // Convertir fechas a formato API (yyyymmdd)
                val fechaNacimientoAPI = DateUtils.convertirFechaAFormatoAPI(_fechaNacimiento.value ?: "")
                val fechaIdentificacionAPI = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")



                // Crear objeto de petición
                val request = RegistroNacimientoBovi(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    identificador = _idCria.value ?: "",
                    identificadorMare = _idMadre.value ?: "",
                    dataNaixement = fechaNacimientoAPI,
                    dataIdentificacio = fechaIdentificacionAPI,
                    sexe = sexoApiSeleccionado,
                    raca = _codigoRaza.value ?: "",
                    aptitud = codigoAptitud
                )
                Log.d("Registro Nacimiento", "Request: $request")

                // Llamar a la API
                val response = repositorio.putRegistrarNacimiento(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    // Desactivar indicador de carga
                    _cargandoNacimiento.value = false

                    when {
                        // Caso: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // Verificar si es respuesta exitosa (codi = "0")
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true
                                _mensajeError.value = ""

                                Log.d("Registro Nacimiento", "Nacimiento reportado exitosamente")
                                Log.d("Registro Nacimiento", "Respuesta: [${body.codi}] ${body.descripcio}")

                                // ELIMINAR BORRADOR AUTOMÁTICO AL ENVIAR EXITOSAMENTE
                                eliminarBorradorAutomatico()

                                // Limpiar formulario después de registrar exitosamente
                                limpiarFormularioNacimiento()
                            }
                        }
                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    // Cogemos la descripción del primer error, o un mensaje por defecto si está vacía
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                }
                            }
                            _registroExitoso.value = false
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
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Nacimiento", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                // Error de red
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Nacimiento", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                // Otros errores
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Registro Nacimiento", "Error general: ${e.message}", e)
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
        return id.length >= 5
    }




    // Convierte una fecha de formato "yyyymmdd" a "dd/MM/yyyy"
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