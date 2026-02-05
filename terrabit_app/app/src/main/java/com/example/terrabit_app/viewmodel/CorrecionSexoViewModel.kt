package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CorrecionSexoViewModel: ViewModel() {

    // ============================================
    // SECCIÓN: CORRECCIÓN DE SEXO
    // ============================================
    private val repositorio = Repositorio()
    // Estados del formulario de corrección de sexo
    private val _identificadorCorreccionSexo = MutableLiveData("")
    val identificadorCorreccionSexo = _identificadorCorreccionSexo

    private val _sexoCorreccionSeleccionado = MutableLiveData("")
    val sexoCorreccionSeleccionado = _sexoCorreccionSeleccionado

    // Estados de expansión de menús desplegables
    private val _sexoCorreccionExpandido = MutableLiveData(false)
    val sexoCorreccionExpandido = _sexoCorreccionExpandido

    // Estados para feedback del registro
    private val _correccionSexoExitosa = MutableLiveData<Boolean>()
    val correccionSexoExitosa = _correccionSexoExitosa

    private val _mensajeErrorCorreccionSexo = MutableLiveData<String>()
    val mensajeErrorCorreccionSexo = _mensajeErrorCorreccionSexo

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError


    private val _estadoCarga = MutableLiveData(false)
    val estadoCarga = _estadoCarga

    // Lista de opciones de sexo (AGREGADO)
    val listaSexos = listOf("Macho", "Hembra")
    private var codigoSexo = ""

    // Funciones para actualizar los campos
    fun actualizarIdentificadorCorreccionSexo(nuevoId: String) {
        _identificadorCorreccionSexo.value = nuevoId
    }

    fun seleccionarSexoCorreccion(sexo: String, codigo: String) {
        _sexoCorreccionSeleccionado.value = sexo
        codigoSexo = codigo
        _sexoCorreccionExpandido.value = false
    }

    // Funciones para controlar la expansión de menús
    fun toggleSexoCorreccionExpandido() {
        _sexoCorreccionExpandido.value = !(_sexoCorreccionExpandido.value ?: false)
    }

    fun cerrarSexoCorreccionMenu() {
        _sexoCorreccionExpandido.value = false
    }

    // Función para validar el formulario
    fun esFormularioCorreccionSexoValido(): Boolean {
        val identificadorValido = !_identificadorCorreccionSexo.value.isNullOrEmpty()
        val sexoValido = !_sexoCorreccionSeleccionado.value.isNullOrEmpty()
        return identificadorValido && sexoValido
    }

    // Función para corregir el sexo del animal
    fun corregirSexoAnimal() {
        // Resetear mensaje de error
        _codiError.value = null

        // Validar que todos los campos requeridos estén completos
        if (!esFormularioCorreccionSexoValido()) {
            val mensajeError = when {
                _identificadorCorreccionSexo.value.isNullOrEmpty() ->
                    12
                _sexoCorreccionSeleccionado.value.isNullOrEmpty() ->
                   4
                else ->
                   0
            }
            _codiError.value = mensajeError
            Log.e("Validación Corrección Sexo", "Formulario no válido: $mensajeError")
            return
        }

        viewModelScope.launch {
            _estadoCarga.value = true
            try {


                // Crear objeto de petición
                val request = PetModicarAnimal(
                    identificador = _identificadorCorreccionSexo.value ?: "",
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    sexe = codigoSexo
                )

                Log.d("Corrección Sexo", "Enviando petición a la API...")
                Log.d("Corrección Sexo", "Request: $request")

                // Llamar a la API
                val response = repositorio.putMoficarAnimal(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    when {
                        // Caso 1: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            // Verificar si es respuesta exitosa (codi = "0")
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _correccionSexoExitosa.value = true
                                _mensajeErrorCorreccionSexo.value = ""
                                // Limpiar formulario después de corregir exitosamente
                                limpiarFormularioCorreccionSexo()
                            }
                            // Caso inesperado
                            else {
                                _correccionSexoExitosa.value = false
                                _mensajeErrorCorreccionSexo.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Corrección Sexo", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    // Cogemos la descripción del primer error, o un mensaje por defecto si está vacía
                                    _mensajeErrorCorreccionSexo.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeErrorCorreccionSexo.value = "Error al procesar respuesta"
                                }
                            }
                            _correccionSexoExitosa.value = false
                            if (errorBody != null) {
                                Log.e("Error Corrección Sexo", "Body: $errorBody")
                            }
                        }

                        // Caso 3: Respuesta exitosa pero sin body
                        else -> {
                            _correccionSexoExitosa.value = false
                            _mensajeErrorCorreccionSexo.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Corrección Sexo", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Corrección Sexo", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Corrección Sexo", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Corrección Sexo", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    // Función para limpiar el formulario
    fun limpiarFormularioCorreccionSexo() {
        _identificadorCorreccionSexo.value = ""
        _sexoCorreccionSeleccionado.value = ""
    }

    // Función para resetear el estado de registro
    fun resetearEstadoCorreccionSexo() {
        _correccionSexoExitosa.value = false
        _mensajeErrorCorreccionSexo.value = ""
    }
}