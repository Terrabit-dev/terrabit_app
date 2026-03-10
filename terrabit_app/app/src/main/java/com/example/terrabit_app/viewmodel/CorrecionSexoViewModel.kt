package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.toBorrador

@HiltViewModel
class CorrecionSexoViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao
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

    private val _identificadorCorreccionSexo = MutableLiveData("")
    val identificadorCorreccionSexo = _identificadorCorreccionSexo

    private val _sexoCorreccionSeleccionado = MutableLiveData("")
    val sexoCorreccionSeleccionado = _sexoCorreccionSeleccionado

    private val _sexoCorreccionExpandido = MutableLiveData(false)
    val sexoCorreccionExpandido = _sexoCorreccionExpandido

    private val _correccionSexoExitosa = MutableLiveData<Boolean>()
    val correccionSexoExitosa = _correccionSexoExitosa

    private val _mensajeErrorCorreccionSexo = MutableLiveData<String>()
    val mensajeErrorCorreccionSexo = _mensajeErrorCorreccionSexo

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    private val _estadoCarga = MutableLiveData(false)
    val estadoCarga = _estadoCarga

    val listaSexos = listOf("Macho", "Hembra")
    private var codigoSexo = ""

    init {
        borradorSesionId = "correccion_sexo_auto_${System.currentTimeMillis()}"
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
                Log.d("CorrecionSexoVM", "Bovinos cargados en caché")
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("CorrecionSexoVM", "Error al cargar bovinos: ${e.message}", e)
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
                Log.d("CorrecionSexoVM", "Búsqueda: '$query' - ${resultados.size} resultados")
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e("CorrecionSexoVM", "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    fun onBovinoSelected(animal: Animal) {
        _identificadorCorreccionSexo.value = animal.identificador
        _suggestionsBovinos.value = emptyList()
        Log.d("CorrecionSexoVM", "Bovino seleccionado: ${animal.identificador}")
    }

    fun tieneContenido(): Boolean {
        return !_identificadorCorreccionSexo.value.isNullOrEmpty() ||
                !_sexoCorreccionSeleccionado.value.isNullOrEmpty()
    }


    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "identificador" to _identificadorCorreccionSexo.value,
                    "sexoSeleccionado" to _sexoCorreccionSeleccionado.value,
                    "codigoSexo" to codigoSexo
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId, tipo = "CORRECCION_SEXO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos), estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("Error Autoguardado Correcion Sexo", "Error al guardar: ${e.message}", e)
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
                _identificadorCorreccionSexo.value = datos["identificador"] as? String ?: ""
                _sexoCorreccionSeleccionado.value = datos["sexoSeleccionado"] as? String ?: ""
                codigoSexo = datos["codigoSexo"] as? String ?: ""
            } catch (e: Exception) {
                Log.e("CorrecionSexoVM", "Error al cargar borrador por ID: ${e.message}", e)
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

    fun cargarBorradorExistente() {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll()
                    .filter { it.tipo == "CORRECCION_SEXO" && it.estado == "BORRADOR_AUTO" }
                    .maxByOrNull { it.id.substringAfter("correccion_sexo_auto_").toLongOrNull() ?: 0L }
                    ?: return@launch
                borradorSesionId = borrador.id
                val datos: Map<String, Any?> = Gson().fromJson(
                    borrador.datos,
                    object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
                )
                _identificadorCorreccionSexo.value = datos["identificador"] as? String ?: ""
                _sexoCorreccionSeleccionado.value = datos["sexoSeleccionado"] as? String ?: ""
                codigoSexo = datos["codigoSexo"] as? String ?: ""
            } catch (e: Exception) {
                Log.e("Error Cargar Borrador", "Error al cargar: ${e.message}", e)
            }
        }
    }





    fun actualizarIdentificadorCorreccionSexo(nuevoId: String) { _identificadorCorreccionSexo.value = nuevoId }

    fun seleccionarSexoCorreccion(sexo: String, codigo: String) {
        _sexoCorreccionSeleccionado.value = sexo
        codigoSexo = codigo
        _sexoCorreccionExpandido.value = false
    }

    fun toggleSexoCorreccionExpandido() { _sexoCorreccionExpandido.value = !(_sexoCorreccionExpandido.value ?: false) }
    fun cerrarSexoCorreccionMenu() { _sexoCorreccionExpandido.value = false }

    fun esFormularioCorreccionSexoValido(): Boolean {
        return !_identificadorCorreccionSexo.value.isNullOrEmpty() &&
                !_sexoCorreccionSeleccionado.value.isNullOrEmpty()
    }

    fun corregirSexoAnimal() {
        _codiError.value = null
        if (!esFormularioCorreccionSexoValido()) {
            _codiError.value = when {
                _identificadorCorreccionSexo.value.isNullOrEmpty() -> 12
                _sexoCorreccionSeleccionado.value.isNullOrEmpty() -> 4
                else -> 0
            }
            Log.e("Validación Corrección Sexo", "Formulario no válido: ${_codiError.value}")
            return
        }
        viewModelScope.launch {
            _estadoCarga.value = true
            try {
                val request = PetModicarAnimal(
                    identificador = _identificadorCorreccionSexo.value ?: "",
                    nif = nif,
                    passwordMobilitat = password,
                    sexe = codigoSexo
                )
                Log.d("Corrección Sexo", "Request: $request")
                val response = repositorio.putMoficarAnimal(request)
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _correccionSexoExitosa.value = true
                                _mensajeErrorCorreccionSexo.value = ""
                                Log.d("Corrección Sexo", "Sexo corregido exitosamente")
                                eliminarBorradorAutomatico()
                                limpiarFormularioCorreccionSexo()
                            } else {
                                _correccionSexoExitosa.value = false
                                _mensajeErrorCorreccionSexo.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Corrección Sexo", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeErrorCorreccionSexo.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeErrorCorreccionSexo.value = "Error al procesar respuesta"
                                }
                                Log.e("Error Corrección Sexo", "Body: $errorBody")
                            }
                            _correccionSexoExitosa.value = false
                        }
                        else -> {
                            _correccionSexoExitosa.value = false
                            _mensajeErrorCorreccionSexo.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Corrección Sexo", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false; _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Corrección Sexo", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false; _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Corrección Sexo", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false; _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Corrección Sexo", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun limpiarFormularioCorreccionSexo() {
        _identificadorCorreccionSexo.value = ""
        _sexoCorreccionSeleccionado.value = ""
        codigoSexo = ""
        borradorSesionId = ""
    }

    fun resetearEstadoCorreccionSexo() {
        _correccionSexoExitosa.value = false
        _mensajeErrorCorreccionSexo.value = ""
        _codiError.value = null
    }
}