package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val repositorio = Repositorio()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ID único para la sesión actual del formulario
    private var borradorSesionId: String = ""

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        // Generar nuevo ID de sesión si no existe
        if (borradorSesionId.isEmpty()) {
            borradorSesionId = "nacimiento_auto_${System.currentTimeMillis()}"
        }
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
                "aptitudSeleccionada" to _aptitudSeleccionada.value,
                "codigoRaza" to _codigoRaza.value,
                "sexoApiSeleccionado" to sexoApiSeleccionado,
                "codigoAptitud" to codigoAptitud
            )

            // Buscar si ya existe este borrador específico de la sesión actual
            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == borradorSesionId }

            val borrador = if (borradorExistente != null) {
                // Actualizar borrador de esta sesión
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosNacimiento)
                )
            } else {
                // Crear nuevo borrador con ID de sesión
                Borrador(
                    id = borradorSesionId,
                    tipo = "NACIMIENTO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosNacimiento),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Nacimiento", "Borrador guardado: $borradorSesionId")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Nacimiento", "Error al guardar: ${e.message}", e)
        }
    }

    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()

            // Buscar cualquier borrador de tipo NACIMIENTO con estado BORRADOR_AUTO
            val borradoresNacimiento = borradores.filter {
                it.tipo == "NACIMIENTO" && it.estado == "BORRADOR_AUTO"
            }

            if (borradoresNacimiento.isNotEmpty()) {
                // Tomar el más reciente (último guardado)
                val borradorNacimiento = borradoresNacimiento.maxByOrNull {
                    it.id.substringAfter("nacimiento_auto_").toLongOrNull() ?: 0L
                }

                if (borradorNacimiento != null) {
                    // Asignar este ID a la sesión actual
                    borradorSesionId = borradorNacimiento.id

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
                    _codigoRaza.value = datos["codigoRaza"] as? String ?: ""
                    sexoApiSeleccionado = datos["sexoApiSeleccionado"] as? String ?: "0"
                    codigoAptitud = datos["codigoAptitud"] as? String ?: "0"

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

    fun obtenerBorradoresNacimiento(): List<Borrador> {
        return try {
            sharedPreferencesManager.obtenerBorradores()
                .filter { it.tipo == "NACIMIENTO" && it.estado == "BORRADOR_AUTO" }
        } catch (e: Exception) {
            Log.e("Error", "Error al obtener borradores: ${e.message}", e)
            emptyList()
        }
    }

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

    private val _sexoExpandido = MutableLiveData(false)
    val sexoExpandido = _sexoExpandido

    private val _razaExpandida = MutableLiveData(false)
    val razaExpandida = _razaExpandida

    private val _aptitudExpandida = MutableLiveData(false)
    val aptitudExpandida = _aptitudExpandida

    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker = _mostrarDatePicker

    private val _mostrarDatePickerIdentificacion = MutableLiveData(false)
    val mostrarDatePickerIdentificacion = _mostrarDatePickerIdentificacion

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    private val _cargandoNacimiento = MutableLiveData(false)
    val cargandoNacimiento = _cargandoNacimiento

    data class Razas(val codigo: String, val nombre: String)

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

    fun mostrarDatePicker() {
        _mostrarDatePicker.value = true
    }

    fun ocultarDatePicker() {
        _mostrarDatePicker.value = false
    }

    fun mostrarDatePickerIdentificacion() {
        _mostrarDatePickerIdentificacion.value = true
    }

    fun ocultarDatePickerIdentificacion() {
        _mostrarDatePickerIdentificacion.value = false
    }

    fun seleccionarFecha(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _fechaNacimiento.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePicker.value = false
    }

    fun seleccionarFechaIdentificacion(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _fechaIdentificacion.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePickerIdentificacion.value = false
    }

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

    fun registrarNacimiento() {
        _codiError.value = null

        if (!esFormularioNacimientoValido()) {
            val mensajeError = when {
                _idMadre.value.isNullOrEmpty() -> 1
                _idCria.value.isNullOrEmpty() -> 2
                _fechaNacimiento.value.isNullOrEmpty() -> 3
                _sexoSeleccionado.value.isNullOrEmpty() -> 4
                _razaSeleccionada.value.isNullOrEmpty() -> 5
                _aptitudSeleccionada.value.isNullOrEmpty() -> 6
                else -> 0
            }
            _codiError.value = mensajeError
            Log.e("Validación Nacimiento", "Error: $mensajeError")
            return
        }

        viewModelScope.launch {
            _cargandoNacimiento.postValue(true)

            try {
                val fechaNacimientoAPI = DateUtils.convertirFechaAFormatoAPI(_fechaNacimiento.value ?: "")
                val fechaIdentificacionAPI = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")

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

                val response = repositorio.putRegistrarNacimiento(request)

                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false

                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true
                                _mensajeError.value = ""

                                Log.d("Registro Nacimiento", "Nacimiento reportado exitosamente")

                                eliminarBorradorAutomatico()
                                limpiarFormularioNacimiento()
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                }
                            }
                            _registroExitoso.value = false
                            Log.e("Error Registro Nacimiento", "HTTP ${response.code()}")
                        }
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Registro Nacimiento", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Nacimiento", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Nacimiento", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
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

    fun limpiarFormularioNacimiento() {
        _idMadre.value = ""
        _idCria.value = ""
        _fechaNacimiento.value = ""
        _fechaIdentificacion.value = ""
        _sexoSeleccionado.value = ""
        _razaSeleccionada.value = ""
        _codigoRaza.value = ""
        _aptitudSeleccionada.value = ""
        sexoApiSeleccionado = "0"
        codigoAptitud = "0"

        // Generar nuevo ID de sesión para el próximo formulario
        borradorSesionId = ""
    }

    fun resetearEstadoRegistro() {
        _registroExitoso.value = false
        _mensajeError.value = ""
        _codiError.value = null
    }

    fun validarIdentificador(id: String): Boolean {
        return id.length >= 5
    }

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