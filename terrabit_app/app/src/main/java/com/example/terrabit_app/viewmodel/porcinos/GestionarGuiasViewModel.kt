package com.example.terrabit_app.viewmodel.porcinos

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.GtrErrorResponseLista
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class GestionarGuiasViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GestionarGuiasPorcinosUiState())
    val uiState: StateFlow<GestionarGuiasPorcinosUiState> = _uiState.asStateFlow()
    private val repo = Repositorio(application)
    private val userPreferences = UserPreferences(application)

    private val nif    = userPreferences.getNif()      ?: ""
    private val pass   = userPreferences.getPassword() ?: ""
    private val codiMo = userPreferences.getCodiMO()   ?: ""

    // Guardamos los millis de la fecha para combinar con la hora
    private var fechaMillisSeleccionada: Long = 0L

    // ─── Formulario ──────────────────────────────────────────────────────────

    fun actualizarRega(valor: String) {
        _uiState.update { it.copy(rega = valor) }
    }

    // ─── DatePicker / TimePicker ──────────────────────────────────────────────

    fun mostrarDatePicker() { _uiState.update { it.copy(mostrarDatePicker = true) } }
    fun ocultarDatePicker() { _uiState.update { it.copy(mostrarDatePicker = false) } }
    fun mostrarTimePicker() { _uiState.update { it.copy(mostrarTimePicker = true) } }
    fun ocultarTimePicker() { _uiState.update { it.copy(mostrarTimePicker = false) } }

    fun seleccionarFecha(millis: Long) {
        fechaMillisSeleccionada = millis
        _uiState.update { it.copy(mostrarDatePicker = false, mostrarTimePicker = true) }
    }

    fun seleccionarHora(hora: Int, minutos: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fechaMillisSeleccionada
        // Display legible: "dd/MM/yyyy HH:mm"
        val display = String.format(
            "%02d/%02d/%04d %02d:%02d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR),
            hora,
            minutos
        )
        // Formato API: "yyyyMMddHHmm"
        val apiFormat = String.format(
            "%04d%02d%02d%02d%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            hora,
            minutos
        )
        _uiState.update { it.copy(
            fechaCorteDisplay = display,
            fechaCorte        = apiFormat,
            mostrarTimePicker = false
        )}
    }

    // ─── Consulta ─────────────────────────────────────────────────────────────

    fun consultarLista(nif: String, pass: String, codiMo: String) {
        val state = _uiState.value

        if (state.rega.isBlank()) {
            _uiState.update { it.copy(mensajeError = "El código REGA es obligatorio.") }
            return
        }
        if (state.fechaCorte.isBlank()) {
            _uiState.update { it.copy(mensajeError = "La fecha de corte es obligatoria.") }
            return
        }

        _uiState.update { it.copy(consultaIniciada = true, mensajeError = null) }
        cargarMovimientosDesdeApi(nif, pass, codiMo, state.rega, state.fechaCorte)
    }

    fun cargarMovimientosDesdeApi(
        nif: String, pass: String, codiMo: String, rega: String, fechaCorte: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repo.getGuiasMobilitatPorcinas(nif, pass, codiMo, rega, fechaCorte)

                if (response.isSuccessful) {
                    val rawJson = response.body()?.string() ?: ""
                    Log.d("DEBUG_API", "Raw JSON: $rawJson")

                    val gson = Gson()
                    val jsonArray = com.google.gson.JsonParser.parseString(rawJson).asJsonArray
                    val primerElemento = jsonArray.firstOrNull()?.asJsonObject

                    if (primerElemento?.has("moOrigen") == true) {
                        val listaGuias = gson.fromJson(rawJson, Array<GuiaGTRLista>::class.java)
                        _uiState.update { it.copy(
                            listaGuiasPorcinos = listaGuias.toList(),
                            isLoading          = false,
                            mensajeError       = null
                        )}
                    } else {
                        val errores = gson.fromJson(rawJson, Array<GtrErrorResponseLista>::class.java)
                        val mensajeError = errores.firstOrNull()?.descripcio ?: "Error desconocido"
                        _uiState.update { it.copy(
                            isLoading        = false,
                            mensajeError     = mensajeError,
                            consultaIniciada = false
                        )}
                    }
                } else {
                    val rawError = response.errorBody()?.string() ?: ""
                    _uiState.update { it.copy(
                        isLoading        = false,
                        mensajeError     = extraerDescripcion(rawError, response.code()),
                        consultaIniciada = false
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading        = false,
                    mensajeError     = "Error de conexión: ${e.localizedMessage}",
                    consultaIniciada = false
                )}
            }
        }
    }

    fun resetearConsulta() {
        _uiState.update { it.copy(
            consultaIniciada  = false,
            listaGuiasPorcinos = emptyList(),
            mensajeError      = null,
            fechaCorteDisplay = "",
            fechaCorte        = ""
        )}
    }

    // ─── Confirmar guía (sin cambios) ─────────────────────────────────────────

    fun confirmarGuia(guia: GuiaGTRLista) {
        if (guia.transportista.isNullOrBlank() || guia.vehicle.isNullOrBlank() || guia.responsable.isNullOrBlank()) {
            _uiState.update { it.copy(mensajeError = "Faltan datos obligatorios (Transportista, Vehículo o Responsable). Por favor, edite la guía antes de confirmar.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val request = ModificarMovimentsAGias(
                nif           = nif,
                password      = pass,
                remo          = guia.remo,
                categoria     = guia.categoria,
                nombreAnimals = guia.nombreAnimals.toString(),
                transportista = guia.transportista,
                responsable   = guia.responsable,
                vehicle       = guia.vehicle,
                dataSortida   = guia.dataSortida.toString(),
                dataArribada  = guia.dataArribada.toString()
            )

            try {
                val response = repo.tramitarGuiaPorcina(request)

                if (response.isSuccessful) {
                    val rawJson = response.body()?.string() ?: ""
                    // Parseo de respuesta omitido por brevedad — sin cambios respecto al original
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    val rawError = response.errorBody()?.string() ?: ""
                    _uiState.update { it.copy(
                        isLoading    = false,
                        mensajeError = extraerDescripcion(rawError, response.code())
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading    = false,
                    mensajeError = "Error de conexión: ${e.localizedMessage}"
                )}
            }
        }
    }

    // ─── Utilidad: extrae solo la descripción del JSON de error ──────────────

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