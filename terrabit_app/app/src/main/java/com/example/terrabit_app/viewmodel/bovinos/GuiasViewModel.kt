package com.example.terrabit_app.viewmodel.bovinos


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guias.PeticionAltaGuia
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.respuestas.ResAltaGuia
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.utils.CodigoPaisUtils

@HiltViewModel
class GuiasViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao,
    val historialCamposManager: HistorialCamposManager
) : BaseBovinoViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _explotacioOrigen      = MutableLiveData("")
    val explotacioOrigen: LiveData<String> = _explotacioOrigen

    private val _activeFieldIndex = MutableLiveData(-1)
    val activeFieldIndex: LiveData<Int> = _activeFieldIndex

    private val _explotacioDestinacio  = MutableLiveData("")
    val explotacioDestinacio: LiveData<String> = _explotacioDestinacio

    private val _temporal              = MutableLiveData(0)
    val temporal: LiveData<Int> = _temporal

    private val _dataSortida           = MutableLiveData("")
    val dataSortida: LiveData<String> = _dataSortida

    private val _horaSortida           = MutableLiveData("")
    val horaSortida: LiveData<String> = _horaSortida

    private val _dataArribada          = MutableLiveData("")
    val dataArribada: LiveData<String> = _dataArribada

    private val _horaArribada          = MutableLiveData("")
    val horaArribada: LiveData<String> = _horaArribada

    private val _mobilitat             = MutableLiveData(0)
    val mobilitat: LiveData<Int> = _mobilitat

    private val _pais                  = MutableLiveData("")
    val pais: LiveData<String> = _pais

    private val _codiExplotacio        = MutableLiveData("")
    val codiExplotacio: LiveData<String> = _codiExplotacio

    private val _codiAtes              = MutableLiveData("")
    val codiAtes: LiveData<String> = _codiAtes

    private val _nomTransportista      = MutableLiveData("")
    val nomTransportista: LiveData<String> = _nomTransportista

    private val _mitjaTransport        = MutableLiveData(0)
    val mitjaTransport: LiveData<Int> = _mitjaTransport

    private val _matricula             = MutableLiveData("")
    val matricula: LiveData<String> = _matricula

    private val _nifConductor          = MutableLiveData("")
    val nifConductor: LiveData<String> = _nifConductor

    private val _nomConductor          = MutableLiveData("")
    val nomConductor: LiveData<String> = _nomConductor

    private val _identificadors        = MutableLiveData<List<String>>(listOf(""))
    val identificadors: LiveData<List<String>> = _identificadors

    // Códigos internos para la API
    private var codiTemporal       = ""
    private var codiGuiaMobilidad  = ""
    private var codiTransport      = ""

    // ─── Dropdowns ────────────────────────────────────────────────────────────
    private val _temporalExpandido         = MutableLiveData(false)
    val temporalExpandido: LiveData<Boolean> = _temporalExpandido

    private val _mobilitatExpandido        = MutableLiveData(false)
    val mobilitatExpandido: LiveData<Boolean> = _mobilitatExpandido

    private val _mitjaTransportExpandido   = MutableLiveData(false)
    val mitjaTransportExpandido: LiveData<Boolean> = _mitjaTransportExpandido

    // ─── DatePickers/TimePickers ──────────────────────────────────────────────
    private val _mostrarDatePickerSortida  = MutableLiveData(false)
    val mostrarDatePickerSortida: LiveData<Boolean> = _mostrarDatePickerSortida

    private val _mostrarTimePickerSortida  = MutableLiveData(false)
    val mostrarTimePickerSortida: LiveData<Boolean> = _mostrarTimePickerSortida

    private val _mostrarDatePickerArribada = MutableLiveData(false)
    val mostrarDatePickerArribada: LiveData<Boolean> = _mostrarDatePickerArribada

    private val _mostrarTimePickerArribada = MutableLiveData(false)
    val mostrarTimePickerArribada: LiveData<Boolean> = _mostrarTimePickerArribada

    init {
        borradorSesionId = "guia_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "GUIA"

    override fun getDatosFormulario() = mapOf(
        "explotacioOrigen"     to _explotacioOrigen.value,
        "explotacioDestinacio" to _explotacioDestinacio.value,
        "temporal"             to _temporal.value,
        "dataSortida"          to _dataSortida.value,
        "horaSortida"          to _horaSortida.value,
        "dataArribada"         to _dataArribada.value,
        "horaArribada"         to _horaArribada.value,
        "mobilitat"            to _mobilitat.value,
        "pais"                 to _pais.value,
        "codiExplotacio"       to _codiExplotacio.value,
        "codiAtes"             to _codiAtes.value,
        "nomTransportista"     to _nomTransportista.value,
        "mitjaTransport"       to _mitjaTransport.value,
        "matricula"            to _matricula.value,
        "nifConductor"         to _nifConductor.value,
        "nomConductor"         to _nomConductor.value,
        "identificadors"       to _identificadors.value,
        "codiTemporal"         to codiTemporal,
        "codiGuiaMobilidad"    to codiGuiaMobilidad,
        "codiTransport"        to codiTransport
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        _explotacioOrigen.value     = datos["explotacioOrigen"] as? String ?: ""
        _explotacioDestinacio.value = datos["explotacioDestinacio"] as? String ?: ""
        _temporal.value             = (datos["temporal"] as? Double)?.toInt() ?: 0
        _dataSortida.value          = datos["dataSortida"] as? String ?: ""
        _horaSortida.value          = datos["horaSortida"] as? String ?: ""
        _dataArribada.value         = datos["dataArribada"] as? String ?: ""
        _horaArribada.value         = datos["horaArribada"] as? String ?: ""
        _mobilitat.value            = (datos["mobilitat"] as? Double)?.toInt() ?: 0
        _pais.value                 = datos["pais"] as? String ?: ""
        _codiExplotacio.value       = datos["codiExplotacio"] as? String ?: ""
        _codiAtes.value             = datos["codiAtes"] as? String ?: ""
        _nomTransportista.value     = datos["nomTransportista"] as? String ?: ""
        _mitjaTransport.value       = (datos["mitjaTransport"] as? Double)?.toInt() ?: 0
        _matricula.value            = datos["matricula"] as? String ?: ""
        _nifConductor.value         = datos["nifConductor"] as? String ?: ""
        _nomConductor.value         = datos["nomConductor"] as? String ?: ""
        codiTemporal                = datos["codiTemporal"] as? String ?: ""
        codiGuiaMobilidad           = datos["codiGuiaMobilidad"] as? String ?: ""
        codiTransport               = datos["codiTransport"] as? String ?: ""
        @Suppress("UNCHECKED_CAST")
        _identificadors.value       = (datos["identificadors"] as? List<String>) ?: listOf("")
    }

    override fun limpiarFormulario() {
        _explotacioOrigen.value = ""; _explotacioDestinacio.value = ""
        _temporal.value = 0; _dataSortida.value = ""; _horaSortida.value = ""
        _dataArribada.value = ""; _horaArribada.value = ""; _mobilitat.value = 0
        _pais.value = ""; _codiExplotacio.value = ""; _codiAtes.value = ""
        _nomTransportista.value = ""; _mitjaTransport.value = 0
        _matricula.value = ""; _nifConductor.value = ""; _nomConductor.value = ""
        _identificadors.value = listOf("")
        codiTemporal = ""; codiGuiaMobilidad = ""; codiTransport = ""; borradorSesionId = ""
    }

    override fun tieneContenido() =
        !_explotacioOrigen.value.isNullOrEmpty() || !_explotacioDestinacio.value.isNullOrEmpty() ||
                (_temporal.value ?: 0) != 0 || !_dataSortida.value.isNullOrEmpty() ||
                !_horaSortida.value.isNullOrEmpty() || !_dataArribada.value.isNullOrEmpty() ||
                !_horaArribada.value.isNullOrEmpty() || (_mobilitat.value ?: 0) != 0 ||
                !_pais.value.isNullOrEmpty() || !_codiExplotacio.value.isNullOrEmpty() ||
                !_codiAtes.value.isNullOrEmpty() || !_nomTransportista.value.isNullOrEmpty() ||
                (_mitjaTransport.value ?: 0) != 0 || !_matricula.value.isNullOrEmpty() ||
                !_nifConductor.value.isNullOrEmpty() || !_nomConductor.value.isNullOrEmpty() ||
                (_identificadors.value?.any { it.isNotEmpty() } == true)

    // ─── Selección de bovino (con índice) ─────────────────────────────────────
    fun onBovinoSelected(index: Int, animal: Animal) {
        actualizarIdentificador(index, animal.identificador)
        limpiarSugerencias()
    }

    // ─── Precarga desde lista ─────────────────────────────────────────────────
    fun precargarAnimal(animalId: String) {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (lista.isEmpty()) lista.add(animalId) else lista[0] = animalId
        _identificadors.value = lista
    }

    // ─── Actualizadores de campos ─────────────────────────────────────────────
    fun actualizarExplotacioOrigen(valor: String)     { _explotacioOrigen.value = valor }
    fun actualizarExplotacioDestinacio(valor: String) { _explotacioDestinacio.value = valor }
    fun actualizarPais(valor: String)                 { _pais.value = valor }
    fun actualizarCodiExplotacio(valor: String)       { _codiExplotacio.value = valor }
    fun campoCodiAtes(codigo: String)                 { if (codigo.length <= 15) _codiAtes.value = codigo }
    fun actualizarNomTransportista(nombre: String)    { _nomTransportista.value = nombre }
    fun actualizarMatricula(matricula: String)        { _matricula.value = matricula }
    fun actualizarNifConductor(nif: String)           { if (nif.length <= 9) _nifConductor.value = nif }
    fun actualizarNomConductor(nombre: String)        { _nomConductor.value = nombre }

    // ─── Dropdowns ────────────────────────────────────────────────────────────
    fun seleccionarTemporal(valor: Int, codigo: String) {
        _temporal.value = valor; codiTemporal = codigo; _temporalExpandido.value = false
    }
    fun seleccionarMobilitat(valor: Int, codigo: String) {
        _mobilitat.value = valor; codiGuiaMobilidad = codigo; _mobilitatExpandido.value = false
    }
    fun seleccionarMitjaTransport(medio: Int, codigo: String) {
        _mitjaTransport.value = medio; codiTransport = codigo; _mitjaTransportExpandido.value = false
    }
    fun searchBovinosConCampo(fieldIndex: Int, query: String) {
        _activeFieldIndex.value = fieldIndex
        searchBovinos(query)
    }
    fun toggleTemporalExpandido()       { _temporalExpandido.value = !(_temporalExpandido.value ?: false) }
    fun toggleMobilitatExpandido()      { _mobilitatExpandido.value = !(_mobilitatExpandido.value ?: false) }
    fun toggleMitjaTransportExpandido() { _mitjaTransportExpandido.value = !(_mitjaTransportExpandido.value ?: false) }
    fun cerrarTemporalMenu()            { _temporalExpandido.value = false }
    fun cerrarMobilitatMenu()           { _mobilitatExpandido.value = false }
    fun cerrarMitjaTransportMenu()      { _mitjaTransportExpandido.value = false }

    // ─── DatePickers/TimePickers ──────────────────────────────────────────────
    fun mostrarDatePickerSortida()  { _mostrarDatePickerSortida.value = true }
    fun ocultarDatePickerSortida()  { _mostrarDatePickerSortida.value = false }
    fun mostrarTimePickerSortida()  { _mostrarTimePickerSortida.value = true }
    fun ocultarTimePickerSortida()  { _mostrarTimePickerSortida.value = false }
    fun mostrarDatePickerArribada() { _mostrarDatePickerArribada.value = true }
    fun ocultarDatePickerArribada() { _mostrarDatePickerArribada.value = false }
    fun mostrarTimePickerArribada() { _mostrarTimePickerArribada.value = true }
    fun ocultarTimePickerArribada() { _mostrarTimePickerArribada.value = false }

    fun seleccionarFechaSortida(millis: Long) {
        _dataSortida.value = fechaMillisAString(millis)
        _mostrarDatePickerSortida.value = false
    }
    fun seleccionarFechaArribada(millis: Long) {
        _dataArribada.value = fechaMillisAString(millis)
        _mostrarDatePickerArribada.value = false
    }
    fun actualizarHoraSortida(hora: String, minutos: String) {
        _horaSortida.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }
    fun actualizarHoraArribada(hora: String, minutos: String) {
        _horaArribada.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }

    // ─── Identificadores ─────────────────────────────────────────────────────

    fun actualizarIdentificador(index: Int, valor: String) {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (index < lista.size) { lista[index] = valor; _identificadors.value = lista }
    }

    fun actualizarIdentificadorDesdeHardware(index: Int, valor: String) {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (index < lista.size) {
            lista[index] = CodigoPaisUtils.traducirCodigoPais(valor.trim())
            _identificadors.value = lista
        }
    }
    fun agregarIdentificador() {
        _identificadors.value = (_identificadors.value?.toMutableList() ?: mutableListOf()).also { it.add("") }
    }
    fun eliminarIdentificador(index: Int) {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (lista.size > 1 && index < lista.size) { lista.removeAt(index); _identificadors.value = lista }
    }

    // ─── Cantidad borradores ──────────────────────────────────────────────────
    suspend fun obtenerCantidadBorradoresGuia(): Int =
        borradorDao.getAll().count { it.tipo == "GUIA" && it.estado == "BORRADOR_AUTO" }

    // ─── Validación y envío ───────────────────────────────────────────────────
    fun esFormularioValido() =
        !_explotacioOrigen.value.isNullOrEmpty() && !_explotacioDestinacio.value.isNullOrEmpty() &&
                (_temporal.value ?: 0) != 0 && !_dataSortida.value.isNullOrEmpty() &&
                !_horaSortida.value.isNullOrEmpty() && !_dataArribada.value.isNullOrEmpty() &&
                !_horaArribada.value.isNullOrEmpty() && (_mobilitat.value ?: 0) != 0

    fun confirmarAltaGuia() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = when {
                _explotacioOrigen.value.isNullOrEmpty()     -> 20
                _explotacioDestinacio.value.isNullOrEmpty() -> 18
                (_temporal.value ?: 0) == 0                 -> 21
                _dataSortida.value.isNullOrEmpty()          -> 22
                _horaSortida.value.isNullOrEmpty()          -> 23
                _dataArribada.value.isNullOrEmpty()         -> 15
                _horaArribada.value.isNullOrEmpty()         -> 16
                (_mobilitat.value ?: 0) == 0                -> 24
                else                                        -> 0
            }
            return
        }
        launchApiCall {
            val request = PeticionAltaGuia(
                nif                  = nif,
                passwordMobilitat    = password,
                especie              = "01",
                explotacioOrigen     = _explotacioOrigen.value ?: "",
                explotacioDestinacio = _explotacioDestinacio.value ?: "",
                temporal             = codiTemporal,
                dataSortida          = convertirFechaHoraAFormatoAPI(_dataSortida.value ?: "", _horaSortida.value ?: ""),
                dataArribada         = convertirFechaHoraAFormatoAPI(_dataArribada.value ?: "", _horaArribada.value ?: ""),
                mobilitat            = codiGuiaMobilidad,
                pais                 = _pais.value?.ifEmpty { null },
                codiExplotacio       = _codiExplotacio.value?.ifEmpty { null },
                codiAtes             = _codiAtes.value?.ifEmpty { null },
                nomTransportista     = _nomTransportista.value?.ifEmpty { null },
                mitjaTransport       = codiTransport.ifEmpty { null },
                matricula            = _matricula.value?.ifEmpty { null },
                nifConductor         = _nifConductor.value?.ifEmpty { null },
                nomConductor         = _nomConductor.value?.ifEmpty { null },
                identificadors       = _identificadors.value?.map { it.trim() }?.filter { it.isNotEmpty() }?.takeIf { it.isNotEmpty() }
            )
            val response = repositorio.putAltaGuia(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()?.let {
                        it.codiRemo == "0" ||
                                it.descripcio?.contains("correcte", ignoreCase = true) == true ||
                                it.descripcio?.equals("OK", ignoreCase = true) == true
                    } == true -> {
                        _operacionExitosa.value = true
                        _mensajeError.value = ""
                        guardarEnHistorial("Guía enviada")
                        guardarHistorialCampos()
                        eliminarBorradorAutomatico()
                        limpiarFormulario()
                    }
                    !response.isSuccessful -> {
                        _mensajeError.value = try {
                            val errorObj = Gson().fromJson(response.errorBody()?.string(), ResAltaGuia::class.java)
                            errorObj.errors?.firstOrNull()?.descripcio ?: errorObj.descripcio ?: "Error desconocido"
                        } catch (e: Exception) { "Error al procesar respuesta" }
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

    private suspend fun guardarHistorialCampos() {
        historialCamposManager.guardarValor("explotacio_origen",    _explotacioOrigen.value ?: "")
        historialCamposManager.guardarValor("explotacio_destino",   _explotacioDestinacio.value ?: "")
        historialCamposManager.guardarValor("codi_ates",            _codiAtes.value ?: "")
        historialCamposManager.guardarValor("nom_transportista",    _nomTransportista.value ?: "")
        historialCamposManager.guardarValor("matricula_vehicle",    _matricula.value ?: "")
        historialCamposManager.guardarValor("nif_conductor",        _nifConductor.value ?: "")
        historialCamposManager.guardarValor("nom_conductor",        _nomConductor.value ?: "")
    }
}