package com.example.terrabit_app.viewmodel.porcinos

import android.os.Build
import com.example.terrabit_app.utils.SecureLog
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
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    val historialCamposManager: HistorialCamposManager
) : BasePorcinosViewModel() {

    private val _uiState = MutableStateFlow(EditarGuiasPorcionsUiState())
    val uiState: StateFlow<EditarGuiasPorcionsUiState> = _uiState.asStateFlow()

    // ─── Carga de datos desde guía seleccionada ───────────────────────────────
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarDatosGuia(guia: GuiaGTRLista) {
        val nombreCategoria = ElementosConCodigosPorcinos().categoriasB()
            .filterValues { it == guia.categoria }.keys.firstOrNull() ?: "Desconocida"
        _uiState.update {
            it.copy(
                remoActual            = guia.remo,
                categoriaSeleccionada = nombreCategoria,
                categoriaCodigo       = guia.categoria,
                numAnimales           = guia.nombreAnimals.toString(),
                fechaSalida           = formatMillisToDate(guia.dataSortida),
                horaSalida            = formatMillisToTime(guia.dataSortida),
                fechaLlegada          = formatMillisToDate(guia.dataArribada),
                horaLlegada           = formatMillisToTime(guia.dataArribada),
                codigoSIR             = guia.transportista ?: "",
                matricula             = guia.vehicle       ?: "",
                nifConductor          = guia.responsable   ?: ""
            )
        }
    }

    // ─── Dropdowns ────────────────────────────────────────────────────────────
    fun seleccionarCategoria(nombre: String, codigo: String) {
        _uiState.update { it.copy(categoriaSeleccionada = nombre, categoriaCodigo = codigo, categoriaExpandido = false) }
    }
    fun toggleCategoriaExpandido() { _uiState.update { it.copy(categoriaExpandido = !it.categoriaExpandido) } }
    fun cerrarCategoriaMenu()      { _uiState.update { it.copy(categoriaExpandido = false) } }

    // ─── Campos ───────────────────────────────────────────────────────────────
    fun actualizarNumAnimales(valor: String)  { _uiState.update { it.copy(numAnimales = valor) } }
    fun actualizarCodigoSIR(valor: String)    { _uiState.update { it.copy(codigoSIR = valor) } }
    fun actualizarMatricula(valor: String)    { _uiState.update { it.copy(matricula = valor) } }
    fun actualizarNifConductor(valor: String) { _uiState.update { it.copy(nifConductor = valor) } }

    // ─── DatePicker / TimePicker salida ──────────────────────────────────────
    fun mostrarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = true) } }
    fun ocultarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = false) } }
    fun mostrarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = true) } }
    fun ocultarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = false) } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seleccionarFechaSalida(millis: Long) {
        _uiState.update { it.copy(fechaSalida = formatMillisToDate(millis),
            mostrarDatePickerSalida = false, mostrarTimePickerSalida = true) }
    }
    fun actualizarHoraSalida(h: String, m: String) {
        _uiState.update { it.copy(horaSalida = "${h.padStart(2,'0')}:${m.padStart(2,'0')}") }
    }

    // ─── DatePicker / TimePicker llegada ─────────────────────────────────────
    fun mostrarDatePickerLlegada()  { _uiState.update { it.copy(mostrarDatePickerLlegada = true) } }
    fun ocultarDatePickerLlegada()  { _uiState.update { it.copy(mostrarDatePickerLlegada = false) } }
    fun mostrarTimePickerLlegada()  { _uiState.update { it.copy(mostrarTimePickerLlegada = true) } }
    fun ocultarTimePickerLlegada()  { _uiState.update { it.copy(mostrarTimePickerLlegada = false) } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seleccionarFechaLlegada(millis: Long) {
        _uiState.update { it.copy(fechaLlegada = formatMillisToDate(millis),
            mostrarDatePickerLlegada = false, mostrarTimePickerLlegada = true) }
    }
    fun actualizarHoraLlegada(h: String, m: String) {
        _uiState.update { it.copy(horaLlegada = "${h.padStart(2,'0')}:${m.padStart(2,'0')}") }
    }

    // ─── Envío ────────────────────────────────────────────────────────────────
    fun editarYConfirmarGuia(onSuccess: () -> Unit) {
        val s = _uiState.value
        if (s.categoriaCodigo.isBlank() || s.numAnimales.isBlank() || s.fechaSalida.isBlank() ||
            s.horaSalida.isBlank() || s.fechaLlegada.isBlank() || s.horaLlegada.isBlank() ||
            s.codigoSIR.isBlank() || s.matricula.isBlank() || s.nifConductor.isBlank()) {
            SecureLog.w("GTR_EDITAR", "Validación fallida — campos vacíos")
            _uiState.update { it.copy(error = "Todos los campos son obligatorios.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val request = ModificarMovimentsAGias(
                    nif = nif, password = password, remo = s.remoActual,
                    categoria = s.categoriaCodigo, nombreAnimals = s.numAnimales,
                    transportista = s.codigoSIR, responsable = s.nifConductor,
                    vehicle = s.matricula,
                    dataSortida = convertirFechaHoraAFormatoAPI(s.fechaSalida, s.horaSalida),
                    dataArribada = convertirFechaHoraAFormatoAPI(s.fechaLlegada, s.horaLlegada)
                )



                val response = repositorio.tramitarGuiaPorcina(request)

                SecureLog.d("GTR_EDITAR", "HTTP code: ${response.code()}")

                if (response.isSuccessful) {
                    val rawJson = response.body()?.string() ?: ""


                    val gson = Gson()
                    if (rawJson.trimStart().startsWith("[")) {
                        val error = gson.fromJson(rawJson, Array<GtrStandardResponse>::class.java).firstOrNull()
                        SecureLog.w("GTR_EDITAR", "API devolvió array de errores: ${error?.descripcio}")
                        _uiState.update { it.copy(isLoading = false, error = error?.descripcio) }
                    } else {
                        val resultado = gson.fromJson(rawJson, GtrStandardResponse::class.java)
                        SecureLog.d("GTR_EDITAR", "resultado.codi: ${resultado.codi} | resultado.descripcio: ${resultado.descripcio}")
                        if (resultado.codi == "OK") {
                            SecureLog.d("GTR_EDITAR", "Edición completada")
                            guardarHistorialCampos()
                            _uiState.update { it.copy(isLoading = false) }
                            onSuccess()
                        } else {
                            SecureLog.w("GTR_EDITAR", "⚠️ API rechazó: ${resultado.descripcio}")
                            _uiState.update { it.copy(isLoading = false, error = resultado.descripcio) }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    SecureLog.e("GTR_EDITAR", "Error HTTP ${response.code()}: $errorBody")
                    _uiState.update { it.copy(isLoading = false, error = "Error servidor (${response.code()})") }
                }
            } catch (e: Exception) {
                SecureLog.e("GTR_EDITAR", "Excepción: ${e.javaClass.simpleName}: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
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
    // ─── Utilidades de fecha con java.time (solo en este VM) ──────────────────
    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatMillisToDate(millis: Long): String =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatMillisToTime(millis: Long): String =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH:mm"))
}