package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.local.database.toBorrador

@HiltViewModel
class IdentificacionAplazaViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao,
    private val historialDao: HistorialDao
) : ViewModel() {

    private var borradorSesionId: String = ""

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""
    val codiMo = userPreferences.getCodiMO() ?: ""
    private val _suggestionsBovinos = MutableLiveData<List<Animal>>(emptyList())
    val suggestionsBovinos = _suggestionsBovinos

    private val _isLoadingBovinos = MutableLiveData(false)
    val isLoadingBovinos = _isLoadingBovinos

    private val _bovinosCargados = MutableLiveData(false)
    val bovinosCargados = _bovinosCargados

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

    init {
        borradorSesionId = "identificacion_aplazada_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    private fun cargarBovinosEnCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingBovinos.postValue(true)
                repositorio.getBovinosWithCache(
                    nif = nif,
                    password = password,
                    tipusVinculacio = "1",
                    explotacio = codiMo,
                    forceRefresh = false
                )
                _bovinosCargados.postValue(true)
                _isLoadingBovinos.postValue(false)
                Log.d("IdentificacionAplazaVM", "Bovinos cargados en caché")
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("IdentificacionAplazaVM", "Error al cargar bovinos: ${e.message}", e)
            }
        }
    }

    fun searchBovinos(query: String) {
        if (query.isBlank()) {
            _suggestionsBovinos.value = emptyList()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultados = repositorio.searchBovinosLocal(query)
                _suggestionsBovinos.postValue(resultados)
                Log.d("IdentificacionAplazaVM", "Búsqueda: '$query' - ${resultados.size} resultados")
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e("IdentificacionAplazaVM", "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    fun onBovinoSelected(animal: Animal) {
        _identificadorAnimal.value = animal.identificador
        _suggestionsBovinos.value = emptyList()
        Log.d("IdentificacionAplazaVM", "Bovino seleccionado: ${animal.identificador}")
    }

    fun tieneContenido(): Boolean {
        return !_identificadorAnimal.value.isNullOrEmpty() ||
                !_fechaIdentificacion.value.isNullOrEmpty()
    }


    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "identificador" to _identificadorAnimal.value,
                    "fechaIdentificacion" to _fechaIdentificacion.value
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId, tipo = "IDENTIFICACION_APLAZADA",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos), estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("Error Autoguardado Identificación", "Error al guardar: ${e.message}", e)
            }
        }
    }

    fun cargarBorradorPorId(id: String) {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll().find { it.id == id } ?: return@launch
                borradorSesionId = borrador.id
                val datos: Map<String, Any?> = Gson().fromJson(
                    borrador.datos,
                    object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                )
                _identificadorAnimal.value = datos["identificador"] as? String ?: ""
                _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""
            } catch (e: Exception) {
                Log.e("IdentificacionAplazaVM", "Error al cargar borrador por ID: ${e.message}", e)
            }
        }
    }

    fun eliminarBorradorAutomatico() {
        viewModelScope.launch {
            try {
                if (borradorSesionId.isNotEmpty()) {
                    borradorDao.deleteById(borradorSesionId)
                    borradorSesionId = ""
                }
            } catch (e: Exception) {
                Log.e("Error Eliminar Borrador", "Error: ${e.message}", e)
            }
        }
    }

    fun actualizarIdentificadorAnimal(nuevoId: String) { _identificadorAnimal.value = nuevoId }
    fun mostrarDatePickerIdentificacion() { _mostrarDatePickerIdentificacion.value = true }
    fun ocultarDatePickerIdentificacion() { _mostrarDatePickerIdentificacion.value = false }

    fun seleccionarFechaIdentificacion(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        _fechaIdentificacion.value = String.format(
            "%02d/%02d/%04d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        _mostrarDatePickerIdentificacion.value = false
    }

    fun esFormularioIdentificacionValido(): Boolean {
        return !_identificadorAnimal.value.isNullOrEmpty() &&
                !_fechaIdentificacion.value.isNullOrEmpty()
    }

    fun corregirIdentificacion() {
        _codiError.value = null
        if (!esFormularioIdentificacionValido()) {
            _codiError.value = when {
                _identificadorAnimal.value.isNullOrEmpty() -> 12
                _fechaIdentificacion.value.isNullOrEmpty() -> 13
                else -> 0
            }
            Log.e("Validación de identificacion", "Error: ${_codiError.value}")
            return
        }
        viewModelScope.launch {
            _estadoCarga.value = true
            try {
                val request = PetIdentificacion(
                    identificador = _identificadorAnimal.value ?: "",
                    nif = nif,
                    passwordMobilitat = password,
                    dataIdentificacio = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")
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
                                guardarEnHistorial("Identificación aplazada registrada")
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
                                    _mensajeErrorIdentificacion.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeErrorIdentificacion.value = "Error al procesar respuesta"
                                }
                                Log.e("Error Corrección Identificacion", "Body: $errorBody")
                            }
                            _identificacionExitosa.value = false
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
                    _estadoCarga.value = false; _identificacionExitosa.value = false
                    _mensajeErrorIdentificacion.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Corrección Identificacion", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false; _identificacionExitosa.value = false
                    _mensajeErrorIdentificacion.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Corrección Identificacion", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false; _identificacionExitosa.value = false
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
        borradorSesionId = ""
    }

    fun resetearEstadoIdentificacion() {
        _identificacionExitosa.value = false
        _mensajeErrorIdentificacion.value = ""
        _codiError.value = null
    }

    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "identificador" to _identificadorAnimal.value,
                    "fechaIdentificacion" to _fechaIdentificacion.value
                )
                historialDao.insert(HistorialEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    tipo = "IDENTIFICACION_APLAZADA",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos),
                    resumen = resumen
                ))
            } catch (e: Exception) {
                Log.e("Historial", "Error: ${e.message}", e)
            }
        }
    }

    fun cargarDesdeHistorial(id: String) {
        viewModelScope.launch {
            try {
                val registro = historialDao.getAll().find { it.id == id } ?: return@launch
                val datos: Map<String, Any?> = Gson().fromJson(
                    registro.datos,
                    object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                )
                _identificadorAnimal.value = datos["identificador"] as? String ?: ""
                _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""
            } catch (e: Exception) {
                Log.e("IdentificacionAplazaVM", "Error al cargar desde historial: ${e.message}", e)
            }
        }
    }
}