package com.example.terrabit_app.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IdentificacionAplazaViewModel (application: Application): AndroidViewModel(application) {

    private var repositorio = Repositorio(application)
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ID único para la sesión actual del formulario
    private var borradorSesionId: String = ""

    // Instanciar UserPreferences directamente con la Application
    private val userPreferences = UserPreferences(application)

    // Leer las credenciales del login guardadas automáticamente
    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        // Generar nuevo ID de sesión si no existe
        if (borradorSesionId.isEmpty()) {
            borradorSesionId = "identificacion_aplazada_auto_${System.currentTimeMillis()}"
        }
    }

    fun tieneContenido(): Boolean {
        return !_identificadorAnimal.value.isNullOrEmpty() ||
                !_fechaIdentificacion.value.isNullOrEmpty()
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) {
            Log.d("Autoguardado Identificación", "No hay contenido para guardar")
            return
        }

        try {
            val datosIdentificacion = mapOf(
                "identificador" to _identificadorAnimal.value,
                "fechaIdentificacion" to _fechaIdentificacion.value
            )

            // Buscar si ya existe este borrador específico de la sesión actual
            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == borradorSesionId }

            val borrador = if (borradorExistente != null) {
                // Actualizar borrador de esta sesión
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosIdentificacion)
                )
            } else {
                // Crear nuevo borrador con ID de sesión
                Borrador(
                    id = borradorSesionId,
                    tipo = "IDENTIFICACION_APLAZADA",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosIdentificacion),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Identificación", "Borrador guardado: $borradorSesionId")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Identificación", "Error al guardar: ${e.message}", e)
        }
    }

    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()

            // Buscar cualquier borrador de tipo IDENTIFICACION_APLAZADA con estado BORRADOR_AUTO
            val borradoresIdentificacion = borradores.filter {
                it.tipo == "IDENTIFICACION_APLAZADA" && it.estado == "BORRADOR_AUTO"
            }

            if (borradoresIdentificacion.isNotEmpty()) {
                // Tomar el más reciente (último guardado)
                val borradorIdentificacion = borradoresIdentificacion.maxByOrNull {
                    it.id.substringAfter("identificacion_aplazada_auto_").toLongOrNull() ?: 0L
                }

                if (borradorIdentificacion != null) {
                    // Asignar este ID a la sesión actual
                    borradorSesionId = borradorIdentificacion.id

                    val gson = Gson()
                    val datos: Map<String, Any?> = gson.fromJson(
                        borradorIdentificacion.datos,
                        object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                    )

                    // Restaurar datos
                    _identificadorAnimal.value = datos["identificador"] as? String ?: ""
                    _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""

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

    fun obtenerBorradoresIdentificacionAplazada(): List<Borrador> {
        return try {
            sharedPreferencesManager.obtenerBorradores()
                .filter { it.tipo == "IDENTIFICACION_APLAZADA" && it.estado == "BORRADOR_AUTO" }
        } catch (e: Exception) {
            Log.e("Error", "Error al obtener borradores: ${e.message}", e)
            emptyList()
        }
    }

    private val _identificadorAnimal = MutableLiveData("")
    val identificadorAnimal = _identificadorAnimal

    private val _fechaIdentificacion = MutableLiveData("")
    val fechaIdentificacion = _fechaIdentificacion

    private val _mostrarDatePickerIdentificacion = MutableLiveData(false)
    val mostrarDatePickerIdentificacion = _mostrarDatePickerIdentificacion

    private val _identificacionExitosa = MutableLiveData<Boolean>()
    val identificacionExitosa = _identificacionExitosa

    private val _mensajeErrorIdentificacion = MutableLiveData<String>()
    val mensajeErrorIdentificacion = _mensajeErrorIdentificacion

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    private val _estadoCarga = MutableLiveData(false)
    val estadoCarga = _estadoCarga

    fun actualizarIdentificadorAnimal(nuevoId: String) {
        _identificadorAnimal.value = nuevoId
    }

    fun mostrarDatePickerIdentificacion() {
        _mostrarDatePickerIdentificacion.value = true
    }

    fun ocultarDatePickerIdentificacion() {
        _mostrarDatePickerIdentificacion.value = false
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

    fun esFormularioIdentificacionValido(): Boolean {
        val identificadorValido = !_identificadorAnimal.value.isNullOrEmpty()
        val fechaValida = !_fechaIdentificacion.value.isNullOrEmpty()
        return identificadorValido && fechaValida
    }

    fun corregirIdentificacion() {
        _codiError.value = null

        if (!esFormularioIdentificacionValido()) {
            val mensajeError = when {
                _identificadorAnimal.value.isNullOrEmpty() -> 12
                _fechaIdentificacion.value.isNullOrEmpty() -> 13
                else -> 0
            }
            _codiError.value = mensajeError
            Log.e("Validación de identificacion", "Error: $mensajeError")
            return
        }

        viewModelScope.launch {
            _estadoCarga.value = true
            try {
                val fechaIdentificacionAPI = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")

                val request = PetIdentificacion(
                    identificador = _identificadorAnimal.value ?: "",
                    nif = nif,
                    passwordMobilitat = password,
                    dataIdentificacio = fechaIdentificacionAPI
                )

                Log.d("Corrección Identificacion", "Request: $request")

                val response = repositorio.putIdentificacionPendiente(request)

                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _identificacionExitosa.value = true
                                _mensajeErrorIdentificacion.value = ""

                                Log.d("Corrección Identificacion", "Identificación corregida exitosamente")

                                eliminarBorradorAutomatico()
                                limpiarFormulario()
                            } else {
                                _identificacionExitosa.value = false
                                _mensajeErrorIdentificacion.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Corrección Identificacion", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeErrorIdentificacion.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeErrorIdentificacion.value = "Error al procesar respuesta"
                                }
                            }
                            _identificacionExitosa.value = false
                            if (errorBody != null) {
                                Log.e("Error Corrección Identificacion", "Body: $errorBody")
                            }
                        }
                        else -> {
                            _identificacionExitosa.value = false
                            _mensajeErrorIdentificacion.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Corrección Identificacion", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _identificacionExitosa.value = false
                    _mensajeErrorIdentificacion.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Corrección Identificacion", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _identificacionExitosa.value = false
                    _mensajeErrorIdentificacion.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Corrección Identificacion", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _identificacionExitosa.value = false
                    _mensajeErrorIdentificacion.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Corrección Identificacion", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun limpiarFormulario() {
        _identificadorAnimal.value = ""
        _fechaIdentificacion.value = ""

        // Generar nuevo ID de sesión para el próximo formulario
        borradorSesionId = ""
    }

    fun resetearEstadoIdentificacion() {
        _identificacionExitosa.value = false
        _mensajeErrorIdentificacion.value = ""
        _codiError.value = null
    }
}