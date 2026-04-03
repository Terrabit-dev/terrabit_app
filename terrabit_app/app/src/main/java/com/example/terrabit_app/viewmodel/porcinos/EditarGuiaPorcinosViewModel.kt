package com.example.terrabit_app.viewmodel.porcinos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.DataClassPorcinos.GtrStandardResponse
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.EditarGuiasPorcionsUiState
import com.example.terrabit_app.utils.UserPreferences
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EditarGuiaPorcinosViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    val historialCamposManager: HistorialCamposManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarGuiasPorcionsUiState())
    val uiState: StateFlow<EditarGuiasPorcionsUiState> = _uiState.asStateFlow()

    fun seleccionarCategoria(nombre: String, codigo: String) {
        _uiState.update { it.copy(categoriaSeleccionada = nombre, categoriaCodigo = codigo, categoriaExpandido = false) }
    }

    fun toggleCategoriaExpandido() {
        _uiState.update { it.copy(categoriaExpandido = !it.categoriaExpandido) }
    }

    fun cerrarCategoriaMenu() {
        _uiState.update { it.copy(categoriaExpandido = false) }
    }

    fun actualizarNumAnimales(valor: String)   { _uiState.update { it.copy(numAnimales = valor) } }
    fun actualizarCodigoSIR(valor: String)     { _uiState.update { it.copy(codigoSIR = valor) } }
    fun actualizarMatricula(valor: String)     { _uiState.update { it.copy(matricula = valor) } }
    fun actualizarNifConductor(valor: String)  { _uiState.update { it.copy(nifConductor = valor) } }

    fun mostrarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = true) } }
    fun ocultarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = false) } }
    fun mostrarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = true) } }
    fun ocultarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = false) } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seleccionarFechaSalida(millis: Long) {
        _uiState.update { it.copy(fechaSalida = formatLongToDate(millis), mostrarDatePickerSalida = false, mostrarTimePickerSalida = true) }
    }

    fun actualizarHoraSalida(h: String, m: String) {
        _uiState.update { it.copy(horaSalida = "${h.padStart(2, '0')}:${m.padStart(2, '0')}") }
    }

    fun mostrarDatePickerLlegada()  { _uiState.update { it.copy(mostrarDatePickerLlegada = true) } }
    fun ocultarDatePickerLlegada()  { _uiState.update { it.copy(mostrarDatePickerLlegada = false) } }
    fun mostrarTimePickerLlegada()  { _uiState.update { it.copy(mostrarTimePickerLlegada = true) } }
    fun ocultarTimePickerLlegada()  { _uiState.update { it.copy(mostrarTimePickerLlegada = false) } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seleccionarFechaLlegada(millis: Long) {
        _uiState.update { it.copy(fechaLlegada = formatLongToDate(millis), mostrarDatePickerLlegada = false, mostrarTimePickerLlegada = true) }
    }

    fun actualizarHoraLlegada(h: String, m: String) {
        _uiState.update { it.copy(horaLlegada = "${h.padStart(2, '0')}:${m.padStart(2, '0')}") }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarDatosGuia(guia: GuiaGTRLista) {
        val utils = ElementosConCodigosPorcinos()
        val nombreCategoria = utils.categoriasB().filterValues { it == guia.categoria }.keys.firstOrNull() ?: "Desconocida"
        _uiState.update { currentState ->
            currentState.copy(
                remoActual            = guia.remo,
                categoriaSeleccionada = nombreCategoria,
                categoriaCodigo       = guia.categoria,
                numAnimales           = guia.nombreAnimals.toString(),
                fechaSalida           = formatLongToDate(guia.dataSortida),
                horaSalida            = formatLongToTime(guia.dataSortida),
                fechaLlegada          = formatLongToDate(guia.dataArribada),
                horaLlegada           = formatLongToTime(guia.dataArribada),
                codigoSIR             = guia.transportista ?: "",
                matricula             = guia.vehicle ?: "",
                nifConductor          = guia.responsable ?: ""
            )
        }
    }

    private fun guardarHistorialCampos() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.codigoSIR.isNotBlank())    historialCamposManager.guardarValor("porcinos_sir", s.codigoSIR)
            if (s.matricula.isNotBlank())     historialCamposManager.guardarValor("porcinos_matricula", s.matricula)
            if (s.nifConductor.isNotBlank()) historialCamposManager.guardarValor("porcinos_nif_conductor", s.nifConductor)
        }
    }

    fun editarYConfirmarGuia(onSuccess: () -> Unit) {
        val state = _uiState.value

        val camposVacios = state.categoriaCodigo.isBlank() ||
                state.numAnimales.isBlank() ||
                state.fechaSalida.isBlank() ||
                state.horaSalida.isBlank() ||
                state.fechaLlegada.isBlank() ||
                state.horaLlegada.isBlank() ||
                state.codigoSIR.isBlank() ||
                state.matricula.isBlank() ||
                state.nifConductor.isBlank()

        if (camposVacios) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val fechaS = combinarFechaHora(state.fechaSalida, state.horaSalida)
            val fechaA = combinarFechaHora(state.fechaLlegada, state.horaLlegada)

            val nif      = userPreferences.getNif()      ?: ""
            val password = userPreferences.getPassword() ?: ""

            val request = ModificarMovimentsAGias(
                nif           = nif,
                password      = password,
                remo          = state.remoActual,
                categoria     = state.categoriaCodigo,
                nombreAnimals = state.numAnimales,
                transportista = state.codigoSIR,
                responsable   = state.nifConductor,
                vehicle       = state.matricula,
                dataSortida   = fechaS,
                dataArribada  = fechaA
            )

            Log.d("EDITAR_GUIA", "Request enviada: $request")

            try {
                val response = repositorio.tramitarGuiaPorcina(request)

                if (response.isSuccessful) {
                    val rawJson = response.body()?.string() ?: ""
                    Log.d("EDITAR_GUIA", "Raw JSON: $rawJson")

                    val gson = Gson()
                    if (rawJson.trimStart().startsWith("[")) {
                        val errores = gson.fromJson(rawJson, Array<GtrStandardResponse>::class.java)
                        val primerError = errores.firstOrNull()
                        Log.w("EDITAR_GUIA", "API devolvió errores: ${primerError?.descripcio}")
                        _uiState.update { it.copy(isLoading = false, error = primerError?.descripcio) }
                    } else {
                        val resultado = gson.fromJson(rawJson, GtrStandardResponse::class.java)
                        if (resultado.codi == "OK") {
                            Log.i("EDITAR_GUIA", "Trámite completado correctamente")
                            guardarHistorialCampos()
                            _uiState.update { it.copy(isLoading = false) }
                            onSuccess()
                        } else {
                            Log.w("EDITAR_GUIA", "API rechazó: ${resultado.descripcio}")
                            _uiState.update { it.copy(isLoading = false, error = resultado.descripcio) }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EDITAR_GUIA", "Error HTTP ${response.code()}: $errorBody")
                    _uiState.update { it.copy(isLoading = false, error = "Error servidor (${response.code()})") }
                }
            } catch (e: Exception) {
                Log.e("EDITAR_GUIA", "EXCEPCIÓN: ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatLongToDate(millis: Long): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatLongToTime(millis: Long): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun combinarFechaHora(fecha: String, hora: String): String {
        val partesFecha = fecha.split("/")
        val partesHora  = hora.split(":")
        if (partesFecha.size < 3 || partesHora.size < 2) return ""
        return "${partesFecha[2]}${partesFecha[1]}${partesFecha[0]}${partesHora[0]}${partesHora[1]}"
    }
}