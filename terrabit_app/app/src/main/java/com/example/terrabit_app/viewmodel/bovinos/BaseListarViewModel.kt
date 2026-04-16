package com.example.terrabit_app.viewmodel.bovinos

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences
import java.util.Calendar
import com.google.gson.JsonParser

abstract class BaseListarViewModel : ViewModel() {

    // ─── Dependencias inyectadas por hijos ────────────────────────────────────
    protected abstract val repositorio: Repositorio
    protected abstract val userPreferences: UserPreferences
    abstract val historialCamposManager: HistorialCamposManager

    // ─── Credenciales ─────────────────────────────────────────────────────────
    val nif      get() = userPreferences.getNif()      ?: ""
    val password get() = userPreferences.getPassword() ?: ""
    val codiMo   get() = userPreferences.getCodiMO()   ?: ""

    // ─── Estado de UI compartido ──────────────────────────────────────────────
    protected val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    protected val _consultaIniciada = MutableLiveData(false)
    val consultaIniciada: LiveData<Boolean> = _consultaIniciada

    protected val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // ─── Fecha/hora con DatePicker + TimePicker ───────────────────────────────
    private val _fechaDisplay = MutableLiveData("")
    val fechaDisplay: LiveData<String> = _fechaDisplay

    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker: LiveData<Boolean> = _mostrarDatePicker

    private val _mostrarTimePicker = MutableLiveData(false)
    val mostrarTimePicker: LiveData<Boolean> = _mostrarTimePicker

    protected var fechaMillisSeleccionada: Long = 0L

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
        val cal = Calendar.getInstance().apply { timeInMillis = fechaMillisSeleccionada }
        _fechaDisplay.value = String.format(
            "%02d/%02d/%04d %02d:%02d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR),
            hora, minutos
        )
        _mostrarTimePicker.value = false
    }

    // ─── Reset de consulta ────────────────────────────────────────────────────
    fun resetearConsulta() {
        _consultaIniciada.value = false
        _error.value = null
        onResetearLista()
    }

    /** Limpia la lista específica del hijo (guías o movimientos) */
    protected abstract fun onResetearLista()

    /** Lanza la carga de datos del hijo */
    abstract fun cargarDatos()

    /** Valida los campos propios del hijo antes de llamar a [cargarDatos] */
    protected abstract fun validarCampos(): String?  // null = válido, String = mensaje error

    fun validarPeticion() {
        val errorMsg = validarCampos()
        if (errorMsg != null) { _error.value = errorMsg; return }
        _error.value = null
        _consultaIniciada.value = true
        cargarDatos()
    }

    // ─── Utilidades compartidas ───────────────────────────────────────────────
    protected fun displayToApiFormat(display: String): String {
        return try {
            val partes = display.trim().split(" ")
            val (dia, mes, anio) = partes[0].split("/")
            val (h, m) = partes[1].split(":")
            "$anio$mes$dia$h$m"
        } catch (e: Exception) { "" }
    }

    protected fun extraerDescripcion(rawJson: String, httpCode: Int): String {
        if (rawJson.isBlank()) return "Error $httpCode"
        return try {
            val element = JsonParser.parseString(rawJson)
            when {
                element.isJsonArray -> element.asJsonArray
                    .mapNotNull { it.asJsonObject.get("descripcio")?.asString }
                    .filter { it.isNotBlank() }.joinToString("\n").ifBlank { "Error $httpCode" }
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    obj.getAsJsonArray("errors")
                        ?.mapNotNull { it.asJsonObject.get("descripcio")?.asString }
                        ?.filter { it.isNotBlank() }?.joinToString("\n")
                        ?.ifBlank { obj.get("descripcio")?.asString ?: "Error $httpCode" }
                        ?: obj.get("descripcio")?.asString ?: "Error $httpCode"
                }
                else -> "Error $httpCode"
            }
        } catch (e: Exception) { "Error $httpCode" }
    }
}