package com.example.terrabit_app.viewmodel.porcinos

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.EditarGuiasPorcionsUiState
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale


class EditarGuiaPorcinosViewModel(
    context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditarGuiasPorcionsUiState())
    val uiState: StateFlow<EditarGuiasPorcionsUiState> = _uiState.asStateFlow()

    private val repositorio = Repositorio(context)

    // --- CATEGORÍA ---
    fun seleccionarCategoria(nombre: String, codigo: String) {
        _uiState.update { it.copy(
            categoriaSeleccionada = nombre,
            categoriaCodigo = codigo,
            categoriaExpandido = false
        )}
    }

    fun toggleCategoriaExpandido() {
        _uiState.update { it.copy(categoriaExpandido = !it.categoriaExpandido) }
    }

    fun cerrarCategoriaMenu() {
        _uiState.update { it.copy(categoriaExpandido = false) }
    }

    // --- CAMPOS DE TEXTO ---
    fun actualizarNumAnimales(valor: String) {
        _uiState.update { it.copy(numAnimales = valor) }
    }

    fun actualizarCodigoSIR(valor: String) {
        _uiState.update { it.copy(codigoSIR = valor) }
    }

    fun actualizarMatricula(valor: String) {
        _uiState.update { it.copy(matricula = valor) }
    }

    fun actualizarNifConductor(valor: String) {
        _uiState.update { it.copy(nifConductor = valor) }
    }

    // --- FECHA Y HORA DE SALIDA ---
    fun mostrarDatePickerSalida() { _uiState.update { it.copy(mostrarDatePickerSalida = true) } }
    fun ocultarDatePickerSalida() { _uiState.update { it.copy(mostrarDatePickerSalida = false) } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seleccionarFechaSalida(millis: Long) {
        val fechaFormat = formatLongToDate(millis)
        _uiState.update { it.copy(
            fechaSalida = fechaFormat,
            mostrarDatePickerSalida = false,
            mostrarTimePickerSalida = true
        )}
    }

    fun mostrarTimePickerSalida() { _uiState.update { it.copy(mostrarTimePickerSalida = true) } }
    fun ocultarTimePickerSalida() { _uiState.update { it.copy(mostrarTimePickerSalida = false) } }

    fun actualizarHoraSalida(h: String, m: String) {
        val horaFormat = "${h.padStart(2, '0')}:${m.padStart(2, '0')}"
        _uiState.update { it.copy(horaSalida = horaFormat) }
    }

    // --- FECHA Y HORA DE LLEGADA ---
    fun mostrarDatePickerLlegada() { _uiState.update { it.copy(mostrarDatePickerLlegada = true) } }
    fun ocultarDatePickerLlegada() { _uiState.update { it.copy(mostrarDatePickerLlegada = false) } }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seleccionarFechaLlegada(millis: Long) {
        val fechaFormat = formatLongToDate(millis)
        _uiState.update { it.copy(
            fechaLlegada = fechaFormat,
            mostrarDatePickerLlegada = false,
            mostrarTimePickerLlegada = true
        )}
    }

    fun mostrarTimePickerLlegada() { _uiState.update { it.copy(mostrarTimePickerLlegada = true) } }
    fun ocultarTimePickerLlegada() { _uiState.update { it.copy(mostrarTimePickerLlegada = false) } }

    fun actualizarHoraLlegada(h: String, m: String) {
        val horaFormat = "${h.padStart(2, '0')}:${m.padStart(2, '0')}"
        _uiState.update { it.copy(horaLlegada = horaFormat) }
    }

    // --- CARGA Y ENVÍO ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarDatosGuia(guia: GuiaGTRLista) {

        val utils = ElementosConCodigosPorcinos()

        val nombreCategoria = utils.categoriasB().filterValues { it == guia.categoria }.keys.firstOrNull()
            ?: "Desconocida" // Valor por defecto si el código no existe
        _uiState.update { currentState ->
            currentState.copy(
                remoActual = guia.remo, // Guardamos el REMO para la edición
                categoriaSeleccionada = nombreCategoria,
                categoriaCodigo = guia.categoria,
                numAnimales = guia.nombreAnimals.toString(),
                fechaSalida = formatLongToDate(guia.dataSortida),
                horaSalida = formatLongToTime(guia.dataSortida),
                fechaLlegada = formatLongToDate(guia.dataArribada),
                horaLlegada = formatLongToTime(guia.dataArribada),
                codigoSIR = guia.transportista ?: "",
                matricula = guia.vehicle ?: "",
                nifConductor = guia.responsable ?: ""
            )
        }
    }

    fun editarYConfirmarGuia(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value

            // 1. Log de los datos de entrada del formulario
            Log.d("EDITAR_GUIA", "Iniciando proceso para REMO: ${state.remoActual}")

            val fechaS = combinarFechaHora(state.fechaSalida, state.horaSalida)
            val fechaA = combinarFechaHora(state.fechaLlegada, state.horaLlegada)

            val request = ModificarMovimentsAGias(
                nif = "37370803N", // TODO: Obtener de userPreferences
                password = "5Q62h4rP",
                remo = state.remoActual,
                categoria = state.categoriaCodigo,
                nombreAnimals = state.numAnimales,
                transportista = state.codigoSIR,
                responsable = state.nifConductor,
                vehicle = state.matricula,
                dataSortida = fechaS,
                dataArribada = fechaA
            )

            // 2. Log del objeto que se va a enviar (Verifica formatos aquí)
            Log.d("EDITAR_GUIA", "Request enviada: $request")

            try {
                val response = repositorio.tramitarGuiaPorcina(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    // 3. Log de la respuesta bruta exitosa
                    Log.d("EDITAR_GUIA", "Respuesta exitosa del servidor: $body")

                    val resultado = body
                    if (resultado?.codi == "OK") {
                        Log.i("EDITAR_GUIA", "✅ Trámite completado correctamente")
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    } else {
                        // 4. Log de error de lógica de la API (ej: "Vehículo no válido")
                        Log.w("EDITAR_GUIA", "❌ API rechazó los datos: ${resultado?.descripcio}")
                        _uiState.update { it.copy(isLoading = false, error = resultado?.descripcio) }
                    }
                } else {
                    // 5. Log de error de protocolo (404, 500, 401)
                    val errorBody = response.errorBody()?.string()
                    Log.e("EDITAR_GUIA", "🔥 Error HTTP ${response.code()}: $errorBody")
                    _uiState.update { it.copy(isLoading = false, error = "Error servidor (${response.code()})") }
                }
            } catch (e: Exception) {
                // 6. Log de excepción crítica (Sin internet, Timeout, Crash de parsing)
                Log.e("EDITAR_GUIA", "💥 EXCEPCIÓN: ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    // --- UTILIDADES DE FORMATO ---
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
        val partesHora = hora.split(":")
        if (partesFecha.size < 3 || partesHora.size < 2) return ""
        return "${partesFecha[2]}${partesFecha[1]}${partesFecha[0]}${partesHora[0]}${partesHora[1]}"
    }
}