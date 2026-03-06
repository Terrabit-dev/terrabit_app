package com.example.terrabit_app.viewmodel.porcinos

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.DataClassPorcinos.AltaMovimientoGTR
import com.example.terrabit_app.ui.screen.porcinos.CrearGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class CrearGuiaPorcinosViewModel(application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(CrearGuiasPorcinosUiState())
    val uiState: StateFlow<CrearGuiasPorcinosUiState> = _uiState.asStateFlow()

    private val repositorio = Repositorio(application)
    private lateinit var userPreferences: UserPreferences

    fun inicializarUserPreferences(application: Context) {
        userPreferences = UserPreferences(application)
    }

    fun actualizarExplotacion(nuevaExplotacion: String) {
        if (nuevaExplotacion.length <= 14) {
            _uiState.update { currentState ->
                currentState.copy(explotacion = nuevaExplotacion)
            }
        }
    }

    fun toggleCategoriaExpandido() {
        _uiState.update { currentState ->
            currentState.copy(categoriaExpandido = !currentState.categoriaExpandido)
        }
    }

    fun cerrarCategoriaMenu() {
        _uiState.update { currentState ->
            currentState.copy(categoriaExpandido = false)
        }
    }

    fun seleccionarCategoria(categoria: String, codigo: String) {
        _uiState.update { currentState ->
            currentState.copy(
                categoriaSeleccionada = categoria,
                categoriaExpandido = false,
                categoriaApiSeleccionada = codigo
            )
        }
    }

    fun actualizarNumAnimales(nuevoNumAnimales: String) {
        if (nuevoNumAnimales.isEmpty()) {
            _uiState.update { it.copy(numAnimales = "") }
            return
        }

        if (nuevoNumAnimales.isDigitsOnly()) {
            val numero = nuevoNumAnimales.toIntOrNull() ?: 1

            if (numero >= 1) {
                _uiState.update { currentState ->
                    currentState.copy(numAnimales = nuevoNumAnimales)
                }
            }
        }
    }

    fun mostrarDatePickerSalida() {
        _uiState.update { currentState ->
            currentState.copy(mostrarDatePickerSalida = true)
        }
    }

    fun ocultarDatePickerSalida() {
        _uiState.update { currentState ->
            currentState.copy(mostrarDatePickerSalida = false)
        }
    }

    fun mostrarTimePickerSalida() {
        _uiState.update { currentState ->
            currentState.copy(mostrarTimePickerSalida = true)
        }
    }

    fun ocultarTimePickerSalida() {
        _uiState.update { currentState ->
            currentState.copy(mostrarTimePickerSalida = false)
        }
    }

    @SuppressLint("DefaultLocale")
    fun actualizarHoraSalida(hora: String, minutos: String) {
        _uiState.update { currentState ->
            currentState.copy(
                horaSalida = String.format(
                    "%02d:%02d",
                    hora.toInt(),
                    minutos.toInt()
                )
            )
        }
    }

    @SuppressLint("DefaultLocale")
    fun seleccionarFechaSalida(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _uiState.update { currentState ->
            currentState.copy(
                fechaSalida = String.format("%02d/%02d/%04d", dia, mes, anio),
                mostrarDatePickerSalida = false
            )
        }
    }

    fun mostrarDatePickerLlegada() {
        _uiState.update { currentState ->
            currentState.copy(mostrarDatePickerLlegada = true)
        }
    }

    fun ocultarDatePickerLlegada() {
        _uiState.update { currentState ->
            currentState.copy(mostrarDatePickerLlegada = false)
        }
    }

    fun mostrarTimePickerLlegada() {
        _uiState.update { currentState ->
            currentState.copy(mostrarTimePickerLlegada = true)
        }
    }

    fun ocultarTimePickerLlegada() {
        _uiState.update { currentState ->
            currentState.copy(mostrarTimePickerLlegada = false)
        }
    }

    @SuppressLint("DefaultLocale")
    fun actualizarHoraLlegada(hora: String, minutos: String) {
        _uiState.update { currentState ->
            currentState.copy(
                horaLlegada = String.format(
                    "%02d:%02d",
                    hora.toInt(),
                    minutos.toInt()
                )
            )
        }
    }

    fun seleccionarFechaLlegada(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _uiState.update { currentState ->
            currentState.copy(
                fechaLlegada = String.format("%02d/%02d/%04d", dia, mes, anio),
                mostrarDatePickerLlegada = false
            )
        }
    }

    fun actualizarCodigoSIR(nuevoCodigoSIR: String) {
        if (nuevoCodigoSIR.length <= 15) {
            _uiState.update { currentState ->
                currentState.copy(codigoSIR = nuevoCodigoSIR)
            }
        }
    }

    fun toggleMedioTransporteExpandido() {
        _uiState.update { currentState ->
            currentState.copy(medioTransporteExpandido = !currentState.medioTransporteExpandido)
        }
    }

    fun cerrarMedioTransporteMenu() {
        _uiState.update { currentState ->
            currentState.copy(medioTransporteExpandido = false)
        }
    }

    fun seleccionarMedioTransporte(medioTransporte: String, codigo: String) {
        _uiState.update { currentState ->
            currentState.copy(
                medioTransporteSeleccionado = medioTransporte,
                medioTransporteApiSeleccionado = codigo,
                medioTransporteExpandido = false
            )
        }
    }

    fun actualizarMatricula(nuevaMatricula: String) {
        if (nuevaMatricula.length <= 10) {
            _uiState.update { currentState ->
                currentState.copy(matricula = nuevaMatricula)
            }
        }
    }

    fun actualizarNifConductor(nif: String) {
        if (nif.length <= 9) {
            _uiState.update { currentState ->
                currentState.copy(nifConductor = nif)
            }
        }
    }

    fun crearGuia() {
        val TAG = "GTR_API_DEBUG" // Etiqueta para filtrar en Logcat

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }

            try {
                // 1. Obtenemos las credenciales del administrador logueado actualmente
//                val nifAdmin = userPreferences.getNif() ?: ""
//                val passwordAdmin = userPreferences.getPassword() ?: ""
//                val codiMoOrigen = userPreferences.getCodiMO() ?: ""

                // 1. Credenciales (Hardcoded para test según tu código)
                val nifAdmin = "37370803N"
                val passwordAdmin = "5Q62h4rP"
                val codiMoOrigen = "1880AE"

                val state = _uiState.value

                // 2. Construcción del objeto
                val request = AltaMovimientoGTR(
                    nif = nifAdmin,
                    password = passwordAdmin,
                    tipusEspecie = "02",
                    tipusAccio = "NO",
                    tipusMoviment = "01",
                    explotacioSortida = codiMoOrigen,
                    explotacioEntrada = state.explotacion,
                    codiCategoria = state.categoriaApiSeleccionada,
                    numAnimals = state.numAnimales.toIntOrNull() ?: 0,
                    dataSortida = convertirFechaHoraAFormatoAPI(state.fechaSalida, state.horaSalida),
                    dataArribada = convertirFechaHoraAFormatoAPI(state.fechaLlegada, state.horaLlegada),
                    codiSirentra = state.codigoSIR,
                    mitjaTransport = state.medioTransporteApiSeleccionado,
                    matricula = state.matricula,
                    nifConductor = state.nifConductor,
                    mobilitat = "SI"
                )

                // --- LOG DE ENVÍO ---
                Log.d(TAG, "Enviando petición PUT a la API...")
                Log.d(TAG, "Cuerpo del JSON: $request")

                // 3. Llamada al repositorio
                val response = repositorio.altaGuiaPorcinas(request)

                // --- LOG DE RESPUESTA ---
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.i(TAG, "Respuesta exitosa (200 OK)")
                    Log.i(TAG, "Cuerpo de respuesta: ${body?.descripcio}")

                    if (body?.descripcio?.firstOrNull() == "OK") {
                        val codigoGuia = body.descripcio.getOrNull(1)
                        Log.d(TAG, "Guía generada con éxito. ID: $codigoGuia")

                        _uiState.update {
                            CrearGuiasPorcinosUiState(mensajeExito = "Guía creada: $codigoGuia")
                        }
                    } else {
                        Log.w(TAG, "La API respondió OK pero el contenido es inesperado")
                    }
                } else {
                    // Log de error de la API (4xx o 5xx)
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error en la API: Código ${response.code()}")
                    Log.e(TAG, "Mensaje de error: ${response.message()}")
                    Log.e(TAG, "Cuerpo del error: $errorBody")

                    _uiState.update { it.copy(mensajeError = "Error en la API: ${response.message()}") }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Excepción durante la llamada: ${e.message}", e)
                _uiState.update { it.copy(mensajeError = "Error de red: ${e.localizedMessage}") }
            } finally {
                Log.d(TAG, "Operación finalizada (loading = false)")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        // Tu lógica actual: DD/MM/YYYY + HH:mm -> YYYYMMDDHHMM
        // Asegúrate de que los ceros a la izquierda estén presentes
        val partesFecha = fecha.split("/")
        val partesHora = hora.split(":")
        return if (partesFecha.size == 3 && partesHora.size == 2) {
            "${partesFecha[2]}${partesFecha[1]}${partesFecha[0]}${partesHora[0]}${partesHora[1]}"
        } else ""
    }
}