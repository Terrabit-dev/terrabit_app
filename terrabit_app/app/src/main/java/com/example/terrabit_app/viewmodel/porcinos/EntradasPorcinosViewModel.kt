package com.example.terrabit_app.viewmodel.porcinos

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.ConfirmarMovimientosRequest
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class EntradasPorcinosViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EntradasPorcinosUiState())
    val uiState: StateFlow<EntradasPorcinosUiState> = _uiState.asStateFlow()

    private val repositorio = Repositorio(application)
    private lateinit var userPreferences: UserPreferences

    // ── Fecha ────────────────────────────────────────────────────────────────
    private val _fechaDisplay = MutableStateFlow("")
    val fechaDisplay: StateFlow<String> = _fechaDisplay

    private val _mostrarDatePicker = MutableStateFlow(false)
    val mostrarDatePicker: StateFlow<Boolean> = _mostrarDatePicker

    private val _mostrarTimePicker = MutableStateFlow(false)
    val mostrarTimePicker: StateFlow<Boolean> = _mostrarTimePicker

    private val _consultaIniciada = MutableStateFlow(false)
    val consultaIniciada: StateFlow<Boolean> = _consultaIniciada

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var fechaMillisSeleccionada: Long = 0L

    // ── Pickers ──────────────────────────────────────────────────────────────
    fun mostrarDatePicker() { _mostrarDatePicker.value = true }
    fun ocultarDatePicker() { _mostrarDatePicker.value = false }
    fun ocultarTimePicker() { _mostrarTimePicker.value = false }

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
            hora, minutos
        )
        _mostrarTimePicker.value = false
    }

    // ── Inicialización ───────────────────────────────────────────────────────
    fun inicializarUserPreferences(context: Context) {
        userPreferences = UserPreferences(context)
    }

    // ── Validar y lanzar consulta ────────────────────────────────────────────
    fun validarYConsultar() {
        if (_fechaDisplay.value.isBlank()) {
            _error.value = "La fecha de sortida és obligatòria."
            return
        }
        _error.value = null
        _consultaIniciada.value = true
        cargarGuiasPendientes()
    }

    fun resetearConsulta() {
        _consultaIniciada.value = false
        _uiState.update { it.copy(listaEntradasPorcinos = emptyList()) }
        _fechaDisplay.value = ""
        _error.value = null
    }

    // ── GET ──────────────────────────────────────────────────────────────────
    private fun cargarGuiasPendientes() {
        val fechaFin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        val fechaInicio = displayToApiFormat(_fechaDisplay.value)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repositorio.getPendientesConfirmarEntradaPorcina(
                    nif          = userPreferences.getNif()      ?: "",
                    password     = userPreferences.getPassword() ?: "",
                    moDesti      = userPreferences.getCodiMO()   ?: "",
                    desde        = fechaInicio,
                    fins         = fechaFin
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(
                        listaEntradasPorcinos = response.body()?.llistat ?: emptyList(),
                        isLoading = false
                    )}
                } else {
                    _error.value = "Error ${response.code()}"
                    _uiState.update { it.copy(isLoading = false) }
                    _consultaIniciada.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error de connexió: ${e.localizedMessage}"
                _uiState.update { it.copy(isLoading = false) }
                _consultaIniciada.value = false
            }
        }
    }

    // ── Confirmar entrada ────────────────────────────────────────────────────
    fun confirmarEntrada(guia: MovimentPteDetail) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = ConfirmarMovimientosRequest(
                nif          = userPreferences.getNif()      ?: "",
                password     = userPreferences.getPassword() ?: "",
                moDesti      = guia.moDesti,
                remo         = guia.codiRemo,
                codiAtes     = guia.codiAtes,
                nifConductor = guia.nifConductor ?: "",
                matricula    = guia.matricula    ?: "",
                nombreAnimals = guia.numAnimals  ?: "0"
            )
            try {
                val response = repositorio.confirmarEntradaPorcina(request)
                if (response.isSuccessful && response.body()?.codi == "OK") {
                    _uiState.update { it.copy(
                        listaEntradasPorcinos = it.listaEntradasPorcinos.filter { it.codiRemo != guia.codiRemo },
                        isLoading = false
                    )}
                } else {
                    _error.value = response.body()?.descripcio ?: "Error al confirmar"
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ── Utils ────────────────────────────────────────────────────────────────
    private fun displayToApiFormat(display: String): String {
        return try {
            val partes = display.trim().split(" ")
            val (dia, mes, anio) = partes[0].split("/")
            val (h, m) = partes[1].split(":")
            "$anio$mes$dia$h$m"
        } catch (e: Exception) { "000101010000" }
    }
}