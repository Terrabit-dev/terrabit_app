package com.example.terrabit_app.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NacimientoViewmodel(application: Application) : AndroidViewModel(application) {

    private val repositorio = Repositorio(application)
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var borradorSesionId: String = ""
    private var editandoBorrador: Boolean = false

    private val userPreferences = UserPreferences(application)

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""
    val codiMo = userPreferences.getCodiMO() ?: ""

    // ============================================
    // ESTADOS PARA AUTOCOMPLETADO
    // ============================================
    private val _suggestionsBovinos = MutableLiveData<List<Animal>>(emptyList())
    val suggestionsBovinos = _suggestionsBovinos

    private val _isLoadingBovinos = MutableLiveData(false)
    val isLoadingBovinos = _isLoadingBovinos

    private val _bovinosCargados = MutableLiveData(false)
    val bovinosCargados = _bovinosCargados

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)
        if (borradorSesionId.isEmpty()) {
            borradorSesionId = "nacimiento_auto_${System.currentTimeMillis()}"
        }

        cargarBovinosEnCache()
    }

    // ============================================
    // FUNCIÓN PARA CARGAR BOVINOS EN CACHÉ
    // ============================================
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
                Log.d("NacimientoVM", "Bovinos cargados en caché")
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("NacimientoVM", "Error al cargar bovinos: ${e.message}", e)
            }
        }
    }

    // ============================================
    // FUNCIÓN PARA BUSCAR BOVINOS (AUTOCOMPLETADO)
    // ============================================
    fun searchBovinos(query: String) {
        if (query.isBlank()) {
            _suggestionsBovinos.value = emptyList()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultados = repositorio.searchBovinosLocal(query)
                _suggestionsBovinos.postValue(resultados)
                Log.d("NacimientoVM", "Búsqueda: '$query' - ${resultados.size} resultados")
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e("NacimientoVM", "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    // ============================================
    // FUNCIÓN AL SELECCIONAR BOVINO
    // ============================================
    fun onBovinoSelected(animal: Animal) {
        _idMadre.value = animal.identificador
        _suggestionsBovinos.value = emptyList()
        Log.d("NacimientoVM", "Bovino seleccionado: ${animal.identificador}")
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

            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == borradorSesionId }

            val borrador = if (borradorExistente != null) {
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosNacimiento)
                )
            } else {
                Borrador(
                    id = borradorSesionId,
                    tipo = "NACIMIENTO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
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

    fun cargarBorradorPorId(id: String) {
        try {
            val borrador = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == id } ?: return

            editandoBorrador = true
            borradorSesionId = borrador.id

            val datos: Map<String, Any?> = Gson().fromJson(
                borrador.datos,
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

            Log.d("NacimientoVM", "Borrador cargado por ID: $id")
        } catch (e: Exception) {
            Log.e("NacimientoVM", "Error al cargar borrador por ID: ${e.message}", e)
        }
    }

    fun eliminarBorradorAutomatico() {
        try {
            if (borradorSesionId.isNotEmpty()) {
                sharedPreferencesManager.eliminarBorrador(borradorSesionId)
                Log.d("Eliminar Borrador", "Borrador eliminado: $borradorSesionId")
                borradorSesionId = ""
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

    fun actualizarIdMadre(nuevoId: String) { _idMadre.value = nuevoId }
    fun actualizarIdCria(nuevoId: String) { _idCria.value = nuevoId }
    fun actualizarFechaNacimiento(nuevaFecha: String) { _fechaNacimiento.value = nuevaFecha }

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

    fun toggleSexoExpandido() { _sexoExpandido.value = !(_sexoExpandido.value ?: false) }
    fun toggleRazaExpandida() { _razaExpandida.value = !(_razaExpandida.value ?: false) }
    fun toggleAptitudExpandida() { _aptitudExpandida.value = !(_aptitudExpandida.value ?: false) }
    fun cerrarSexoMenu() { _sexoExpandido.value = false }
    fun cerrarRazaMenu() { _razaExpandida.value = false }
    fun cerrarAptitudMenu() { _aptitudExpandida.value = false }
    fun mostrarDatePicker() { _mostrarDatePicker.value = true }
    fun ocultarDatePicker() { _mostrarDatePicker.value = false }
    fun mostrarDatePickerIdentificacion() { _mostrarDatePickerIdentificacion.value = true }
    fun ocultarDatePickerIdentificacion() { _mostrarDatePickerIdentificacion.value = false }

    fun seleccionarFecha(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        _fechaNacimiento.value = String.format(
            "%02d/%02d/%04d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        _mostrarDatePicker.value = false
    }

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

    fun esFormularioNacimientoValido(): Boolean {
        return !_idMadre.value.isNullOrEmpty() &&
                !_idCria.value.isNullOrEmpty() &&
                !_fechaNacimiento.value.isNullOrEmpty() &&
                !_sexoSeleccionado.value.isNullOrEmpty() &&
                !_razaSeleccionada.value.isNullOrEmpty() &&
                !_aptitudSeleccionada.value.isNullOrEmpty()
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
            return
        }

        viewModelScope.launch {
            _cargandoNacimiento.postValue(true)
            try {
                val request = RegistroNacimientoBovi(
                    nif = nif,
                    passwordMobilitat = password,
                    identificador = _idCria.value ?: "",
                    identificadorMare = _idMadre.value ?: "",
                    dataNaixement = DateUtils.convertirFechaAFormatoAPI(_fechaNacimiento.value ?: ""),
                    dataIdentificacio = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: ""),
                    sexe = sexoApiSeleccionado,
                    raca = _codigoRaza.value ?: "",
                    aptitud = codigoAptitud
                )

                val response = repositorio.putRegistrarNacimiento(request)

                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true
                                _mensajeError.value = ""
                                eliminarBorradorAutomatico()
                                limpiarFormularioNacimiento()
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                }
                            }
                            _registroExitoso.value = false
                        }
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargandoNacimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
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
        borradorSesionId = ""
        editandoBorrador = false
    }

    fun resetearEstadoRegistro() {
        _registroExitoso.value = false
        _mensajeError.value = ""
        _codiError.value = null
    }

    fun validarIdentificador(id: String): Boolean = id.length >= 5
}