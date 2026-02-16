package com.example.terrabit_app.viewmodel.porcinos

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guiasPorcinos.CrearGuiaMobilitatPorcinos
import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos
import com.example.terrabit_app.ui.screen.porcinos.CrearGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class CrearGuiaPorcinosViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(CrearGuiasPorcinosUiState())
    val uiState: StateFlow<CrearGuiasPorcinosUiState> = _uiState.asStateFlow()

    private val repositorio = Repositorio()

    private lateinit var userPreferences: UserPreferences

    private var categoriaApiSeleccionada: String = "0"
    private var medioTransporteApiSeleccionada: String = "0"

    fun inicializarUserPreferences(context: Context) {
        userPreferences = UserPreferences(context)
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

    suspend fun crearGuia() {
        val nif: String? = userPreferences.getNif()
        val password: String? = userPreferences.getPassword()
        val codiMoOrign: String? = userPreferences.getCodiMO()

        val currentUiState = _uiState.value

        val fechaSalida = convertirFechaHoraAFormatoAPI(
            currentUiState.fechaSalida,
            currentUiState.horaSalida
        ).toLong()
        val fechaLlegada = convertirFechaHoraAFormatoAPI(
            currentUiState.fechaLlegada,
            currentUiState.horaLlegada
        ).toLong()

        val guia = CrearGuiaMobilitatPorcinos(
            nif = nif,
            password = password,
            moOrigen = codiMoOrign,
            moDesti = currentUiState.explotacion,
            categoria = currentUiState.categoriaApiSeleccionada,
            nombreAnimals = currentUiState.numAnimales.toInt(),
            dataSortida = fechaSalida,
            dataArribada = fechaLlegada,
            codiSir = currentUiState.codigoSIR,
            medioTransporte = currentUiState.medioTransporteApiSeleccionado,
            matricula = currentUiState.matricula,
            nifConductor = currentUiState.nifConductor
        )

        repositorio.putMovilidadPorcinos(guia)

        _uiState.update { CrearGuiasPorcinosUiState() }
        /*TODO*/
    }

    private fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        val partesFecha = fecha.split("/")
        val fechaLimpia = if (partesFecha.size == 3) {
            "${partesFecha[2]}${partesFecha[1]}${partesFecha[0]}"
        } else {
            "00000000" // Valor por defecto en caso de error
        }
        
        val horaLimpia = hora.replace(":", "")

        return fechaLimpia + horaLimpia
    }

    fun rellenarCampos(guia: GuiaMobilitatPorcinos) {
        val fechaSalida = guia.dataSortida.toString().let {
            "${it.substring(6, 8)}/${it.substring(4, 6)}/${it.substring(0, 4)}"
        }
        val fechaLlegada = guia.dataArribada.toString().let {
            "${it.substring(6, 8)}/${it.substring(4, 6)}/${it.substring(0, 4)}"
        }
        val horaSalida = guia.dataSortida.toString().let {
            "${it.substring(8, 10)}:${it.substring(10, 12)}"
        }
        val horaLlegada = guia.dataArribada.toString().let {
            "${it.substring(8, 10)}:${it.substring(10, 12)}"
        }

        _uiState.update { currentState ->
            currentState.copy(
                explotacion = guia.moDesti,
                categoriaSeleccionada = guia.categoria,
                numAnimales = guia.nombreAnimals.toString(),
                fechaSalida = fechaSalida,
                fechaLlegada = fechaLlegada,
                horaSalida = horaSalida,
                horaLlegada = horaLlegada,
                matricula= guia.vehicle.toString(),
                nifConductor = guia.responsable.toString()
            )
        }
    }

    fun editarYConfirmarGuia() {
        TODO()
    }
}