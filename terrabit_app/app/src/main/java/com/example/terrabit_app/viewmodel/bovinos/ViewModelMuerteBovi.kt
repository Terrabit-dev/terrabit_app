package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.utils.DateUtils
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao

@HiltViewModel
class ViewModelMuerteBovi @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao
) : BaseBovinoViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _identificadorAnimal = MutableLiveData("")
    val identificadorMuerte: LiveData<String> = _identificadorAnimal

    private val _tipoMuerte = MutableLiveData(0)
    val tipoMuerte: LiveData<Int> = _tipoMuerte

    private val _codigoTipoMuerte = MutableLiveData("")
    val codigoTipoMuerte: LiveData<String> = _codigoTipoMuerte

    private val _fechaMuerte = MutableLiveData("")
    val fechaMuerte: LiveData<String> = _fechaMuerte

    private val _mesesGestacion = MutableLiveData("")
    val mesesGestacion: LiveData<String> = _mesesGestacion

    private val _cadaverInaccesible = MutableLiveData(false)
    val cadaverInaccesible: LiveData<Boolean> = _cadaverInaccesible

    private val _coordenadaX = MutableLiveData("")
    val coordenadaX: LiveData<String> = _coordenadaX

    private val _coordenadaY = MutableLiveData("")
    val coordenadaY: LiveData<String> = _coordenadaY

    private val _tipoMuerteExpandido = MutableLiveData(false)
    val tipoMuerteExpandido: LiveData<Boolean> = _tipoMuerteExpandido

    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePickerMuerte: LiveData<Boolean> = _mostrarDatePicker

    init {
        cargarCodisMos()
        borradorSesionId = "muerte_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "MUERTE"

    override fun getDatosFormulario() = mapOf(
        "tipo"               to _tipoMuerte.value,
        "codigoTipo"         to _codigoTipoMuerte.value,
        "identificador"      to _identificadorAnimal.value,
        "fecha"              to _fechaMuerte.value,
        "mesesGestacion"     to _mesesGestacion.value,
        "cadaverInaccesible" to _cadaverInaccesible.value,
        "coordenadaX"        to _coordenadaX.value,
        "coordenadaY"        to _coordenadaY.value
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        _tipoMuerte.value          = (datos["tipo"] as? Double)?.toInt() ?: 0
        _codigoTipoMuerte.value    = datos["codigoTipo"] as? String ?: ""
        _identificadorAnimal.value = datos["identificador"] as? String ?: ""
        _fechaMuerte.value         = datos["fecha"] as? String ?: ""
        _mesesGestacion.value      = datos["mesesGestacion"] as? String ?: ""
        _cadaverInaccesible.value  = datos["cadaverInaccesible"] as? Boolean ?: false
        _coordenadaX.value         = datos["coordenadaX"] as? String ?: ""
        _coordenadaY.value         = datos["coordenadaY"] as? String ?: ""
    }

    override fun limpiarFormulario() {
        _tipoMuerte.value          = 0
        _codigoTipoMuerte.value    = ""
        _identificadorAnimal.value = ""
        _fechaMuerte.value         = ""
        _mesesGestacion.value      = ""
        _cadaverInaccesible.value  = false
        _coordenadaX.value         = ""
        _coordenadaY.value         = ""
        borradorSesionId           = ""
    }

    override fun tieneContenido() =
        (_tipoMuerte.value ?: 0) != 0 ||
                !_identificadorAnimal.value.isNullOrEmpty() ||
                !_fechaMuerte.value.isNullOrEmpty() ||
                !_mesesGestacion.value.isNullOrEmpty() ||
                _cadaverInaccesible.value == true ||
                !_coordenadaX.value.isNullOrEmpty() ||
                !_coordenadaY.value.isNullOrEmpty()

    // ─── Selección de bovino ──────────────────────────────────────────────────
    fun onBovinoSelected(animal: Animal) {
        _identificadorAnimal.value = animal.identificador
        limpiarSugerencias()
    }

    // ─── Actualizadores de campos ─────────────────────────────────────────────
    fun actualizarIdentificador(nuevoId: String) { _identificadorAnimal.value = nuevoId }

    fun actualizarMesesGestacion(valor: String) {
        if (valor.isEmpty() || valor.toIntOrNull() in 1..9) _mesesGestacion.value = valor
    }

    fun actualizarUbicacion(x: String, y: String) {
        _coordenadaX.value = x
        _coordenadaY.value = y
    }

    fun actualizarCoordenadaX(valor: String) { _coordenadaX.value = valor }
    fun actualizarCoordenadaY(valor: String) { _coordenadaY.value = valor }

    // ─── Dropdown tipo de muerte ──────────────────────────────────────────────
    fun seleccionarTipoMuerte(tipo: Int, codigo: String) {
        _tipoMuerte.value = tipo
        _codigoTipoMuerte.value = codigo
        _tipoMuerteExpandido.value = false
        // Si no es aborto, limpiamos meses de gestación
        if (codigo != "02") _mesesGestacion.value = ""
    }

    fun toggleTipoMuerteExpandido() {
        _tipoMuerteExpandido.value = !(_tipoMuerteExpandido.value ?: false)
    }
    fun cerrarTipoMuerteMenu() { _tipoMuerteExpandido.value = false }

    // ─── Toggle cadáver inaccesible ───────────────────────────────────────────
    fun toggleCadaverInaccesible() {
        val nuevoValor = !(_cadaverInaccesible.value ?: false)
        _cadaverInaccesible.value = nuevoValor
        if (!nuevoValor) { _coordenadaX.value = ""; _coordenadaY.value = "" }
    }

    // ─── DatePicker ───────────────────────────────────────────────────────────
    fun mostrarDatePickerMuerte() { _mostrarDatePicker.value = true }
    fun ocultarDatePickerMuerte() { _mostrarDatePicker.value = false }

    fun seleccionarFechaMuerte(fechaMillis: Long) {
        _fechaMuerte.value = fechaMillisAString(fechaMillis)
        _mostrarDatePicker.value = false
    }

    // ─── Validación ───────────────────────────────────────────────────────────
    fun esFormularioValido(): Boolean {
        val mesesValidos = if (_codigoTipoMuerte.value == "02")
            !_mesesGestacion.value.isNullOrEmpty() && _mesesGestacion.value?.toIntOrNull() in 1..9
        else true
        val coordenadasValidas = if (_cadaverInaccesible.value == true)
            !_coordenadaX.value.isNullOrEmpty() && !_coordenadaY.value.isNullOrEmpty()
        else true
        return (_tipoMuerte.value ?: 0) != 0 &&
                !_identificadorAnimal.value.isNullOrEmpty() &&
                !_fechaMuerte.value.isNullOrEmpty() &&
                mesesValidos && coordenadasValidas
    }

    // ─── Registro principal ───────────────────────────────────────────────────
    fun putMuerteBovino() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = when {
                (_tipoMuerte.value ?: 0) == 0                                           -> 7
                _identificadorAnimal.value.isNullOrEmpty()                              -> 0
                _fechaMuerte.value.isNullOrEmpty()                                      -> 8
                _codigoTipoMuerte.value == "02" && _mesesGestacion.value.isNullOrEmpty() -> 9
                _cadaverInaccesible.value == true && _coordenadaX.value.isNullOrEmpty() -> 10
                _cadaverInaccesible.value == true && _coordenadaY.value.isNullOrEmpty() -> 11
                else                                                                    -> 0
            }
            return
        }
        launchApiCall {
            val request = RegistroMuerteBovi(
                cadaverInaccesible = if (_cadaverInaccesible.value == true) "SI" else "NO",
                coordenadaX        = if (_cadaverInaccesible.value == true) _coordenadaX.value else null,
                coordenadaY        = if (_cadaverInaccesible.value == true) _coordenadaY.value else null,
                dataMort           = DateUtils.convertirFechaAFormatoAPI(_fechaMuerte.value ?: ""),
                identificador      = _identificadorAnimal.value,
                mesosGestacio      = if (_codigoTipoMuerte.value == "02") _mesesGestacion.value else null,
                nif                = nif,
                passwordMobilitat  = password,
                tipus              = _codigoTipoMuerte.value?.take(2) ?: ""
            )
            val response = repositorio.putRegistrarMuerte(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()
                        ?.let { it.codi == "0" || it.descripcio == "OK" } == true -> {
                        _operacionExitosa.value = true
                        _mensajeError.value = ""
                        guardarEnHistorial("Fallecimiento Registrado")
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
    fun precargarAnimal(id: String) {
        _identificadorAnimal.value = id
    }
}