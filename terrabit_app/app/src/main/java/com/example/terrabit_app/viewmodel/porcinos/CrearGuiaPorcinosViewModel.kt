package com.example.terrabit_app.viewmodel.porcinos

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.network.DataClassPorcinos.AltaMovimientoGTR
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.CrearGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CrearGuiaPorcinosViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao,
    private val historialDao: HistorialDao,
    val historialCamposManager: HistorialCamposManager
) : ViewModel() {

    private var borradorSesionId: String = ""

    private val _uiState = MutableStateFlow(CrearGuiasPorcinosUiState())
    val uiState: StateFlow<CrearGuiasPorcinosUiState> = _uiState.asStateFlow()

    init {
        borradorSesionId = "guiaporcinos_auto_${System.currentTimeMillis()}"
    }

    suspend fun obtenerCantidadBorradoresPorcinos(): Int {
        return borradorDao.getAll().count { it.tipo == "GUIA_PORCINOS" && it.estado == "BORRADOR_AUTO" }
    }

    fun tieneContenido(): Boolean {
        val s = _uiState.value
        return s.explotacion.isNotEmpty() || s.categoriaSeleccionada.isNotEmpty() ||
                s.numAnimales.isNotEmpty() || s.fechaSalida.isNotEmpty() ||
                s.horaSalida.isNotEmpty() || s.fechaLlegada.isNotEmpty() ||
                s.horaLlegada.isNotEmpty() || s.codigoSIR.isNotEmpty() ||
                s.medioTransporteSeleccionado.isNotEmpty() || s.matricula.isNotEmpty() ||
                s.nifConductor.isNotEmpty()
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val s = _uiState.value
                val datos = mapOf(
                    "explotacion" to s.explotacion,
                    "categoriaSeleccionada" to s.categoriaSeleccionada,
                    "categoriaApiSeleccionada" to s.categoriaApiSeleccionada,
                    "numAnimales" to s.numAnimales,
                    "fechaSalida" to s.fechaSalida,
                    "horaSalida" to s.horaSalida,
                    "fechaLlegada" to s.fechaLlegada,
                    "horaLlegada" to s.horaLlegada,
                    "codigoSIR" to s.codigoSIR,
                    "medioTransporteSeleccionado" to s.medioTransporteSeleccionado,
                    "medioTransporteApiSeleccionado" to s.medioTransporteApiSeleccionado,
                    "matricula" to s.matricula,
                    "nifConductor" to s.nifConductor
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId,
                    tipo = "GUIA_PORCINOS",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos),
                    estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("AutoguardadoPorcinos", "Error: ${e.message}", e)
            }
        }
    }

    fun cargarBorradorPorId(id: String) {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll().find { it.id == id } ?: return@launch
                borradorSesionId = borrador.id
                val datos: Map<String, Any?> = Gson().fromJson(borrador.datos, object : TypeToken<Map<String, Any?>>() {}.type)
                _uiState.update {
                    it.copy(
                        explotacion = datos["explotacion"] as? String ?: "",
                        categoriaSeleccionada = datos["categoriaSeleccionada"] as? String ?: "",
                        categoriaApiSeleccionada = datos["categoriaApiSeleccionada"] as? String ?: "",
                        numAnimales = datos["numAnimales"] as? String ?: "",
                        fechaSalida = datos["fechaSalida"] as? String ?: "",
                        horaSalida = datos["horaSalida"] as? String ?: "",
                        fechaLlegada = datos["fechaLlegada"] as? String ?: "",
                        horaLlegada = datos["horaLlegada"] as? String ?: "",
                        codigoSIR = datos["codigoSIR"] as? String ?: "",
                        medioTransporteSeleccionado = datos["medioTransporteSeleccionado"] as? String ?: "",
                        medioTransporteApiSeleccionado = datos["medioTransporteApiSeleccionado"] as? String ?: "",
                        matricula = datos["matricula"] as? String ?: "",
                        nifConductor = datos["nifConductor"] as? String ?: ""
                    )
                }
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error al cargar borrador: ${e.message}", e)
            }
        }
    }

    fun eliminarBorradorAutomatico() {
        viewModelScope.launch {
            try {
                if (borradorSesionId.isNotEmpty()) {
                    borradorDao.deleteById(borradorSesionId)
                    borradorSesionId = ""
                }
            } catch (e: Exception) {
                Log.e("EliminarBorradorPorcinos", "Error: ${e.message}", e)
            }
        }
    }

    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                val s = _uiState.value
                val datos = mapOf(
                    "explotacion" to s.explotacion,
                    "categoriaSeleccionada" to s.categoriaSeleccionada,
                    "categoriaApiSeleccionada" to s.categoriaApiSeleccionada,
                    "numAnimales" to s.numAnimales,
                    "fechaSalida" to s.fechaSalida,
                    "horaSalida" to s.horaSalida,
                    "fechaLlegada" to s.fechaLlegada,
                    "horaLlegada" to s.horaLlegada,
                    "codigoSIR" to s.codigoSIR,
                    "medioTransporteSeleccionado" to s.medioTransporteSeleccionado,
                    "medioTransporteApiSeleccionado" to s.medioTransporteApiSeleccionado,
                    "matricula" to s.matricula,
                    "nifConductor" to s.nifConductor
                )
                historialDao.insert(HistorialEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    tipo = "GUIA_PORCINOS",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos),
                    resumen = resumen
                ))
            } catch (e: Exception) {
                Log.e("HistorialPorcinos", "Error: ${e.message}", e)
            }
        }
    }

    private fun guardarHistorialCampos() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.explotacion.isNotBlank()) historialCamposManager.guardarValor("porcinos_explotacion", s.explotacion)
            if (s.codigoSIR.isNotBlank())   historialCamposManager.guardarValor("porcinos_sir", s.codigoSIR)
            if (s.matricula.isNotBlank())    historialCamposManager.guardarValor("porcinos_matricula", s.matricula)
            if (s.nifConductor.isNotBlank()) historialCamposManager.guardarValor("porcinos_nif_conductor", s.nifConductor)
        }
    }

    fun cargarDesdeHistorial(id: String) {
        viewModelScope.launch {
            try {
                val registro = historialDao.getAll().find { it.id == id } ?: return@launch
                val datos: Map<String, Any?> = Gson().fromJson(registro.datos, object : TypeToken<Map<String, Any?>>() {}.type)
                _uiState.update {
                    it.copy(
                        explotacion = datos["explotacion"] as? String ?: "",
                        categoriaSeleccionada = datos["categoriaSeleccionada"] as? String ?: "",
                        categoriaApiSeleccionada = datos["categoriaApiSeleccionada"] as? String ?: "",
                        numAnimales = datos["numAnimales"] as? String ?: "",
                        fechaSalida = datos["fechaSalida"] as? String ?: "",
                        horaSalida = datos["horaSalida"] as? String ?: "",
                        fechaLlegada = datos["fechaLlegada"] as? String ?: "",
                        horaLlegada = datos["horaLlegada"] as? String ?: "",
                        codigoSIR = datos["codigoSIR"] as? String ?: "",
                        medioTransporteSeleccionado = datos["medioTransporteSeleccionado"] as? String ?: "",
                        medioTransporteApiSeleccionado = datos["medioTransporteApiSeleccionado"] as? String ?: "",
                        matricula = datos["matricula"] as? String ?: "",
                        nifConductor = datos["nifConductor"] as? String ?: ""
                    )
                }
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error al cargar historial: ${e.message}", e)
            }
        }
    }

    fun resetearEstado() {
        _uiState.update { it.copy(mensajeExito = null, mensajeError = null) }
    }

    fun actualizarExplotacion(nuevaExplotacion: String) {
        if (nuevaExplotacion.length <= 14) {
            _uiState.update { it.copy(explotacion = nuevaExplotacion) }
        }
    }

    fun toggleCategoriaExpandido() {
        _uiState.update { it.copy(categoriaExpandido = !it.categoriaExpandido) }
    }

    fun cerrarCategoriaMenu() {
        _uiState.update { it.copy(categoriaExpandido = false) }
    }

    fun seleccionarCategoria(categoria: String, codigo: String) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria, categoriaExpandido = false, categoriaApiSeleccionada = codigo) }
    }

    fun actualizarNumAnimales(nuevoNumAnimales: String) {
        if (nuevoNumAnimales.isEmpty()) { _uiState.update { it.copy(numAnimales = "") }; return }
        if (nuevoNumAnimales.isDigitsOnly()) {
            val numero = nuevoNumAnimales.toIntOrNull() ?: 1
            if (numero >= 1) _uiState.update { it.copy(numAnimales = nuevoNumAnimales) }
        }
    }

    fun mostrarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = true) } }
    fun ocultarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = false) } }
    fun mostrarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = true) } }
    fun ocultarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = false) } }

    @SuppressLint("DefaultLocale")
    fun actualizarHoraSalida(hora: String, minutos: String) {
        _uiState.update { it.copy(horaSalida = String.format("%02d:%02d", hora.toInt(), minutos.toInt())) }
    }

    @SuppressLint("DefaultLocale")
    fun seleccionarFechaSalida(fechaMillis: Long) {
        val calendar = Calendar.getInstance().apply { timeInMillis = fechaMillis }
        _uiState.update {
            it.copy(
                fechaSalida = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)),
                mostrarDatePickerSalida = false
            )
        }
    }

    fun mostrarDatePickerLlegada()  { _uiState.update { it.copy(mostrarDatePickerLlegada = true) } }
    fun ocultarDatePickerLlegada()  { _uiState.update { it.copy(mostrarDatePickerLlegada = false) } }
    fun mostrarTimePickerLlegada()  { _uiState.update { it.copy(mostrarTimePickerLlegada = true) } }
    fun ocultarTimePickerLlegada()  { _uiState.update { it.copy(mostrarTimePickerLlegada = false) } }

    @SuppressLint("DefaultLocale")
    fun actualizarHoraLlegada(hora: String, minutos: String) {
        _uiState.update { it.copy(horaLlegada = String.format("%02d:%02d", hora.toInt(), minutos.toInt())) }
    }

    fun seleccionarFechaLlegada(fechaMillis: Long) {
        val calendar = Calendar.getInstance().apply { timeInMillis = fechaMillis }
        _uiState.update {
            it.copy(
                fechaLlegada = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)),
                mostrarDatePickerLlegada = false
            )
        }
    }

    fun actualizarCodigoSIR(nuevoCodigoSIR: String) {
        if (nuevoCodigoSIR.length <= 15) _uiState.update { it.copy(codigoSIR = nuevoCodigoSIR) }
    }

    fun toggleMedioTransporteExpandido() {
        _uiState.update { it.copy(medioTransporteExpandido = !it.medioTransporteExpandido) }
    }

    fun cerrarMedioTransporteMenu() {
        _uiState.update { it.copy(medioTransporteExpandido = false) }
    }

    fun seleccionarMedioTransporte(medioTransporte: String, codigo: String) {
        _uiState.update { it.copy(medioTransporteSeleccionado = medioTransporte, medioTransporteApiSeleccionado = codigo, medioTransporteExpandido = false) }
    }

    fun actualizarMatricula(nuevaMatricula: String) {
        if (nuevaMatricula.length <= 10) _uiState.update { it.copy(matricula = nuevaMatricula) }
    }

    fun actualizarNifConductor(nif: String) {
        if (nif.length <= 9) _uiState.update { it.copy(nifConductor = nif) }
    }

    fun crearGuia() {
        val TAG = "GTR_API_DEBUG"
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            try {
                val nifAdmin = userPreferences.getNif() ?: ""
                val passwordAdmin = userPreferences.getPassword() ?: ""
                val codiMoOrigen = userPreferences.getCodiMO() ?: ""
                val state = _uiState.value

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

                Log.d(TAG, "Enviando petición PUT a la API: $request")
                val response = repositorio.altaGuiaPorcinas(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.i(TAG, "Respuesta exitosa: ${body?.descripcio}")
                    if (body?.descripcio?.firstOrNull() == "OK") {
                        val codigoGuia = body.descripcio.getOrNull(1)
                        Log.d(TAG, "Guía generada: $codigoGuia")
                        guardarEnHistorial("Guía porcinos enviada")
                        guardarHistorialCampos()
                        eliminarBorradorAutomatico()
                        _uiState.update { CrearGuiasPorcinosUiState(mensajeExito = "Guía creada: $codigoGuia") }
                    } else {
                        Log.w(TAG, "Respuesta inesperada de la API")
                        _uiState.update { it.copy(isLoading = false, mensajeError = "Respuesta inesperada del servidor") }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error API ${response.code()}: $errorBody")
                    _uiState.update { it.copy(isLoading = false, mensajeError = "Error en la API: ${response.message()}") }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error de red: ${e.localizedMessage}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        val partesFecha = fecha.split("/")
        val partesHora = hora.split(":")
        return if (partesFecha.size == 3 && partesHora.size == 2) {
            "${partesFecha[2]}${partesFecha[1]}${partesFecha[0]}${partesHora[0]}${partesHora[1]}"
        } else ""
    }
}