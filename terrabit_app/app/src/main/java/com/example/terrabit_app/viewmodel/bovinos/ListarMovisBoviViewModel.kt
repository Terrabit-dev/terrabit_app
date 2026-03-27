package com.example.terrabit_app.viewmodel.bovinos

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.moviminetos.modelos.Moviment
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ListarMovisBoviViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    val historialCamposManager: HistorialCamposManager
) : ViewModel() {

    private val _listaMovimientos = MutableLiveData<List<Moviment>>(emptyList())
    val listaMovimientos = _listaMovimientos

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _consultaIniciada = MutableLiveData(false)
    val consultaIniciada: LiveData<Boolean> = _consultaIniciada

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _codiExplotacionDesti = MutableLiveData("")
    val codiExplotacionDesti = _codiExplotacionDesti

    private val _fechaDisplay = MutableLiveData("")
    val fechaDisplay: LiveData<String> = _fechaDisplay

    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker: LiveData<Boolean> = _mostrarDatePicker

    private val _mostrarTimePicker = MutableLiveData(false)
    val mostrarTimePicker: LiveData<Boolean> = _mostrarTimePicker

    private var fechaMillisSeleccionada: Long = 0L

    private val nif      = userPreferences.getNif()      ?: ""
    private val password = userPreferences.getPassword() ?: ""

    private val _movimientoSeleccionado = MutableLiveData<Moviment?>(null)
    val movimientoSeleccionado = _movimientoSeleccionado

    fun seleccionarMovi(movimiento: Moviment) { _movimientoSeleccionado.value = movimiento }

    fun onCodiChange(valor: String) { _codiExplotacionDesti.value = valor }

    fun mostrarDatePicker()  { _mostrarDatePicker.value = true }
    fun ocultarDatePicker()  { _mostrarDatePicker.value = false }
    fun mostrarTimePicker()  { _mostrarTimePicker.value = true }
    fun ocultarTimePicker()  { _mostrarTimePicker.value = false }

    fun seleccionarFecha(millis: Long) {
        fechaMillisSeleccionada = millis
        _mostrarDatePicker.value = false
        _mostrarTimePicker.value = true
    }

    @SuppressLint("DefaultLocale")
    fun seleccionarHora(hora: Int, minutos: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fechaMillisSeleccionada
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

    fun validarPeticion() {
        val codi  = _codiExplotacionDesti.value.orEmpty().trim()
        val fecha = _fechaDisplay.value.orEmpty().trim()
        if (codi.isBlank()) {
            _error.value = "El código REGA es obligatorio."
            return
        }
        if (fecha.isBlank()) {
            _error.value = "La fecha de salida es obligatoria."
            return
        }
        _error.value = null
        _consultaIniciada.value = true
        cargarMovimientos()
    }

    fun cargarMovimientos() {
        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val response = repositorio.getConfirmacionMovimientos(
                    nif                  = nif,
                    passwordMobilitat    = password,
                    explotacioDestinacio = _codiExplotacionDesti.value.orEmpty(),
                    dataSortida          = displayToApiFormat(_fechaDisplay.value.orEmpty())
                )
                if (response.isSuccessful) {
                    val movimientos = response.body()?.moviments ?: emptyList()
                    Log.d("BOVI_VM", "Movimientos recibidos: ${movimientos.size}")
                    _listaMovimientos.postValue(movimientos)
                    _cargando.postValue(false)
                    guardarHistorialCampos()
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
        _consultaIniciada.value  = false
        _listaMovimientos.value  = emptyList()
        _error.value             = null
    }

    private suspend fun guardarHistorialCampos() {
        historialCamposManager.guardarValor("codi_rega", _codiExplotacionDesti.value ?: "")
    }

    private fun displayToApiFormat(display: String): String {
        return try {
            val partes = display.trim().split(" ")
            val (dia, mes, anio) = partes[0].split("/")
            val (h, m) = partes[1].split(":")
            "$anio$mes$dia$h$m"
        } catch (e: Exception) { "" }
    }

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