package com.example.terrabit_app.viewmodel.bovinos

import com.example.terrabit_app.utils.SecureLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.utils.DateUtils
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.utils.CodigoPaisUtils

@HiltViewModel
class NacimientoViewmodel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao
) : BaseBovinoViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _identificadores = MutableLiveData<Identificadores>()
    val identificadores: LiveData<Identificadores> = _identificadores

    private val _activeFieldIndex = MutableLiveData(-1)
    val activeFieldIndex: LiveData<Int> = _activeFieldIndex

    private val _idMadre = MutableLiveData("")
    val idMadre: LiveData<String> = _idMadre

    private val _idCria = MutableLiveData("")
    val idCria: LiveData<String> = _idCria

    private val _fechaNacimiento = MutableLiveData("")
    val fechaNacimiento: LiveData<String> = _fechaNacimiento

    private val _fechaIdentificacion = MutableLiveData("")
    val fechaIdentificacion: LiveData<String> = _fechaIdentificacion

    private val _sexoSeleccionado = MutableLiveData(0)
    val sexoSeleccionado: LiveData<Int> = _sexoSeleccionado

    private val _razaSeleccionada = MutableLiveData(0)
    val razaSeleccionada: LiveData<Int> = _razaSeleccionada

    private val _aptitudSeleccionada = MutableLiveData(0)
    val aptitudSeleccionada: LiveData<Int> = _aptitudSeleccionada

    private val _codigoRaza = MutableLiveData("")

    // Estados de expansión de dropdowns
    private val _sexoExpandido = MutableLiveData(false)
    val sexoExpandido: LiveData<Boolean> = _sexoExpandido

    private val _razaExpandida = MutableLiveData(false)
    val razaExpandida: LiveData<Boolean> = _razaExpandida

    private val _aptitudExpandida = MutableLiveData(false)
    val aptitudExpandida: LiveData<Boolean> = _aptitudExpandida

    // DatePickers
    private val _mostrarDatePickerNacimiento = MutableLiveData(false)
    val mostrarDatePickerNacimiento: LiveData<Boolean> = _mostrarDatePickerNacimiento

    private val _mostrarDatePickerIdentificacion = MutableLiveData(false)
    val mostrarDatePickerIdentificacion: LiveData<Boolean> = _mostrarDatePickerIdentificacion

    // Códigos internos para la API
    private var sexoApiSeleccionado = "0"
    private var codigoAptitud = "0"

    val listaAptitudes = listOf("Carne", "Leche", "Doble propósito")

    init {
        cargarCodisMos()
        borradorSesionId = "nacimiento_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "NACIMIENTO"

    override fun getDatosFormulario() = mapOf(
        "idMadre"            to _idMadre.value,
        "idCria"             to _idCria.value,
        "fechaNacimiento"    to _fechaNacimiento.value,
        "fechaIdentificacion" to _fechaIdentificacion.value,
        "sexoSeleccionado"   to _sexoSeleccionado.value,
        "razaSeleccionada"   to _razaSeleccionada.value,
        "aptitudSeleccionada" to _aptitudSeleccionada.value,
        "codigoRaza"         to _codigoRaza.value,
        "sexoApiSeleccionado" to sexoApiSeleccionado,
        "codigoAptitud"      to codigoAptitud
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        _idMadre.value             = datos["idMadre"] as? String ?: ""
        _idCria.value              = datos["idCria"] as? String ?: ""
        _fechaNacimiento.value     = datos["fechaNacimiento"] as? String ?: ""
        _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""
        _sexoSeleccionado.value    = (datos["sexoSeleccionado"] as? Double)?.toInt() ?: 0
        _razaSeleccionada.value    = (datos["razaSeleccionada"] as? Double)?.toInt() ?: 0
        _aptitudSeleccionada.value = (datos["aptitudSeleccionada"] as? Double)?.toInt() ?: 0
        _codigoRaza.value          = datos["codigoRaza"] as? String ?: ""
        sexoApiSeleccionado        = datos["sexoApiSeleccionado"] as? String ?: "0"
        codigoAptitud              = datos["codigoAptitud"] as? String ?: "0"
    }

    override fun limpiarFormulario() {
        _idMadre.value             = ""
        _idCria.value              = ""
        _fechaNacimiento.value     = ""
        _fechaIdentificacion.value = ""
        _sexoSeleccionado.value    = 0
        _razaSeleccionada.value    = 0
        _aptitudSeleccionada.value = 0
        _codigoRaza.value          = ""
        sexoApiSeleccionado        = "0"
        codigoAptitud              = "0"
        borradorSesionId           = ""
        _activeFieldIndex.value    = -1
    }

    override fun tieneContenido() =
        !_idMadre.value.isNullOrEmpty() ||
                !_idCria.value.isNullOrEmpty() ||
                !_fechaNacimiento.value.isNullOrEmpty() ||
                !_fechaIdentificacion.value.isNullOrEmpty() ||
                (_sexoSeleccionado.value ?: 0) != 0 ||
                (_razaSeleccionada.value ?: 0) != 0 ||
                (_aptitudSeleccionada.value ?: 0) != 0

    // ─── Selección de bovinos ─────────────────────────────────────────────────
    fun onMotherSelected(animal: Animal) {
        _idMadre.value = animal.identificador
        limpiarSugerencias()
        _activeFieldIndex.value = -1
    }

    fun onBreedingSelected(animal: Animal) {
        _idCria.value = animal.identificador
        limpiarSugerencias()
        _activeFieldIndex.value = -1
    }

    // Nacimiento necesita saber qué campo está activo (madre vs cría)
    fun searchBovinosConCampo(fieldIndex: Int, query: String) {
        _activeFieldIndex.value = fieldIndex
        searchBovinos(query)
    }

    // ─── Actualizadores de campos ─────────────────────────────────────────────
    fun actualizarIdMadre(nuevoId: String) { _idMadre.value = nuevoId }
    fun actualizarIdCria(nuevoId: String) { _idCria.value = nuevoId }
    fun actualizarFechaNacimiento(nuevaFecha: String) { _fechaNacimiento.value = nuevaFecha }

    fun actualizarIdMadreDesdeHardware(nuevoId: String) {
        _idMadre.value = CodigoPaisUtils.traducirCodigoPais(nuevoId.trim())
    }
    fun actualizarIdCriaDesdeHardware(nuevoId: String) {
        _idCria.value = CodigoPaisUtils.traducirCodigoPais(nuevoId.trim())
    }

    // ─── Dropdowns ────────────────────────────────────────────────────────────
    fun seleccionarSexo(sexo: Int, codigo: String) {
        _sexoSeleccionado.value = sexo
        sexoApiSeleccionado = codigo
        _sexoExpandido.value = false
    }

    fun seleccionarRaza(raza: Int, codigo: String) {
        _razaSeleccionada.value = raza
        _codigoRaza.value = codigo
        _razaExpandida.value = false
    }

    fun seleccionarAptitud(aptitud: Int, codigo: String) {
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

    // ─── DatePickers ──────────────────────────────────────────────────────────
    fun mostrarDatePickerNacimiento() { _mostrarDatePickerNacimiento.value = true }
    fun ocultarDatePickerNacimiento() { _mostrarDatePickerNacimiento.value = false }
    fun mostrarDatePickerIdentificacion() { _mostrarDatePickerIdentificacion.value = true }
    fun ocultarDatePickerIdentificacion() { _mostrarDatePickerIdentificacion.value = false }

    fun seleccionarFechaNacimiento(fechaMillis: Long) {
        _fechaNacimiento.value = fechaMillisAString(fechaMillis)
        _mostrarDatePickerNacimiento.value = false
    }

    fun seleccionarFechaIdentificacion(fechaMillis: Long) {
        _fechaIdentificacion.value = fechaMillisAString(fechaMillis)
        _mostrarDatePickerIdentificacion.value = false
    }

    // ─── Identificadores disponibles ─────────────────────────────────────────
    fun getIdentificadores(nif: String, password: String, codiMO: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repositorio.getIdentificadoresDisponibles(nif, password, codiMO)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) _identificadores.value = response.body()
                else SecureLog.e("NacimientoVM", "Error identificadores: ${response.message()}")
            }
        }
    }

    // ─── Validación ───────────────────────────────────────────────────────────
    fun esFormularioValido() =
        !_idMadre.value.isNullOrEmpty() &&
                !_idCria.value.isNullOrEmpty() &&
                !_fechaNacimiento.value.isNullOrEmpty() &&
                (_sexoSeleccionado.value ?: 0) != 0 &&
                (_razaSeleccionada.value ?: 0) != 0 &&
                (_aptitudSeleccionada.value ?: 0) != 0

    fun validarIdentificador(id: String) = id.length >= 5

    // ─── Registro principal ───────────────────────────────────────────────────
    fun registrarNacimiento() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = when {
                _idMadre.value.isNullOrEmpty()           -> 1
                _idCria.value.isNullOrEmpty()            -> 2
                _fechaNacimiento.value.isNullOrEmpty()   -> 3
                (_sexoSeleccionado.value ?: 0) == 0      -> 4
                (_razaSeleccionada.value ?: 0) == 0      -> 5
                (_aptitudSeleccionada.value ?: 0) == 0   -> 6
                else                                     -> 0
            }
            return
        }
        launchApiCall {
            val request = RegistroNacimientoBovi(
                nif                 = nif,
                passwordMobilitat   = password,
                identificador       = _idCria.value ?: "",
                identificadorMare   = _idMadre.value ?: "",
                dataNaixement       = DateUtils.convertirFechaAFormatoAPI(_fechaNacimiento.value ?: ""),
                dataIdentificacio   = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: ""),
                sexe                = sexoApiSeleccionado,
                raca                = _codigoRaza.value ?: "",
                aptitud             = codigoAptitud
            )
            val response = repositorio.putRegistrarNacimiento(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()
                        ?.let { it.codi == "0" || it.descripcio == "OK" } == true -> {
                        _operacionExitosa.value = true
                        _mensajeError.value = ""
                        guardarEnHistorial("Nacimiento Registrado")
                        eliminarBorradorAutomatico()
                        limpiarFormulario()
                    }
                    !response.isSuccessful -> {
                        _mensajeError.value = parsearMensajeError(response)
                        _operacionExitosa.value = false
                    }
                    else -> {
                        _operacionExitosa.value = false
                        _mensajeError.value = "Error: Respuesta vacía del servidor"
                    }
                }
            }
        }
    }
}