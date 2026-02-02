package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class IdentificacionAplazaViewModel : ViewModel() {
    // ============================================
    // SECCIÓN: Identifiacion aplazada
    // ============================================
    private val repositorio = Repositorio()

    private val _identificadorAnimal = MutableLiveData("")
    val identificadorAnimal = _identificadorAnimal

    private val _fechaIdentificacion = MutableLiveData("")
    val fechaIdentificacion = _fechaIdentificacion

    // Estados de expansión de menús desplegables
    private val _mostrarDatePickerIdentificacion = MutableLiveData(false)
    val mostrarDatePickerIdentificacion = _mostrarDatePickerIdentificacion


    // Estados para feedback del registro
    private val _identificacionExitosa = MutableLiveData<Boolean>()
    val identificacionExitosa = _identificacionExitosa

    private val _mensajeErrorIdentificacion = MutableLiveData<String>()
    val mensajeErrorIdentificacion = _mensajeErrorIdentificacion

    private val _estadoCarga = MutableLiveData(false)
    val estadoCarga = _estadoCarga

    // Funciones para actualizar los campos
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

    // Función para validar el formulario
    fun esFormularioIdentificacionValido(): Boolean {
        val identificadorValido = !_identificadorAnimal.value.isNullOrEmpty()
        val fechaValida = !_identificadorAnimal.value.isNullOrEmpty()
        return identificadorValido && fechaValida
    }

    // Función para identificar el animal
    fun corregirIdentificacion() {
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioIdentificacionValido()) {
            val mensajeError = when {
                _identificadorAnimal.value.isNullOrEmpty() ->
                    "Por favor, introduzca el identificador del animal"
                _fechaIdentificacion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la fecha de indentificación del animal"
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
                // Convertir fecha al formato de la API
                val fechaIdentificacionAPI = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")

                // Crear objeto de petición
                val request = PetIdentificacion(
                    identificador = _identificadorAnimal.value ?: "",
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    dataIdentificacio = fechaIdentificacionAPI
                )

                Log.d("Corrección Identificacion", "Enviando petición a la API...")
                Log.d("Corrección Identificacion", "Request: $request")

                // Llamar a la API
                val response = repositorio.putIdentificacionPendiente(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    when {
                        // Caso 1: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            // Verificar si es respuesta exitosa (codi = "0")
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _identificacionExitosa.value = true
                                _mensajeErrorIdentificacion.value = ""
                                // Limpiar formulario después de corregir exitosamente
                                limpiarFormulario()
                            }
                            // Caso inesperado
                            else {
                                _identificacionExitosa.value = false
                                _mensajeErrorIdentificacion.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Corrección Identificacion", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    // Cogemos la descripción del primer error, o un mensaje por defecto si está vacía
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

                        // Caso 3: Respuesta exitosa pero sin body
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

    // Función para limpiar el formulario
    fun limpiarFormulario() {
        _identificadorAnimal.value = ""
        _fechaIdentificacion.value = ""
    }

    // Función para resetear el estado de registro
    fun resetearEstadoIdentificacion() {
        _identificacionExitosa.value = false
        _mensajeErrorIdentificacion.value = ""
    }
}