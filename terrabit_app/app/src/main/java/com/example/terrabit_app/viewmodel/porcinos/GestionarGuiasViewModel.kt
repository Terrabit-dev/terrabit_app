package com.example.terrabit_app.viewmodel.porcinos

import android.annotation.SuppressLint
import com.example.terrabit_app.utils.SecureLog
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.GtrErrorResponseLista
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class GestionarGuiasViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences
) : BasePorcinosViewModel() {

    private val _uiState = MutableStateFlow(GestionarGuiasPorcinosUiState())
    val uiState: StateFlow<GestionarGuiasPorcinosUiState> = _uiState.asStateFlow()

    private var fechaMillisSeleccionada: Long = 0L

    // ─── Campos ───────────────────────────────────────────────────────────────
    fun actualizarRega(valor: String) { _uiState.update { it.copy(rega = valor) } }

    // ─── DatePicker / TimePicker ──────────────────────────────────────────────
    fun mostrarDatePicker() { _uiState.update { it.copy(mostrarDatePicker = true) } }
    fun ocultarDatePicker() { _uiState.update { it.copy(mostrarDatePicker = false) } }
    fun mostrarTimePicker() { _uiState.update { it.copy(mostrarTimePicker = true) } }
    fun ocultarTimePicker() { _uiState.update { it.copy(mostrarTimePicker = false) } }

    fun seleccionarFecha(millis: Long) {
        fechaMillisSeleccionada = millis
        _uiState.update { it.copy(mostrarDatePicker = false, mostrarTimePicker = true) }
    }

    @SuppressLint("DefaultLocale")
    fun seleccionarHora(hora: Int, minutos: Int) {
        val cal = Calendar.getInstance().apply { timeInMillis = fechaMillisSeleccionada }
        val display = String.format("%02d/%02d/%04d %02d:%02d",
            cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR), hora, minutos)
        // Formato API construido directamente con los valores del calendario
        val apiFormat = String.format("%04d%02d%02d%02d%02d",
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH), hora, minutos)
        _uiState.update { it.copy(fechaCorteDisplay = display, fechaCorte = apiFormat, mostrarTimePicker = false) }
    }

    // ─── Consulta ─────────────────────────────────────────────────────────────
    fun consultarLista() {
        val s = _uiState.value
        if (s.rega.isBlank()) { _uiState.update { it.copy(mensajeError = "El código REGA es obligatorio.") }; return }
        if (s.fechaCorte.isBlank()) { _uiState.update { it.copy(mensajeError = "La fecha de corte es obligatoria.") }; return }
        _uiState.update { it.copy(consultaIniciada = true, mensajeError = null) }
        cargarMovimientosDesdeApi(s.rega, s.fechaCorte)
    }

    private fun cargarMovimientosDesdeApi(rega: String, fechaCorte: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                SecureLog.d("GTR_GESTIONAR", "━━━ CONSULTA LISTA ━━━")
                SecureLog.d("GTR_GESTIONAR", "nif:       $nif")
                SecureLog.d("GTR_GESTIONAR", "password:  $password")
                SecureLog.d("GTR_GESTIONAR", "codiMo:    $codiMo")
                SecureLog.d("GTR_GESTIONAR", "rega:      $rega")
                SecureLog.d("GTR_GESTIONAR", "fechaCorte: $fechaCorte")

                val response = repositorio.getGuiasMobilitatPorcinas(nif, password, codiMo, rega, fechaCorte)

                SecureLog.d("GTR_GESTIONAR", "━━━ RESPUESTA ━━━")
                SecureLog.d("GTR_GESTIONAR", "HTTP code: ${response.code()}")
                SecureLog.d("GTR_GESTIONAR", "isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val rawJson = response.body()?.string() ?: ""
                    SecureLog.d("GTR_GESTIONAR", "rawJson: $rawJson")

                    if (rawJson.isBlank()) {
                        SecureLog.e("GTR_GESTIONAR", "❌ rawJson vacío")
                        _uiState.update { it.copy(isLoading = false, mensajeError = "Respuesta vacía del servidor", consultaIniciada = false) }
                        return@launch
                    }

                    val gson = Gson()
                    val jsonArray = JsonParser.parseString(rawJson).asJsonArray
                    SecureLog.d("GTR_GESTIONAR", "jsonArray size: ${jsonArray.size()}")
                    SecureLog.d("GTR_GESTIONAR", "primer elemento: ${jsonArray.firstOrNull()?.asJsonObject}")

                    val primerElemento = jsonArray.firstOrNull()?.asJsonObject
                    if (primerElemento?.has("moOrigen") == true) {
                        val lista = gson.fromJson(rawJson, Array<GuiaGTRLista>::class.java).toList()
                        SecureLog.d("GTR_GESTIONAR", "✅ Guías recibidas: ${lista.size}")
                        lista.forEachIndexed { i, g -> SecureLog.d("GTR_GESTIONAR", "  [$i] remo=${g.remo} moOrigen=${g.moOrigen}") }
                        _uiState.update { it.copy(listaGuiasPorcinos = lista, isLoading = false, mensajeError = null) }
                    } else {
                        val msg = gson.fromJson(rawJson, Array<GtrErrorResponseLista>::class.java)
                            .firstOrNull()?.descripcio ?: "Error desconocido"
                        SecureLog.w("GTR_GESTIONAR", "⚠️ API devolvió error: $msg")
                        _uiState.update { it.copy(isLoading = false, mensajeError = msg, consultaIniciada = false) }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    SecureLog.e("GTR_GESTIONAR", "❌ Error HTTP ${response.code()}: $errorBody")
                    _uiState.update { it.copy(isLoading = false,
                        mensajeError = extraerDescripcion(errorBody, response.code()),
                        consultaIniciada = false) }
                }
            } catch (e: Exception) {
                SecureLog.e("GTR_GESTIONAR", "❌ Excepción: ${e.javaClass.simpleName}: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false,
                    mensajeError = "Error de conexión: ${e.localizedMessage}",
                    consultaIniciada = false) }
            }
        }
    }

    fun resetearConsulta() {
        _uiState.update { it.copy(consultaIniciada = false, listaGuiasPorcinos = emptyList(),
            mensajeError = null, fechaCorteDisplay = "", fechaCorte = "") }
    }

    // ─── Confirmar guía ───────────────────────────────────────────────────────
    fun confirmarGuia(guia: GuiaGTRLista) {
        if (guia.transportista.isNullOrBlank() || guia.vehicle.isNullOrBlank() || guia.responsable.isNullOrBlank()) {
            _uiState.update { it.copy(mensajeError = "Faltan datos obligatorios. Por favor, edite la guía antes de confirmar.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val request = ModificarMovimentsAGias(
                    nif = nif, password = password, remo = guia.remo,
                    categoria = guia.categoria, nombreAnimals = guia.nombreAnimals.toString(),
                    transportista = guia.transportista, responsable = guia.responsable,
                    vehicle = guia.vehicle, dataSortida = guia.dataSortida.toString(),
                    dataArribada = guia.dataArribada.toString()
                )
                val response = repositorio.tramitarGuiaPorcina(request)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false,
                        mensajeError = extraerDescripcion(response.errorBody()?.string() ?: "", response.code())) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error de conexión: ${e.localizedMessage}") }
            }
        }
    }
}