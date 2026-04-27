package com.example.terrabit_app.viewmodel.bovinos



import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guias.Guia
import com.example.terrabit_app.data.network.guias.PeticionModificarGuia
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditarGuiaBoviViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // ─── Credenciales ────────────────────────────────────────────────────────
    private val nif      = userPreferences.getNif()      ?: ""
    private val password = userPreferences.getPassword() ?: ""

    // ─── Identificador interno de la guía ────────────────────────────────────
    private var codiRemoActual = ""

    // ─── Campos del formulario ───────────────────────────────────────────────
    private val _explotacioOrigen      = MutableLiveData("")
    val explotacioOrigen: LiveData<String> = _explotacioOrigen

    private val _explotacioDestinacio  = MutableLiveData("")
    val explotacioDestinacio: LiveData<String> = _explotacioDestinacio

    private val _dataSortida           = MutableLiveData("")
    val dataSortida: LiveData<String> = _dataSortida

    private val _horaSortida           = MutableLiveData("")
    val horaSortida: LiveData<String> = _horaSortida

    private val _dataArribada          = MutableLiveData("")
    val dataArribada: LiveData<String> = _dataArribada

    private val _horaArribada          = MutableLiveData("")
    val horaArribada: LiveData<String> = _horaArribada

    private val _codiAtes              = MutableLiveData("")
    val codiAtes: LiveData<String> = _codiAtes

    private val _nomTransportista      = MutableLiveData("")
    val nomTransportista: LiveData<String> = _nomTransportista

    private val _mitjaTransport        = MutableLiveData(0)
    val mitjaTransport = _mitjaTransport

    // Código interno del medio de transporte (para la petición)
    private var codiTransport = ""

    private val _matricula             = MutableLiveData("")
    val matricula: LiveData<String> = _matricula

    private val _nifConductor          = MutableLiveData("")
    val nifConductor: LiveData<String> = _nifConductor

    private val _nomConductor          = MutableLiveData("")
    val nomConductor: LiveData<String> = _nomConductor

    private val _identificadors        = MutableLiveData<List<String>>(listOf(""))
    val identificadors: LiveData<List<String>> = _identificadors

    // ─── Estado de la UI ─────────────────────────────────────────────────────
    private val _cargando              = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _exitoso               = MutableLiveData(false)
    val exitoso: LiveData<Boolean> = _exitoso

    private val _error                 = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // ─── DatePickers / TimePickers ───────────────────────────────────────────
    private val _mostrarDatePickerSortida  = MutableLiveData(false)
    val mostrarDatePickerSortida: LiveData<Boolean> = _mostrarDatePickerSortida

    private val _mostrarTimePickerSortida  = MutableLiveData(false)
    val mostrarTimePickerSortida: LiveData<Boolean> = _mostrarTimePickerSortida

    private val _mostrarDatePickerArribada = MutableLiveData(false)
    val mostrarDatePickerArribada: LiveData<Boolean> = _mostrarDatePickerArribada

    private val _mostrarTimePickerArribada = MutableLiveData(false)
    val mostrarTimePickerArribada: LiveData<Boolean> = _mostrarTimePickerArribada

    // ─── Dropdown del medio de transporte ────────────────────────────────────
    private val _mitjaTransportExpandido = MutableLiveData(false)
    val mitjaTransportExpandido: LiveData<Boolean> = _mitjaTransportExpandido

    // ─────────────────────────────────────────────────────────────────────────
    //  Carga de datos desde la guía seleccionada
    // ─────────────────────────────────────────────────────────────────────────

    fun cargarDatosGuia(guia: Guia) {
        codiRemoActual = guia.remo

        _explotacioOrigen.value     = guia.explotacioOrigen     ?: ""
        _explotacioDestinacio.value = guia.explotacioDestinacio ?: ""

        // Fechas en formato "dd/MM/yyyy" y horas "HH:mm"

        val partesSortida  = parsearFechaHora(guia.dataSortida  ?: "")
        val partesArribada = parsearFechaHora(guia.dataArribada ?: "")

        _dataSortida.value  = partesSortida.first
        _horaSortida.value  = partesSortida.second
        _dataArribada.value = partesArribada.first
        _horaArribada.value = partesArribada.second

        _nomTransportista.value = guia.codiTransportista ?: ""   // ← antes sin ?: ""
        _matricula.value        = guia.matricula         ?: ""   // ← antes sin ?: ""
        _nifConductor.value     = guia.nifConductor      ?: ""   // ← antes sin ?: ""

        // La lista de identificadores viene directamente del modelo
        _identificadors.value = if (guia.identificadors.isNotEmpty())
            guia.identificadors.toMutableList()
        else mutableListOf("")

        Log.d("EDITAR_BOVI_VM", "Guía cargada: ${guia.remo}, ${guia.identificadors.size} ids")
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Actualización de campos
    // ─────────────────────────────────────────────────────────────────────────

    fun actualizarCodiAtes(valor: String)         { if (valor.length <= 15) _codiAtes.value = valor }
    fun actualizarNomTransportista(valor: String)  { _nomTransportista.value = valor }
    fun actualizarMatricula(valor: String)         { _matricula.value = valor }
    fun actualizarNifConductor(valor: String)      { if (valor.length <= 9) _nifConductor.value = valor }
    fun actualizarNomConductor(valor: String)      { _nomConductor.value = valor }

    fun seleccionarMitjaTransport(nombre: Int, codigo: String) {
        _mitjaTransport.value = nombre
        codiTransport = codigo
        _mitjaTransportExpandido.value = false
    }
    fun toggleMitjaTransportExpandido() { _mitjaTransportExpandido.value = !(_mitjaTransportExpandido.value ?: false) }
    fun cerrarMitjaTransportMenu()       { _mitjaTransportExpandido.value = false }

    // ─── Identificadores ─────────────────────────────────────────────────────

    fun actualizarIdentificador(index: Int, valor: String) {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (index < lista.size) {
            lista[index] = valor
            _identificadors.value = lista
        }
    }

    fun agregarIdentificador() {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        lista.add("")
        _identificadors.value = lista
    }

    fun eliminarIdentificador(index: Int) {
        val lista = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (lista.size > 1 && index < lista.size) {
            lista.removeAt(index)
            _identificadors.value = lista
        }
    }

    // ─── DatePicker / TimePicker: Salida ─────────────────────────────────────

    fun mostrarDatePickerSortida()  { _mostrarDatePickerSortida.value = true }
    fun ocultarDatePickerSortida()  { _mostrarDatePickerSortida.value = false }
    fun mostrarTimePickerSortida()  { _mostrarTimePickerSortida.value = true }
    fun ocultarTimePickerSortida()  { _mostrarTimePickerSortida.value = false }

    fun seleccionarFechaSortida(millis: Long) {
        _dataSortida.value = millisToDate(millis)
        _mostrarDatePickerSortida.value = false
        _mostrarTimePickerSortida.value = true   // Auto-abre el time picker
    }

    fun actualizarHoraSortida(hora: String, minutos: String) {
        _horaSortida.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }

    // ─── DatePicker / TimePicker: Llegada ─────────────────────────────────────

    fun mostrarDatePickerArribada()  { _mostrarDatePickerArribada.value = true }
    fun ocultarDatePickerArribada()  { _mostrarDatePickerArribada.value = false }
    fun mostrarTimePickerArribada()  { _mostrarTimePickerArribada.value = true }
    fun ocultarTimePickerArribada()  { _mostrarTimePickerArribada.value = false }

    fun seleccionarFechaArribada(millis: Long) {
        _dataArribada.value = millisToDate(millis)
        _mostrarDatePickerArribada.value = false
        _mostrarTimePickerArribada.value = true
    }

    fun actualizarHoraArribada(hora: String, minutos: String) {
        _horaArribada.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Envío al servidor
    // ─────────────────────────────────────────────────────────────────────────

    fun confirmarModificacion(onSuccess: () -> Unit) {
        val fechaSortida  = _dataSortida.value.orEmpty()
        val horaSortida   = _horaSortida.value.orEmpty()
        val fechaArribada = _dataArribada.value.orEmpty()
        val horaArribada  = _horaArribada.value.orEmpty()

        if (fechaSortida.isBlank() || horaSortida.isBlank() ||
            fechaArribada.isBlank() || horaArribada.isBlank()
        ) {
            _error.value = "Las fechas y horas de salida y llegada son obligatorias."
            return
        }

        val ids = _identificadors.value
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        if (ids.isEmpty()) {
            _error.value = "Debes añadir al menos un identificador."
            return
        }

        val request = PeticionModificarGuia(
            nif               = nif,
            passwordMobilitat = password,
            codiRemo          = codiRemoActual,
            especie           = "01",
            dataArribada      = combinarFechaHora(fechaArribada, horaArribada),
            dataSortida       = combinarFechaHora(fechaSortida, horaSortida),
            codiAtes          = _codiAtes.value.orEmpty(),
            nomTransportista  = _nomTransportista.value.orEmpty(),
            mitjaTransport    = codiTransport,
            matricula         = _matricula.value.orEmpty(),
            nifConductor      = _nifConductor.value.orEmpty(),
            nomConductor      = _nomConductor.value.orEmpty(),
            identificadors    = ids
        )

        Log.d("EDITAR_BOVI_VM", "Enviando request: $request")

        viewModelScope.launch {
            _cargando.postValue(true)
            _error.postValue(null)

            try {
                Log.d("EDITAR_BOVI_VM", "Request: $request")
                val response = repositorio.putModificarGuia(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("EDITAR_BOVI_VM", "Respuesta: ${body?.codiRemo} - ${body?.descripcio}")

                    if (body?.codiRemo != null && body.codiRemo != "0" || body?.descripcio?.contains("correcte", ignoreCase = true) == true) {
                        _cargando.postValue(false)
                        _exitoso.postValue(true)
                        onSuccess()
                    } else {
                        _cargando.postValue(false)
                        _error.postValue(body?.descripcio ?: "Error desconocido")
                    }
                } else {
                    val rawError = response.errorBody()?.string() ?: ""
                    Log.e("EDITAR_BOVI_VM", "HTTP ${response.code()}: $rawError")
                    _cargando.postValue(false)
                    _error.postValue(extraerDescripcion(rawError, response.code()))
                }

            } catch (e: Exception) {
                Log.e("EDITAR_BOVI_VM", "Excepción: ${e.message}")
                _cargando.postValue(false)
                _error.postValue("Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    fun resetearError() { _error.value = null }

    // ─────────────────────────────────────────────────────────────────────────
    //  Utilidades privadas
    // ─────────────────────────────────────────────────────────────────────────

    /** "yyyyMMddHHmm" → Pair("dd/MM/yyyy", "HH:mm") */
    private fun parsearFechaHora(rawDate: String): Pair<String, String> {
        return try {
            // "19/03/2026 14:00" → Pair("19/03/2026", "14:00")
            val partes = rawDate.trim().split(" ")
            if (partes.size >= 2) {
                Pair(partes[0], partes[1])
            } else Pair("", "")
        } catch (e: Exception) { Pair("", "") }
    }

    /** millis → "dd/MM/yyyy" */
    @SuppressLint("DefaultLocale")
    private fun millisToDate(millis: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return String.format(
            "%02d/%02d/%04d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }

    /** "dd/MM/yyyy" + "HH:mm" → "yyyyMMddHHmm" */
    private fun combinarFechaHora(fecha: String, hora: String): String {
        return try {
            val (dia, mes, anio) = fecha.split("/")
            val (h, m)           = hora.split(":")
            "$anio$mes$dia$h$m"
        } catch (e: Exception) { "" }
    }
    private fun extraerDescripcion(rawJson: String, httpCode: Int): String {
        if (rawJson.isBlank()) return "Error $httpCode"
        return try {
            val element = com.google.gson.JsonParser.parseString(rawJson)
            when {
                element.isJsonArray -> {
                    element.asJsonArray
                        .mapNotNull { it.asJsonObject.get("descripcio")?.asString }
                        .filter { it.isNotBlank() }
                        .joinToString("\n")
                        .ifBlank { "Error $httpCode" }
                }
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    // Soporta { "errors": [...] } y { "descripcio": "..." }
                    obj.getAsJsonArray("errors")
                        ?.mapNotNull { it.asJsonObject.get("descripcio")?.asString }
                        ?.filter { it.isNotBlank() }
                        ?.joinToString("\n")
                        ?.ifBlank { obj.get("descripcio")?.asString ?: "Error $httpCode" }
                        ?: obj.get("descripcio")?.asString
                        ?: "Error $httpCode"
                }
                else -> "Error $httpCode"
            }
        } catch (e: Exception) {
            "Error $httpCode"
        }
    }
}