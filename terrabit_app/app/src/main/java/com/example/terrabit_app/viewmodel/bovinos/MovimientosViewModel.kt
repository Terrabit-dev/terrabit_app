package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.IdenMovimiento
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.moviminetos.modelos.Movimientos
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils.convertirFechaAFormatoAPI
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.network.moviminetos.modelos.Moviment
import com.example.terrabit_app.utils.CodigoPaisUtils
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID

@HiltViewModel
class MovimientosViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao,
    val historialCamposManager: HistorialCamposManager
) : BaseBovinoViewModel() {

    val listaCodigosAtes = listOf(CodigoAtes("D", "D - Transportista"))

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _movimientosPendientes = MutableLiveData<Movimientos?>()
    val movimientosPendientes: LiveData<Movimientos?> = _movimientosPendientes

    private val _codiRemo = MutableLiveData("")
    val codiRemo: LiveData<String> = _codiRemo

    private val _dataArribada = MutableLiveData("")
    val dataArribada: LiveData<String> = _dataArribada

    private val _horaArribada = MutableLiveData("")
    val horaArribada: LiveData<String> = _horaArribada

    private val _codiAtes = MutableLiveData("")
    val codiAtes: LiveData<String> = _codiAtes

    private val _nomTransportista = MutableLiveData("")
    val nomTransportista: LiveData<String> = _nomTransportista

    private val _matricula = MutableLiveData("")
    val matricula: LiveData<String> = _matricula

    private val _mitjaTransport = MutableLiveData(0)
    val mitjaTransport: LiveData<Int> = _mitjaTransport

    private val _codiTransport = MutableLiveData("")
    val codiTransport: LiveData<String> = _codiTransport

    private val _nifConductor = MutableLiveData("")
    val nifConductor: LiveData<String> = _nifConductor

    private val _nomConductor = MutableLiveData("")
    val nomConductor: LiveData<String> = _nomConductor

    private val _explotacioDestinacio = MutableLiveData("")
    val explotacioDestinacio: LiveData<String> = _explotacioDestinacio

    // Campos de validación simple (usados en codiError, no en listaAnimales)
    private val _identificadorAnimal = MutableLiveData("")
    private val _estatArribada = MutableLiveData("")
    private val _codiEstats = MutableLiveData("")

    // ─── Lista de animales con sus estados por índice ─────────────────────────
    private val _listaAnimales = MutableLiveData<List<IdenMovimiento>>(
        listOf(IdenMovimiento(identificador = "", estatArribada = "", classCanal = null,
            dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
    )

    private val _activeFieldIndex = MutableLiveData(-1)
    val activeFieldIndex: LiveData<Int> = _activeFieldIndex
    val listaAnimales: LiveData<List<IdenMovimiento>> = _listaAnimales

    private val _estatArribadaExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val estatArribadaExpandidoPorIndice: LiveData<Map<Int, Boolean>> = _estatArribadaExpandidoPorIndice

    private val _classCanalExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val classCanalExpandidoPorIndice: LiveData<Map<Int, Boolean>> = _classCanalExpandidoPorIndice

    private val _tipusPresentacioExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipusPresentacioExpandidoPorIndice: LiveData<Map<Int, Boolean>> = _tipusPresentacioExpandidoPorIndice

    private val _mostrarDatePickerPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val mostrarDatePickerPorIndice: LiveData<Map<Int, Boolean>> = _mostrarDatePickerPorIndice

    private val _textoEstatArribadaPorIndice = MutableLiveData<Map<Int, String>>(emptyMap())
    val textoEstatArribadaPorIndice: LiveData<Map<Int, String>> = _textoEstatArribadaPorIndice

    // ─── Dropdowns generales ──────────────────────────────────────────────────
    private val _codiAtesExpandido = MutableLiveData(false)
    val codiAtesExpandido: LiveData<Boolean> = _codiAtesExpandido

    private val _mitjaTransportExpandido = MutableLiveData(false)
    val mitjaTransportExpandido: LiveData<Boolean> = _mitjaTransportExpandido

    private val _estatArribadaExpandido = MutableLiveData(false)
    val estatArribadaExpandido: LiveData<Boolean> = _estatArribadaExpandido

    // ─── DatePicker / TimePicker ──────────────────────────────────────────────
    private val _mostrarDatePickerArribada = MutableLiveData(false)
    val mostrarDatePickerArribada: LiveData<Boolean> = _mostrarDatePickerArribada

    private val _mostrarTimePickerArribada = MutableLiveData(false)
    val mostrarTimePickerArribada: LiveData<Boolean> = _mostrarTimePickerArribada

    private val _cargandoLista = MutableLiveData(false)
    val cargandoLista: LiveData<Boolean> = _cargandoLista

    private val limiteClassCanal = 5

    init {
        borradorSesionId = "movimiento_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "MOVIMIENTO"

    override fun getDatosFormulario() = mapOf(
        "codiRemo"             to _codiRemo.value,
        "dataArribada"         to _dataArribada.value,
        "horaArribada"         to _horaArribada.value,
        "codiAtes"             to _codiAtes.value,
        "nomTransportista"     to _nomTransportista.value,
        "matricula"            to _matricula.value,
        "mitjaTransport"       to _mitjaTransport.value,
        "nifConductor"         to _nifConductor.value,
        "nomConductor"         to _nomConductor.value,
        "explotacioDestinacio" to _explotacioDestinacio.value,
        "listaAnimales"        to _listaAnimales.value,
        "codiTransport"        to _codiTransport.value
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        _codiRemo.value            = datos["codiRemo"] as? String ?: ""
        _dataArribada.value        = datos["dataArribada"] as? String ?: ""
        _horaArribada.value        = datos["horaArribada"] as? String ?: ""
        _codiAtes.value            = datos["codiAtes"] as? String ?: ""
        _nomTransportista.value    = datos["nomTransportista"] as? String ?: ""
        _matricula.value           = datos["matricula"] as? String ?: ""
        _mitjaTransport.value      = (datos["mitjaTransport"] as? Double)?.toInt() ?: 0
        _nifConductor.value        = datos["nifConductor"] as? String ?: ""
        _nomConductor.value        = datos["nomConductor"] as? String ?: ""
        _explotacioDestinacio.value = datos["explotacioDestinacio"] as? String ?: ""
        _codiTransport.value       = datos["codiTransport"] as? String ?: ""
        val listaJson = datos["listaAnimales"] as? List<*>
        if (listaJson != null) {
            _listaAnimales.value = listaJson.mapNotNull { item ->
                val m = item as? Map<*, *>
                IdenMovimiento(
                    identificador  = m?.get("identificador") as? String ?: "",
                    estatArribada  = m?.get("estatArribada") as? String,
                    classCanal     = m?.get("classCanal") as? String,
                    dataSacrMort   = m?.get("dataSacrMort") as? String,
                    pesCanal       = m?.get("pesCanal") as? String,
                    tipusPresentacio = m?.get("tipusPresentacio") as? String
                )
            }.ifEmpty {
                listOf(IdenMovimiento(identificador = "", estatArribada = null,
                    classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
            }
        }
    }

    override fun limpiarFormulario() {
        _codiRemo.value = "";            _dataArribada.value = "";  _horaArribada.value = ""
        _codiAtes.value = "";            _nomTransportista.value = ""; _matricula.value = ""
        _mitjaTransport.value = 0;       _nifConductor.value = ""; _nomConductor.value = ""
        _explotacioDestinacio.value = ""; _codiTransport.value = ""
        _listaAnimales.value = listOf(IdenMovimiento(identificador = "", estatArribada = null,
            classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
        _estatArribadaExpandidoPorIndice.value  = emptyMap()
        _classCanalExpandidoPorIndice.value     = emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = emptyMap()
        _mostrarDatePickerPorIndice.value       = emptyMap()
        _textoEstatArribadaPorIndice.value      = emptyMap()
        borradorSesionId = ""
    }

    override fun tieneContenido() =
        !_codiRemo.value.isNullOrEmpty() || !_dataArribada.value.isNullOrEmpty() ||
                !_horaArribada.value.isNullOrEmpty() || !_codiAtes.value.isNullOrEmpty() ||
                !_nomTransportista.value.isNullOrEmpty() || !_matricula.value.isNullOrEmpty() ||
                (_mitjaTransport.value ?: 0) != 0 || !_nifConductor.value.isNullOrEmpty() ||
                !_nomConductor.value.isNullOrEmpty() || !_explotacioDestinacio.value.isNullOrEmpty() ||
                (_listaAnimales.value?.any { it.identificador.isNotEmpty() } == true)

    // ─── Carga desde movimiento pendiente ─────────────────────────────────────
    fun cargarDatosMovimiento(movimiento: Moviment, transportNombre: Int?) {
        _codiRemo.value  = movimiento.codiRemo
        _codiAtes.value  = movimiento.codiAtes
        val (fecha, hora) = parsearFechaAPI(movimiento.dataArribada)
        _dataArribada.value        = fecha
        _horaArribada.value        = hora
        _explotacioDestinacio.value = movimiento.moDestinacio
        if (!movimiento.mitjaTransport.isNullOrEmpty()) {
            _codiTransport.value = movimiento.mitjaTransport
            if (transportNombre != null) _mitjaTransport.value = transportNombre
        }
        if (movimiento.nomConductor    != null) _nomConductor.value    = movimiento.nomConductor
        if (movimiento.nomTransportista != null) _nomTransportista.value = movimiento.nomTransportista
        if (movimiento.matricula       != null) _matricula.value       = movimiento.matricula
        if (movimiento.nifConductor    != null) _nifConductor.value    = movimiento.nifConductor

        _listaAnimales.value = movimiento.identificadors
            .map { IdenMovimiento(identificador = it.identificador, estatArribada = null,
                classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null) }
            .ifEmpty {
                listOf(IdenMovimiento(identificador = "", estatArribada = null,
                    classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
            }
    }

    // ─── Bovinos con índice ───────────────────────────────────────────────────
    fun onBovinoSelected(index: Int, animal: Animal) {
        actualizarIdentificadorAnimal(index, animal.identificador)
        limpiarSugerencias()
    }

    fun searchBovinosConCampo(fieldIndex: Int, query: String) {
        _activeFieldIndex.value = fieldIndex
        searchBovinos(query)
    }

    // ─── Actualizadores de campos simples ─────────────────────────────────────
    fun actualizarCodiRemo(valor: String)              { _codiRemo.value = valor }
    fun actualizarDataArribada(fecha: String)          { _dataArribada.value = fecha }
    fun actualizarHoraArribada(hora: String, min: String) {
        _horaArribada.value = String.format("%02d:%02d", hora.toInt(), min.toInt())
    }
    fun actualizarNomTransportista(nombre: String)     { _nomTransportista.value = nombre }
    fun actualizarMatricula(matricula: String)         { _matricula.value = matricula }
    fun actualizarNifConductor(nif: String)            { _nifConductor.value = nif }
    fun actualizarNomConductor(nombre: String)         { _nomConductor.value = nombre }
    fun actualizarExplotacioDestinacio(valor: String)  { _explotacioDestinacio.value = valor }

    // ─── Dropdowns generales ──────────────────────────────────────────────────
    fun seleccionarCodiAtes(codigo: String)                { _codiAtes.value = codigo; _codiAtesExpandido.value = false }
    fun seleccionarMitjaTransport(medio: Int, codigo: String) {
        _mitjaTransport.value = medio; _codiTransport.value = codigo; _mitjaTransportExpandido.value = false
    }
    fun seleccionarEstatArribada(estat: String, codigo: String) {
        _estatArribada.value = estat; _codiEstats.value = codigo; _estatArribadaExpandido.value = false
    }
    fun toggleCodiAtesExpandido()       { _codiAtesExpandido.value = !(_codiAtesExpandido.value ?: false) }
    fun toggleMitjaTransportExpandido() { _mitjaTransportExpandido.value = !(_mitjaTransportExpandido.value ?: false) }
    fun toggleEstatArribadaExpandido()  { _estatArribadaExpandido.value = !(_estatArribadaExpandido.value ?: false) }
    fun cerrarCodiAtesMenu()            { _codiAtesExpandido.value = false }
    fun cerrarMitjaTransportMenu()      { _mitjaTransportExpandido.value = false }
    fun cerrarEstatArribadaMenu()       { _estatArribadaExpandido.value = false }

    // ─── DatePicker / TimePicker llegada ──────────────────────────────────────
    fun mostrarDatePickerArribada()  { _mostrarDatePickerArribada.value = true }
    fun ocultarDatePickerArribada()  { _mostrarDatePickerArribada.value = false }
    fun mostrarTimePickerArribada()  { _mostrarTimePickerArribada.value = true }
    fun ocultarTimePickerArribada()  { _mostrarTimePickerArribada.value = false }

    fun seleccionarFechaArribada(fechaMillis: Long) {
        _dataArribada.value = fechaMillisAString(fechaMillis)   // ← helper de la base
        _mostrarDatePickerArribada.value = false
    }

    // ─── Lista de animales ────────────────────────────────────────────────────
    fun agregarAnimal() {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()) +
                IdenMovimiento(identificador = "", estatArribada = "", classCanal = null,
                    dataSacrMort = null, pesCanal = null, tipusPresentacio = null)
    }

    fun eliminarAnimal(indice: Int) {
        val lista = _listaAnimales.value ?: emptyList()
        if (lista.size > 1) {
            _listaAnimales.value = lista.filterIndexed { index, _ -> index != indice }
            _estatArribadaExpandidoPorIndice.value    = _estatArribadaExpandidoPorIndice.value?.minus(indice)
            _classCanalExpandidoPorIndice.value       = _classCanalExpandidoPorIndice.value?.minus(indice)
            _tipusPresentacioExpandidoPorIndice.value = _tipusPresentacioExpandidoPorIndice.value?.minus(indice)
            _mostrarDatePickerPorIndice.value         = _mostrarDatePickerPorIndice.value?.minus(indice)
            _textoEstatArribadaPorIndice.value        = _textoEstatArribadaPorIndice.value?.minus(indice)
        }
    }

    fun actualizarIdentificadorAnimal(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = identificador) else animal
        }
    }

    // ← Función nueva detectada en la versión final del original
    fun actualizarIdentificadorAnimalDesdeHardware(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice)
                animal.copy(identificador = CodigoPaisUtils.traducirCodigoPais(identificador.trim()))
            else animal
        }
    }

    fun actualizarClassCanal(indice: Int, clase: String) {
        if (clase.length <= limiteClassCanal) {
            _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
                if (index == indice) animal.copy(classCanal = clase) else animal
            }
        }
    }

    fun seleccionarEstatArribadaAnimal(indice: Int, estatTexto: Int, estatCodigo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) {
                if (estatCodigo != "80")
                    animal.copy(estatArribada = estatCodigo, classCanal = null,
                        dataSacrMort = null, pesCanal = null, tipusPresentacio = null)
                else animal.copy(estatArribada = estatCodigo)
            } else animal
        }
        _textoEstatArribadaPorIndice.value =
            (_textoEstatArribadaPorIndice.value ?: emptyMap()) + (indice to estatCodigo)
        _estatArribadaExpandidoPorIndice.value =
            (_estatArribadaExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun actualizarDataSacrMort(indice: Int, fecha: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(dataSacrMort = fecha) else animal
        }
    }

    fun actualizarPesCanal(indice: Int, peso: String) {
        if (peso.length <= 5) {
            _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
                if (index == indice) animal.copy(pesCanal = peso) else animal
            }
        }
    }

    fun seleccionarTipusPresentacio(indice: Int, codigo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(tipusPresentacio = codigo) else animal
        }
        _tipusPresentacioExpandidoPorIndice.value =
            (_tipusPresentacioExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun toggleEstatArribadaExpandido(indice: Int) {
        val mapa = _estatArribadaExpandidoPorIndice.value ?: emptyMap()
        _estatArribadaExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun toggleTipusPresentacioExpandido(indice: Int) {
        val mapa = _tipusPresentacioExpandidoPorIndice.value ?: emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun cerrarEstatArribadaMenu(indice: Int) {
        _estatArribadaExpandidoPorIndice.value =
            (_estatArribadaExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun cerrarTipusPresentacioMenu(indice: Int) {
        _tipusPresentacioExpandidoPorIndice.value =
            (_tipusPresentacioExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun mostrarDatePickerSacrMort(indice: Int) {
        _mostrarDatePickerPorIndice.value =
            (_mostrarDatePickerPorIndice.value ?: emptyMap()) + (indice to true)
    }

    fun ocultarDatePickerSacrMort(indice: Int) {
        _mostrarDatePickerPorIndice.value =
            (_mostrarDatePickerPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun seleccionarFechaSacrMort(indice: Int, fechaMillis: Long) {
        actualizarDataSacrMort(indice, fechaMillisAString(fechaMillis))  // ← helper de la base
        ocultarDatePickerSacrMort(indice)
    }

    // ─── Movimientos pendientes ───────────────────────────────────────────────
    fun obtenerMovimientosPendientes(
        nif: String, password: String,
        explotacioDestinacio: String, dataSortida: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repositorio.getConfirmacionMovimientos(nif, password, explotacioDestinacio, dataSortida)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _movimientosPendientes.value = response.body()
                } else {
                    _mensajeError.value = "Error al obtener movimientos pendientes"
                }
            }
        }
    }

    suspend fun obtenerCantidadBorradoresMovimiento(): Int =
        borradorDao.getAll().count { it.tipo == "MOVIMIENTO" && it.estado == "BORRADOR_AUTO" }

    // ─── Validación y envío ───────────────────────────────────────────────────
    fun esFormularioValido(): Boolean {
        val animalesValidos = (_listaAnimales.value ?: emptyList()).all { animal ->
            val camposAdicionales = if (animal.estatArribada == "80") {
                !animal.dataSacrMort.isNullOrEmpty() && !animal.pesCanal.isNullOrEmpty() &&
                        !animal.classCanal.isNullOrEmpty()   && !animal.tipusPresentacio.isNullOrEmpty()
            } else true
            animal.identificador.isNotEmpty() &&
                    animal.estatArribada?.isNotEmpty() == true &&
                    camposAdicionales
        }
        return !_codiRemo.value.isNullOrEmpty() && !_dataArribada.value.isNullOrEmpty() &&
                !_horaArribada.value.isNullOrEmpty() && !_codiAtes.value.isNullOrEmpty() &&
                !_explotacioDestinacio.value.isNullOrEmpty() && animalesValidos
    }

    fun confirmarMovimiento() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = when {
                _codiRemo.value.isNullOrEmpty()            -> 14
                _dataArribada.value.isNullOrEmpty()        -> 15
                _horaArribada.value.isNullOrEmpty()        -> 16
                _codiAtes.value.isNullOrEmpty()            -> 17
                _explotacioDestinacio.value.isNullOrEmpty() -> 18
                _identificadorAnimal.value.isNullOrEmpty() -> 12
                _estatArribada.value.isNullOrEmpty()       -> 19
                else                                       -> 0
            }
            return
        }
        launchApiCall {
            val listaAPI = (_listaAnimales.value ?: emptyList()).map { animal ->
                animal.copy(
                    dataSacrMort = if (animal.estatArribada == "80" && animal.dataSacrMort != null)
                        convertirFechaAFormatoAPI(animal.dataSacrMort) else null
                )
            }
            val request = PetConfirmacionMovi(
                nif                  = nif,
                passwordMobilitat    = password,
                especie              = "01",
                codiRemo             = _codiRemo.value ?: "",
                dataArribada         = convertirFechaHoraAFormatoAPI(_dataArribada.value ?: "", _horaArribada.value ?: ""),
                codiAtes             = _codiAtes.value ?: "",
                nomTransportista     = _nomTransportista.value ?: "",
                mitjaTransport       = _codiTransport.value ?: "",
                matricula            = _matricula.value ?: "",
                nifConductor         = _nifConductor.value ?: "",
                nomConductor         = _nomConductor.value ?: "",
                explotacioDestinacio = _explotacioDestinacio.value ?: "",
                identificadors       = listaAPI
            )
            val response = repositorio.putConfirmarMovi(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()
                        ?.descripcio?.equals("OK", ignoreCase = true) == true -> {
                        _operacionExitosa.value = true; _mensajeError.value = ""
                        guardarEnHistorial("Movimiento Registrado")
                        guardarHistorialCampos()
                        eliminarBorradorAutomatico()
                        limpiarFormulario()
                    }
                    !response.isSuccessful -> {
                        _mensajeError.value = parsearMensajeError(response)  // ← helper de la base
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

    // ─── Historial de campos ──────────────────────────────────────────────────
    private suspend fun guardarHistorialCampos() {
        historialCamposManager.guardarValor("codi_remo_guia",    _codiRemo.value ?: "")
        historialCamposManager.guardarValor("explotacio_destino", _explotacioDestinacio.value ?: "")
        historialCamposManager.guardarValor("matricula_vehicle",  _matricula.value ?: "")
        historialCamposManager.guardarValor("nom_transportista",  _nomTransportista.value ?: "")
        historialCamposManager.guardarValor("nif_conductor",      _nifConductor.value ?: "")
        historialCamposManager.guardarValor("nom_conductor",      _nomConductor.value ?: "")
    }

    // ─── Utilidades privadas ──────────────────────────────────────────────────
    private fun parsearFechaAPI(fechaAPI: String): Pair<String, String> {
        return try {
            if (fechaAPI.length >= 12) {
                "${ fechaAPI.substring(6,8) }/${ fechaAPI.substring(4,6) }/${ fechaAPI.substring(0,4) }" to
                        "${ fechaAPI.substring(8,10) }:${ fechaAPI.substring(10,12) }"
            } else "" to ""
        } catch (e: Exception) { "" to "" }
    }

    data class CodigoAtes(val codigo: String, val nombre: String)
}