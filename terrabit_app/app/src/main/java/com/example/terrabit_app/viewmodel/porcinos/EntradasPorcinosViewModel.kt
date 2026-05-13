package com.example.terrabit_app.viewmodel.porcinos

import android.annotation.SuppressLint
import android.os.Build
import com.example.terrabit_app.utils.SecureLog
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.ConfirmarMovimientosRequest
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EntradasPorcinosViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences
) : BasePorcinosViewModel() {

    private val _uiState = MutableStateFlow(EntradasPorcinosUiState())
    val uiState: StateFlow<EntradasPorcinosUiState> = _uiState.asStateFlow()

    // ─── Fecha display con DatePicker + TimePicker ────────────────────────────
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
        val cal = Calendar.getInstance().apply { timeInMillis = fechaMillisSeleccionada }
        _fechaDisplay.value = String.format("%02d/%02d/%04d %02d:%02d",
            cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR), hora, minutos)
        _mostrarTimePicker.value = false
    }

    fun validarYConsultar() {
        if (_fechaDisplay.value.isBlank()) { _error.value = "La fecha de sortida és obligatòria."; return }
        _error.value = null; _consultaIniciada.value = true
        cargarGuiasPendientes()
    }

    fun resetearConsulta() {
        _consultaIniciada.value = false
        _uiState.update { it.copy(listaEntradasPorcinos = emptyList()) }
        _fechaDisplay.value = ""; _error.value = null
    }

    private fun cargarGuiasPendientes() {
        val fechaFin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        val fechaInicio = displayToApiFormat(_fechaDisplay.value)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                SecureLog.d("GTR_ENTRADAS", "━━━ CARGAR PENDIENTES ━━━")
                SecureLog.d("GTR_ENTRADAS", "nif:        $nif")
                SecureLog.d("GTR_ENTRADAS", "codiMo:     $codiMo")
                SecureLog.d("GTR_ENTRADAS", "fechaInicio: $fechaInicio")
                SecureLog.d("GTR_ENTRADAS", "fechaFin:   $fechaFin")

                val response = repositorio.getPendientesConfirmarEntradaPorcina(
                    nif = nif, password = password, moDesti = codiMo,
                    desde = fechaInicio, fins = fechaFin
                )

                SecureLog.d("GTR_ENTRADAS", "HTTP code: ${response.code()}")
                SecureLog.d("GTR_ENTRADAS", "isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val lista = response.body()?.llistat ?: emptyList()
                    SecureLog.d("GTR_ENTRADAS", "✅ Entradas recibidas: ${lista.size}")
                    lista.forEachIndexed { i, e -> SecureLog.d("GTR_ENTRADAS", "  [$i] remo=${e.codiRemo}") }
                    _uiState.update { it.copy(listaEntradasPorcinos = lista, isLoading = false) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    SecureLog.e("GTR_ENTRADAS", "❌ Error HTTP ${response.code()}: $errorBody")
                    _error.value = "Error ${response.code()}"
                    _uiState.update { it.copy(isLoading = false) }
                    _consultaIniciada.value = false
                }
            } catch (e: Exception) {
                SecureLog.e("GTR_ENTRADAS", "❌ Excepción: ${e.javaClass.simpleName}: ${e.message}", e)
                _error.value = "Error de connexió: ${e.localizedMessage}"
                _uiState.update { it.copy(isLoading = false) }
                _consultaIniciada.value = false
            }
        }
    }

    fun confirmarEntrada(guia: MovimentPteDetail) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = ConfirmarMovimientosRequest(
                nif = nif, password = password, moDesti = guia.moDesti,
                remo = guia.codiRemo, codiAtes = guia.codiAtes,
                nifConductor = guia.nifConductor ?: "", matricula = guia.matricula ?: "",
                nombreAnimals = guia.numAnimals ?: "0"
            )

            SecureLog.d("GTR_ENTRADAS", "━━━ CONFIRMAR ENTRADA ━━━")
            SecureLog.d("GTR_ENTRADAS", "request: $request")

            try {
                val response = repositorio.confirmarEntradaPorcina(request)

                SecureLog.d("GTR_ENTRADAS", "HTTP code: ${response.code()}")
                SecureLog.d("GTR_ENTRADAS", "body.codi: ${response.body()?.codi}")
                SecureLog.d("GTR_ENTRADAS", "body.descripcio: ${response.body()?.descripcio}")

                if (response.isSuccessful && response.body()?.codi == "OK") {
                    SecureLog.d("GTR_ENTRADAS", "✅ Entrada confirmada: ${guia.codiRemo}")
                    _uiState.update { it.copy(
                        listaEntradasPorcinos = it.listaEntradasPorcinos.filter { it.codiRemo != guia.codiRemo },
                        isLoading = false
                    )}
                } else {
                    val msg = response.body()?.descripcio ?: "Error al confirmar"
                    SecureLog.w("GTR_ENTRADAS", "⚠️ $msg")
                    _error.value = msg
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                SecureLog.e("GTR_ENTRADAS", "❌ Excepción: ${e.javaClass.simpleName}: ${e.message}", e)
                _error.value = e.localizedMessage
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}