package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IdentificacionAplazaViewModel : ViewModel() {

    private val repositorio = Repositorio()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ============================================
    // SECCIÓN: AUTOGUARDADO
    // ============================================

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)
    }

    // Detecta si el formulario tiene datos
    fun tieneContenido(): Boolean {
        return !_identificadorAnimal.value.isNullOrEmpty() ||
                !_fechaIdentificacion.value.isNullOrEmpty()
    }

    // Guarda automáticamente el formulario
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

            // Buscar si ya existe un borrador de identificación aplazada
            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.tipo == "IDENTIFICACION_APLAZADA" && it.estado == "BORRADOR_AUTO" }

            val borrador = if (borradorExistente != null) {
                // Actualizar borrador existente
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosIdentificacion)
                )
            } else {
                // Crear nuevo borrador
                Borrador(
                    id = "identificacion_aplazada_auto_${System.currentTimeMillis()}",
                    tipo = "IDENTIFICACION_APLAZADA",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosIdentificacion),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Identificación", "Borrador guardado automáticamente")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Identificación", "Error al guardar: ${e.message}", e)
        }
    }

    // Cargar borrador existente
    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()
            val borradorIdentificacion = borradores.find {
                it.tipo == "IDENTIFICACION_APLAZADA" && it.estado == "BORRADOR_AUTO"
            }

            if (borradorIdentificacion != null) {
                val gson = Gson()
                val datos: Map<String, Any?> = gson.fromJson(
                    borradorIdentificacion.datos,
                    object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                )

                // Restaurar datos
                _identificadorAnimal.value = datos["identificador"] as? String ?: ""
                _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""

                Log.d("Cargar Borrador", "Borrador de identificación aplazada cargado")
            }
        } catch (e: Exception) {
            Log.e("Error Cargar Borrador", "Error al cargar: ${e.message}", e)
        }
    }

    // Eliminar borrador al enviar exitosamente
    fun eliminarBorradorAutomatico() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()
            val borradorIdentificacion = borradores.find {
                it.tipo == "IDENTIFICACION_APLAZADA" && it.estado == "BORRADOR_AUTO"
            }

            if (borradorIdentificacion != null) {
                sharedPreferencesManager.eliminarBorrador(borradorIdentificacion.id)
                Log.d("Eliminar Borrador", "Borrador automático eliminado")
            }
        } catch (e: Exception) {
            Log.e("Error Eliminar Borrador", "Error: ${e.message}", e)
        }
    }

    // ============================================
    // SECCIÓN: IDENTIFICACIÓN APLAZADA
    // ============================================

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
        if (!esFormularioIdentificacionValido()) {
            val mensajeError = when {
                _identificadorAnimal.value.isNullOrEmpty() ->
                    "Por favor, introduzca el identificador del animal"
                _fechaIdentificacion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la fecha de identificación del animal"
                else ->
                    "Por favor, complete todos los campos obligatorios marcados con *"
            }
            _mensajeErrorIdentificacion.value = mensajeError
            Log.e("Validación de identificacion", mensajeError)
            return
        }

        viewModelScope.launch {
            _estadoCarga.value = true
            try {
                val fechaIdentificacionAPI = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")

                val request = PetIdentificacion(
                    identificador = _identificadorAnimal.value ?: "",
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    dataIdentificacio = fechaIdentificacionAPI
                )

                Log.d("Corrección Identificacion", "Enviando petición a la API...")
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

                                // ELIMINAR BORRADOR AUTOMÁTICO AL ENVIAR EXITOSAMENTE
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
    }

    fun resetearEstadoIdentificacion() {
        _identificacionExitosa.value = false
        _mensajeErrorIdentificacion.value = ""
    }
}