package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guias.Guia
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ListarGuiasBoviViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    // ─── Estado observable ───────────────────────────────────────────────────
    private val _listaGuias = MutableLiveData<List<Guia>>(emptyList())
    val listaGuias: LiveData<List<Guia>> = _listaGuias

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _consultaIniciada = MutableLiveData(false)
    val consultaIniciada: LiveData<Boolean> = _consultaIniciada

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // ─── Campo REGA ───────────────────────────────────────────────────────────
    private val _codiRega = MutableLiveData("")
    val codiRega: LiveData<String> = _codiRega

    // ─── Fecha mostrada al usuario: "dd/MM/yyyy HH:mm" ───────────────────────
    private val _fechaDisplay = MutableLiveData("")
    val fechaDisplay: LiveData<String> = _fechaDisplay

    // ─── Control de pickers ──────────────────────────────────────────────────
    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker: LiveData<Boolean> = _mostrarDatePicker

    private val _mostrarTimePicker = MutableLiveData(false)
    val mostrarTimePicker: LiveData<Boolean> = _mostrarTimePicker

    // Millis de la fecha elegida para combinar con la hora después
    private var fechaMillisSeleccionada: Long = 0L

    // ─── Credenciales ────────────────────────────────────────────────────────
    private val nif      = userPreferences.getNif()      ?: ""
    private val password = userPreferences.getPassword() ?: ""
    private val codiMo   = userPreferences.getCodiMO()   ?: ""

    // ─── Guía seleccionada para editar ───────────────────────────────────────
    private val _guiaSeleccionada = MutableLiveData<Guia?>(null)
    val guiaSeleccionada: LiveData<Guia?> = _guiaSeleccionada

    fun seleccionarGuia(guia: Guia) { _guiaSeleccionada.value = guia }

    // ─── Actualización de campos ─────────────────────────────────────────────
    fun onRegaChange(valor: String) { _codiRega.value = valor }

    // ─── Lógica del DatePicker / TimePicker ──────────────────────────────────
    fun mostrarDatePicker()  { _mostrarDatePicker.value = true }
    fun ocultarDatePicker()  { _mostrarDatePicker.value = false }
    fun mostrarTimePicker()  { _mostrarTimePicker.value = true }
    fun ocultarTimePicker()  { _mostrarTimePicker.value = false }

    /** El usuario eligió una fecha → guardamos los millis y abrimos el time picker */
    fun seleccionarFecha(millis: Long) {
        fechaMillisSeleccionada = millis
        _mostrarDatePicker.value = false
        _mostrarTimePicker.value = true
    }

    /** El usuario eligió una hora → componemos la cadena de display */
    fun seleccionarHora(hora: Int, minutos: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fechaMillisSeleccionada
        // Display legible para el usuario
        _fechaDisplay.value = String.format(
            "%02d/%02d/%04d %02d:%02d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR),
            hora,
            minutos
        )
        _mostrarTimePicker.value = false
    }

    // ─── Lógica de negocio ───────────────────────────────────────────────────

    fun validarPeticion() {
        val rega  = _codiRega.value.orEmpty().trim()
        val fecha = _fechaDisplay.value.orEmpty().trim()

        if (rega.isBlank()) {
            _error.value = "El código REGA es obligatorio."
            return
        }
        if (fecha.isBlank()) {
            _error.value = "La fecha de salida es obligatoria."
            return
        }

        _error.value = null
        _consultaIniciada.value = true
        cargarGuias()
    }

    fun cargarGuias() {
        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val response = repositorio.getDescargaGuiasMobilitat(
                    nif               = nif,
                    passwordMobilitat = password,
                    codiMo            = codiMo,
                    codiRega          = _codiRega.value.orEmpty(),
                    dataSortida       = displayToApiFormat(_fechaDisplay.value.orEmpty())
                )

                if (response.isSuccessful) {
                    val guias = response.body()?.guies ?: emptyList()
                    Log.d("BOVI_VM", "Guías recibidas: ${guias.size}")
                    _listaGuias.postValue(guias)
                    _cargando.postValue(false)
                } else {
                    val rawError = response.errorBody()?.string() ?: ""
                    Log.e("BOVI_VM", "HTTP ${response.code()}: $rawError")
                    _error.postValue(extraerDescripcion(rawError, response.code()))
                    _cargando.postValue(false)
                    _consultaIniciada.postValue(false)
                }

            } catch (e: Exception) {
                Log.e("BOVI_VM", "Excepción: ${e.message}")
                _error.postValue("Error de conexión: ${e.localizedMessage}")
                _cargando.postValue(false)
                _consultaIniciada.postValue(false)
            }
        }
    }

    fun resetearConsulta() {
        _consultaIniciada.value = false
        _listaGuias.value       = emptyList()
        _error.value            = null
    }

    // ─── Utilidades privadas ─────────────────────────────────────────────────

    /** "dd/MM/yyyy HH:mm" → "yyyyMMddHHmm" */
    private fun displayToApiFormat(display: String): String {
        return try {
            val partes = display.trim().split(" ")
            val (dia, mes, anio) = partes[0].split("/")
            val (h, m) = partes[1].split(":")
            "$anio$mes$dia$h$m"
        } catch (e: Exception) { "" }
    }

    /**
     * Parsea el JSON de error de la API y devuelve solo la/s descripción/es.
     * Soporta:  [ {"codi":"x","descripcio":"y"} ]
     * y también { "errors": [ {"codi":"x","descripcio":"y"} ] }
     */
    private fun extraerDescripcion(rawJson: String, httpCode: Int): String {
        if (rawJson.isBlank()) return "Error $httpCode"
        return try {
            val element = JsonParser.parseString(rawJson)
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