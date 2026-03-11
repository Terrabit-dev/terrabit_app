package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
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
class ViewModelMuerteBovi @Inject constructor(
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

    private val _tipoMuerte = MutableLiveData("")
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

    private val _tipoMuerteExpandido = MutableLiveData(false)
    val tipoMuerteExpandido = _tipoMuerteExpandido

    private val _mostrarDatePickerMuerte = MutableLiveData(false)
    val mostrarDatePickerMuerte = _mostrarDatePickerMuerte

    private val _registroMuerteExitoso = MutableLiveData<Boolean>()
    val registroMuerteExitoso = _registroMuerteExitoso

    private val _mensajeErrorMuerte = MutableLiveData<String>()
    val mensajeErrorMuerte = _mensajeErrorMuerte

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    private val _cargandoMuerte = MutableLiveData(false)
    val cargandoMuerte = _cargandoMuerte

    private val _codigoTipoMuerte = MutableLiveData<String>()
    val codigoTipoMuerte = _codigoTipoMuerte

    init {
        borradorSesionId = "muerte_auto_${System.currentTimeMillis()}"
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
                Log.d("MuerteVM", "Bovinos cargados en caché")
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("MuerteVM", "Error al cargar bovinos: ${e.message}", e)
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
                Log.d("MuerteVM", "Búsqueda: '$query' - ${resultados.size} resultados")
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e("MuerteVM", "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    fun onBovinoSelected(animal: Animal) {
        _identificadorMuerte.value = animal.identificador
        _suggestionsBovinos.value = emptyList()
        Log.d("MuerteVM", "Bovino seleccionado: ${animal.identificador}")
    }

    fun tieneContenido(): Boolean {
        return !_tipoMuerte.value.isNullOrEmpty() ||
                !_identificadorMuerte.value.isNullOrEmpty() ||
                !_fechaMuerte.value.isNullOrEmpty() ||
                !_mesesGestacion.value.isNullOrEmpty() ||
                _cadaverInaccesible.value == true ||
                !_coordenadaX.value.isNullOrEmpty() ||
                !_coordenadaY.value.isNullOrEmpty()
    }


    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "tipo" to _tipoMuerte.value,
                    "codigoTipo" to _codigoTipoMuerte.value,
                    "identificador" to _identificadorMuerte.value,
                    "fecha" to _fechaMuerte.value,
                    "mesesGestacion" to _mesesGestacion.value,
                    "cadaverInaccesible" to _cadaverInaccesible.value,
                    "coordenadaX" to _coordenadaX.value,
                    "coordenadaY" to _coordenadaY.value
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId, tipo = "MUERTE",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos), estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("Error Autoguardado Muerte", "Error al guardar: ${e.message}", e)
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
                _tipoMuerte.value = datos["tipo"] as? String ?: ""
                _codigoTipoMuerte.value = datos["codigoTipo"] as? String ?: ""
                _identificadorMuerte.value = datos["identificador"] as? String ?: ""
                _fechaMuerte.value = datos["fecha"] as? String ?: ""
                _mesesGestacion.value = datos["mesesGestacion"] as? String ?: ""
                _cadaverInaccesible.value = datos["cadaverInaccesible"] as? Boolean ?: false
                _coordenadaX.value = datos["coordenadaX"] as? String ?: ""
                _coordenadaY.value = datos["coordenadaY"] as? String ?: ""
            } catch (e: Exception) {
                Log.e("MuerteVM", "Error al cargar borrador por ID: ${e.message}", e)
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

    fun seleccionarTipoMuerte(tipo: String, codigo: String) {
        _tipoMuerte.value = tipo
        _codigoTipoMuerte.value = codigo
        _tipoMuerteExpandido.value = false
        if (tipo.contains("Mort")) _mesesGestacion.value = ""
    }

    fun actualizarIdentificadorMuerte(nuevoId: String) { _identificadorMuerte.value = nuevoId }

    fun actualizarMesesGestacion(valor: String) {
        if (valor.isEmpty() || (valor.toIntOrNull() in 1..9)) _mesesGestacion.value = valor
    }

    fun toggleCadaverInaccesible() {
        val nuevoValor = !(_cadaverInaccesible.value ?: false)
        _cadaverInaccesible.value = nuevoValor
        if (!nuevoValor) { _coordenadaX.value = ""; _coordenadaY.value = "" }
    }

    fun actualizarCoordenadaX(valor: String) { _coordenadaX.value = valor }
    fun actualizarCoordenadaY(valor: String) { _coordenadaY.value = valor }
    fun toggleTipoMuerteExpandido() { _tipoMuerteExpandido.value = !(_tipoMuerteExpandido.value ?: false) }
    fun cerrarTipoMuerteMenu() { _tipoMuerteExpandido.value = false }
    fun mostrarDatePickerMuerte() { _mostrarDatePickerMuerte.value = true }
    fun ocultarDatePickerMuerte() { _mostrarDatePickerMuerte.value = false }

    fun seleccionarFechaMuerte(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        _fechaMuerte.value = String.format(
            "%02d/%02d/%04d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        _mostrarDatePickerMuerte.value = false
    }

    fun obtenerUbicacionActual() {
        _coordenadaX.value = "123456,12"
        _coordenadaY.value = "1234567,12"
        Log.d("GPS", "Ubicación obtenida - X: ${_coordenadaX.value}, Y: ${_coordenadaY.value}")
    }

    fun esFormularioMuerteValido(): Boolean {
        val tipoValido = !_tipoMuerte.value.isNullOrEmpty()
        val identificadorValido = !_identificadorMuerte.value.isNullOrEmpty()
        val fechaValida = !_fechaMuerte.value.isNullOrEmpty()
        val mesesValidos = if (_tipoMuerte.value?.contains("Avortament") == true) {
            !_mesesGestacion.value.isNullOrEmpty() && _mesesGestacion.value?.toIntOrNull() in 1..9
        } else true
        val coordenadasValidas = if (_cadaverInaccesible.value == true) {
            !_coordenadaX.value.isNullOrEmpty() && !_coordenadaY.value.isNullOrEmpty()
        } else true
        return tipoValido && identificadorValido && fechaValida && mesesValidos && coordenadasValidas
    }

    fun limpiarFormularioMuerte() {
        _tipoMuerte.value = ""; _codigoTipoMuerte.value = ""; _identificadorMuerte.value = ""
        _fechaMuerte.value = ""; _mesesGestacion.value = ""; _cadaverInaccesible.value = false
        _coordenadaX.value = ""; _coordenadaY.value = ""; borradorSesionId = ""
    }

    fun resetearEstadoRegistroMuerte() {
        _registroMuerteExitoso.value = false
        _mensajeErrorMuerte.value = ""
        _codiError.value = null
    }

    fun putMuerteBovino() {
        _codiError.value = null
        if (!esFormularioMuerteValido()) {
            _codiError.value = when {
                _tipoMuerte.value.isNullOrEmpty() -> 7
                _identificadorMuerte.value.isNullOrEmpty() -> 0
                _fechaMuerte.value.isNullOrEmpty() -> 8
                _tipoMuerte.value?.contains("Avortament") == true && _mesesGestacion.value.isNullOrEmpty() -> 9
                _cadaverInaccesible.value == true && _coordenadaX.value.isNullOrEmpty() -> 10
                _cadaverInaccesible.value == true && _coordenadaY.value.isNullOrEmpty() -> 11
                else -> 0
            }
            Log.e("Validación Muerte", "Error: ${_codiError.value}")
            return
        }
        viewModelScope.launch {
            _cargandoMuerte.postValue(true)
            try {
                val tipoCodigo = _codigoTipoMuerte.value?.substring(0, 2) ?: ""
                val fechaAPI = DateUtils.convertirFechaAFormatoAPI(_fechaMuerte.value ?: "")
                val coordX = if (_cadaverInaccesible.value == true) _coordenadaX.value else null
                val coordY = if (_cadaverInaccesible.value == true) _coordenadaY.value else null
                val mesesGest = if (_tipoMuerte.value?.contains("Avortament") == true) _mesesGestacion.value else null

                val request = RegistroMuerteBovi(
                    cadaverInaccesible = if (_cadaverInaccesible.value == true) "SI" else "NO",
                    coordenadaX = coordX,
                    coordenadaY = coordY,
                    dataMort = fechaAPI,
                    identificador = _identificadorMuerte.value,
                    mesosGestacio = mesesGest,
                    nif = nif,
                    passwordMobilitat = password,
                    tipus = tipoCodigo
                )
                Log.d("Registro Muerte", "Request: $request")

                val response = repositorio.putRegistrarMuerte(request)
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroMuerteExitoso.value = true
                                _mensajeErrorMuerte.value = ""
                                Log.d("Registro Muerte", "Muerte reportada exitosamente")
                                guardarEnHistorial("Fallecimiento Registrado")
                                eliminarBorradorAutomatico()
                                limpiarFormularioMuerte()
                            } else {
                                _registroMuerteExitoso.value = false
                                _mensajeErrorMuerte.value = "Respuesta inesperada del servidor: ${body.descripcio ?: "Sin descripción"}"
                                Log.w("Registro Muerte", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeErrorMuerte.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
                                } catch (e: Exception) { _mensajeErrorMuerte.value = "Error al procesar respuesta" }
                            }
                            _registroMuerteExitoso.value = false
                            Log.e("Error Registro Muerte", "HTTP ${response.code()}")
                        }
                        else -> {
                            _registroMuerteExitoso.value = false
                            _mensajeErrorMuerte.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Registro Muerte", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false; _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Registro Muerte", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false; _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Registro Muerte", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargandoMuerte.value = false; _registroMuerteExitoso.value = false
                    _mensajeErrorMuerte.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Registro Muerte", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }


    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                historialDao.insert(
                    HistorialEntity(
                        id = UUID.randomUUID().toString(),
                        tipo = "MUERTE",
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                        datos = "",
                        resumen = resumen
                    )
                )
            } catch (e: Exception) {
                Log.e("Historial", "Error al guardar en historial: ${e.message}", e)
            }
        }
    }
}