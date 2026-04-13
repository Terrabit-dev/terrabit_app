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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CrearGuiaPorcinosViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao,
    private val historialDao: HistorialDao,
    val historialCamposManager: HistorialCamposManager
) : BasePorcinosViewModel() {

    private var borradorSesionId = ""

    private val _uiState = MutableStateFlow(CrearGuiasPorcinosUiState())
    val uiState: StateFlow<CrearGuiasPorcinosUiState> = _uiState.asStateFlow()

    init {
        borradorSesionId = "guiaporcinos_auto_${System.currentTimeMillis()}"
    }

    // ─── Borrador ─────────────────────────────────────────────────────────────
    fun tieneContenido(): Boolean {
        val s = _uiState.value
        return s.explotacion.isNotEmpty() || s.categoriaSeleccionada.isNotEmpty() ||
                s.numAnimales.isNotEmpty() || s.fechaSalida.isNotEmpty() ||
                s.horaSalida.isNotEmpty()  || s.fechaLlegada.isNotEmpty() ||
                s.horaLlegada.isNotEmpty() || s.codigoSIR.isNotEmpty() ||
                s.medioTransporteSeleccionado.isNotEmpty() || s.matricula.isNotEmpty() ||
                s.nifConductor.isNotEmpty()
    }

    private fun serializarEstado(): Map<String, Any?> {
        val s = _uiState.value
        return mapOf(
            "explotacion"                   to s.explotacion,
            "categoriaSeleccionada"         to s.categoriaSeleccionada,
            "categoriaApiSeleccionada"      to s.categoriaApiSeleccionada,
            "numAnimales"                   to s.numAnimales,
            "fechaSalida"                   to s.fechaSalida,
            "horaSalida"                    to s.horaSalida,
            "fechaLlegada"                  to s.fechaLlegada,
            "horaLlegada"                   to s.horaLlegada,
            "codigoSIR"                     to s.codigoSIR,
            "medioTransporteSeleccionado"   to s.medioTransporteSeleccionado,
            "medioTransporteApiSeleccionado" to s.medioTransporteApiSeleccionado,
            "matricula"                     to s.matricula,
            "nifConductor"                  to s.nifConductor
        )
    }

    private fun restaurarEstado(datos: Map<String, Any?>) {
        _uiState.update {
            it.copy(
                explotacion                    = datos["explotacion"] as? String ?: "",
                categoriaSeleccionada          = datos["categoriaSeleccionada"] as? String ?: "",
                categoriaApiSeleccionada       = datos["categoriaApiSeleccionada"] as? String ?: "",
                numAnimales                    = datos["numAnimales"] as? String ?: "",
                fechaSalida                    = datos["fechaSalida"] as? String ?: "",
                horaSalida                     = datos["horaSalida"] as? String ?: "",
                fechaLlegada                   = datos["fechaLlegada"] as? String ?: "",
                horaLlegada                    = datos["horaLlegada"] as? String ?: "",
                codigoSIR                      = datos["codigoSIR"] as? String ?: "",
                medioTransporteSeleccionado    = datos["medioTransporteSeleccionado"] as? String ?: "",
                medioTransporteApiSeleccionado = datos["medioTransporteApiSeleccionado"] as? String ?: "",
                matricula                      = datos["matricula"] as? String ?: "",
                nifConductor                   = datos["nifConductor"] as? String ?: ""
            )
        }
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val json = Gson().toJson(serializarEstado())
                val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val ahora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(fecha = hoy, datos = json)
                    ?: BorradorEntity(id = borradorSesionId, tipo = "GUIA_PORCINOS",
                        fecha = hoy, hora = ahora, datos = json, estado = "BORRADOR_AUTO")
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error autoguardado: ${e.message}", e)
            }
        }
    }

    fun cargarBorradorPorId(id: String) {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll().find { it.id == id } ?: return@launch
                borradorSesionId = borrador.id
                restaurarEstado(Gson().fromJson(borrador.datos, object : TypeToken<Map<String, Any?>>() {}.type))
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error cargar borrador: ${e.message}", e)
            }
        }
    }

    fun eliminarBorradorAutomatico() {
        viewModelScope.launch {
            try {
                if (borradorSesionId.isNotEmpty()) {
                    borradorDao.deleteById(borradorSesionId); borradorSesionId = ""
                }
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error eliminar borrador: ${e.message}", e)
            }
        }
    }

    fun cargarDesdeHistorial(id: String) {
        viewModelScope.launch {
            try {
                val registro = historialDao.getAll().find { it.id == id } ?: return@launch
                restaurarEstado(Gson().fromJson(registro.datos, object : TypeToken<Map<String, Any?>>() {}.type))
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error cargar historial: ${e.message}", e)
            }
        }
    }

    suspend fun obtenerCantidadBorradoresPorcinos(): Int =
        borradorDao.getAll().count { it.tipo == "GUIA_PORCINOS" && it.estado == "BORRADOR_AUTO" }

    // ─── Formulario ───────────────────────────────────────────────────────────
    fun resetearEstado() { _uiState.update { it.copy(mensajeExito = null, mensajeError = null) } }

    fun actualizarExplotacion(valor: String) {
        if (valor.length <= 14) _uiState.update { it.copy(explotacion = valor) }
    }
    fun actualizarNumAnimales(valor: String) {
        if (valor.isEmpty()) { _uiState.update { it.copy(numAnimales = "") }; return }
        if (valor.isDigitsOnly() && (valor.toIntOrNull() ?: 0) >= 1)
            _uiState.update { it.copy(numAnimales = valor) }
    }
    fun actualizarCodigoSIR(valor: String) {
        if (valor.length <= 15) _uiState.update { it.copy(codigoSIR = valor) }
    }
    fun actualizarMatricula(valor: String) {
        if (valor.length <= 10) _uiState.update { it.copy(matricula = valor) }
    }
    fun actualizarNifConductor(valor: String) {
        if (valor.length <= 9) _uiState.update { it.copy(nifConductor = valor) }
    }

    // ─── Dropdowns ────────────────────────────────────────────────────────────
    fun toggleCategoriaExpandido()          { _uiState.update { it.copy(categoriaExpandido = !it.categoriaExpandido) } }
    fun cerrarCategoriaMenu()               { _uiState.update { it.copy(categoriaExpandido = false) } }
    fun toggleMedioTransporteExpandido()    { _uiState.update { it.copy(medioTransporteExpandido = !it.medioTransporteExpandido) } }
    fun cerrarMedioTransporteMenu()         { _uiState.update { it.copy(medioTransporteExpandido = false) } }

    fun seleccionarCategoria(nombre: String, codigo: String) {
        _uiState.update { it.copy(categoriaSeleccionada = nombre, categoriaApiSeleccionada = codigo, categoriaExpandido = false) }
    }
    fun seleccionarMedioTransporte(nombre: String, codigo: String) {
        _uiState.update { it.copy(medioTransporteSeleccionado = nombre, medioTransporteApiSeleccionado = codigo, medioTransporteExpandido = false) }
    }

    // ─── DatePicker / TimePicker salida ──────────────────────────────────────
    fun mostrarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = true) } }
    fun ocultarDatePickerSalida()  { _uiState.update { it.copy(mostrarDatePickerSalida = false) } }
    fun mostrarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = true) } }
    fun ocultarTimePickerSalida()  { _uiState.update { it.copy(mostrarTimePickerSalida = false) } }

    fun seleccionarFechaSalida(millis: Long) {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        _uiState.update {
            it.copy(
                fechaSalida = String.format("%02d/%02d/%04d",
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)),
                mostrarDatePickerSalida = false
            )
        }
    }
    fun actualizarHoraSalida(h: String, m: String) {
        _uiState.update { it.copy(horaSalida = String.format("%02d:%02d", h.toInt(), m.toInt())) }
    }

    // ─── DatePicker / TimePicker llegada ─────────────────────────────────────
    fun mostrarDatePickerLlegada() { _uiState.update { it.copy(mostrarDatePickerLlegada = true) } }
    fun ocultarDatePickerLlegada() { _uiState.update { it.copy(mostrarDatePickerLlegada = false) } }
    fun mostrarTimePickerLlegada() { _uiState.update { it.copy(mostrarTimePickerLlegada = true) } }
    fun ocultarTimePickerLlegada() { _uiState.update { it.copy(mostrarTimePickerLlegada = false) } }

    fun seleccionarFechaLlegada(millis: Long) {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        _uiState.update {
            it.copy(
                fechaLlegada = String.format("%02d/%02d/%04d",
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)),
                mostrarDatePickerLlegada = false
            )
        }
    }
    fun actualizarHoraLlegada(h: String, m: String) {
        _uiState.update { it.copy(horaLlegada = String.format("%02d:%02d", h.toInt(), m.toInt())) }
    }

    // ─── Envío ────────────────────────────────────────────────────────────────
    fun crearGuia() {
        val s = _uiState.value
        if (s.explotacion.isBlank() || s.numAnimales.isBlank() || s.fechaSalida.isBlank() ||
            s.horaSalida.isBlank() || s.fechaLlegada.isBlank() || s.horaLlegada.isBlank() ||
            s.categoriaSeleccionada.isBlank()) {
            Log.w("GTR_EDITAR", "Validación fallida — campos vacíos")
            _uiState.update { it.copy(mensajeError = "Todos los campos con * son obligatorios.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            try {
                val s = _uiState.value
                val request = AltaMovimientoGTR(
                    nif = nif, password = password,
                    tipusEspecie = "02", tipusAccio = "NO", tipusMoviment = "01",
                    explotacioSortida = codiMo, explotacioEntrada = s.explotacion,
                    codiCategoria = s.categoriaApiSeleccionada,
                    numAnimals = s.numAnimales.toIntOrNull() ?: 0,
                    dataSortida = convertirFechaHoraAFormatoAPI(s.fechaSalida, s.horaSalida),
                    dataArribada = convertirFechaHoraAFormatoAPI(s.fechaLlegada, s.horaLlegada),
                    codiSirentra = s.codigoSIR, mitjaTransport = s.medioTransporteApiSeleccionado,
                    matricula = s.matricula, nifConductor = s.nifConductor, mobilitat = "SI"
                )

                Log.d("GTR_CREAR", "━━━ CREAR GUÍA ━━━")
                Log.d("GTR_CREAR", "request: $request")

                val response = repositorio.altaGuiaPorcinas(request)

                Log.d("GTR_CREAR", "HTTP code: ${response.code()}")
                Log.d("GTR_CREAR", "isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("GTR_CREAR", "body: $body")
                    Log.d("GTR_CREAR", "body.descripcio: ${body?.descripcio}")

                    if (body?.descripcio?.firstOrNull() == "OK") {
                        val codigoGuia = body.descripcio.getOrNull(1)
                        Log.d("GTR_CREAR", "✅ Guía creada: $codigoGuia")
                        guardarEnHistorial("Guía porcinos enviada")
                        guardarHistorialCampos()
                        eliminarBorradorAutomatico()
                        _uiState.update { CrearGuiasPorcinosUiState(mensajeExito = "Guía creada: $codigoGuia") }
                    } else {
                        Log.w("GTR_CREAR", "⚠️ Respuesta inesperada: ${body?.descripcio}")
                        _uiState.update { it.copy(isLoading = false, mensajeError = "Respuesta inesperada del servidor") }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    Log.e("GTR_CREAR", "❌ Error HTTP ${response.code()}: $errorBody")
                    _uiState.update { it.copy(isLoading = false, mensajeError = "Error en la API: ${response.message()}") }
                }
            } catch (e: Exception) {
                Log.e("GTR_CREAR", "❌ Excepción: ${e.javaClass.simpleName}: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error de red: ${e.localizedMessage}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val ahora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                historialDao.insert(HistorialEntity(
                    id = UUID.randomUUID().toString(), tipo = "GUIA_PORCINOS",
                    fecha = hoy, hora = ahora,
                    datos = Gson().toJson(serializarEstado()), resumen = resumen
                ))
            } catch (e: Exception) {
                Log.e("CrearGuiaPorcinosVM", "Error historial: ${e.message}", e)
            }
        }
    }

    private fun guardarHistorialCampos() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.explotacion.isNotBlank())  historialCamposManager.guardarValor("porcinos_explotacion", s.explotacion)
            if (s.codigoSIR.isNotBlank())    historialCamposManager.guardarValor("porcinos_sir", s.codigoSIR)
            if (s.matricula.isNotBlank())    historialCamposManager.guardarValor("porcinos_matricula", s.matricula)
            if (s.nifConductor.isNotBlank()) historialCamposManager.guardarValor("porcinos_nif_conductor", s.nifConductor)
        }
    }
}