package com.example.terrabit_app.viewmodel.bovinos

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


// Centralizar las funciones en comun de los Bovinos.

abstract class BaseBovinoViewModel : ViewModel() {

    // ─── Dependencias inyectadas por los hijos ────────────────────────────────
    protected abstract val repositorio: Repositorio
    protected abstract val userPreferences: UserPreferences
    protected abstract val borradorDao: BorradorDao
    protected abstract val historialDao: HistorialDao

    // ─── Credenciales (una sola vez para todos) ───────────────────────────────
    val nif get() = userPreferences.getNif() ?: ""
    val password get() = userPreferences.getPassword() ?: ""
    var codiMo get() = userPreferences.getCodiMO() ?: ""
        protected set(value) { /* no-op — se actualiza desde userPreferences */ }

    // ─── Estado de búsqueda de bovinos ────────────────────────────────────────
    private val _suggestionsBovinos = MutableLiveData<List<Animal>>(emptyList())
    val suggestionsBovinos: LiveData<List<Animal>> = _suggestionsBovinos

    private val _isLoadingBovinos = MutableLiveData(false)
    val isLoadingBovinos: LiveData<Boolean> = _isLoadingBovinos

    private val _bovinosCargados = MutableLiveData(false)
    val bovinosCargados: LiveData<Boolean> = _bovinosCargados

    // ─── Estado común de UI ───────────────────────────────────────────────────
    protected val _estadoCarga = MutableLiveData(false)
    val estadoCarga: LiveData<Boolean> = _estadoCarga

    protected val _operacionExitosa = MutableLiveData<Boolean>()
    val operacionExitosa: LiveData<Boolean> = _operacionExitosa

    protected val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> = _mensajeError

    protected val _codiError = MutableLiveData<Int?>()
    val codiError: LiveData<Int?> = _codiError

    // ─── Borrador ─────────────────────────────────────────────────────────────
    protected var borradorSesionId: String = ""

    // ─── Gestion de codigos MO ─────────────────────────────────────────────────────────────
    private val _codisMoExpandido = MutableLiveData(false)
    val codisMoExpandido = _codisMoExpandido

    private val _codiMoActivo = MutableLiveData<String?>(null)
    val codiMoActivo = _codiMoActivo

    private val _codisMoList = MutableLiveData<List<String>>(emptyList())

    // ─── Gestion de codigos MO ───────────────────────────────────────────────
    fun cargarCodisMos(){
        viewModelScope.launch {
            _codiMoActivo.value = userPreferences.getCodiMO()
            _codisMoList.value = userPreferences.getUserMOList()
        }
    }

    fun toggleCodisMoExpandido() {
        _codisMoExpandido.value = !(_codisMoExpandido.value ?: false)
    }

    fun cerrarCodisMo() {
        _codisMoExpandido.value = false
    }

    fun getCodisMos(): List<String> = _codisMoList.value ?: emptyList()

    fun seleccionarCodiMo(nuevoCodi: String) {
        viewModelScope.launch {
            val nif = userPreferences.getNif() ?: return@launch
            val password = userPreferences.getPassword() ?: return@launch
            val rememberMe = userPreferences.getRememberMe()
            userPreferences.saveCredentials(nif, password, nuevoCodi, rememberMe)
            _codiMoActivo.value = nuevoCodi
            _codisMoExpandido.value = false
            cargarBovinosEnCache()
        }

    }


    // ─── Contrato con los hijos ───────────────────────────────────────────────
    /** Tipo de registro: "NACIMIENTO", "MUERTE", etc. */
    protected abstract fun getTipoRegistro(): String

    /** Serializa el estado del formulario hijo a un Map */
    protected abstract fun getDatosFormulario(): Map<String, Any?>

    /** Restaura el estado del formulario hijo desde un Map */
    protected abstract fun restaurarDatos(datos: Map<String, Any?>)

    /** Limpia los campos específicos del formulario hijo */
    abstract fun limpiarFormulario()

    // ─── Carga de bovinos en caché ────────────────────────────────────────────
    protected fun cargarBovinosEnCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingBovinos.postValue(true)
                repositorio.getBovinosWithCache(
                    nif = nif,
                    password = password,
                    tipusVinculacio = "1",
                    explotacio = userPreferences.getCodiMO() ?: "",
                    forceRefresh = true
                )
                _bovinosCargados.postValue(true)
                _isLoadingBovinos.postValue(false)
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e(getTipoRegistro(), "Error al cargar bovinos: ${e.message}", e)
            }
        }
    }

    // ─── Búsqueda local ───────────────────────────────────────────────────────
    fun searchBovinos(query: String) {
        if (query.isBlank()) { _suggestionsBovinos.value = emptyList(); return }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultados = repositorio.searchBovinosLocal(query)
                _suggestionsBovinos.postValue(resultados)
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e(getTipoRegistro(), "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    fun limpiarSugerencias() { _suggestionsBovinos.value = emptyList() }

    // ─── Borrador ─────────────────────────────────────────────────────────────
    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datosJson = Gson().toJson(getDatosFormulario())
                val hoy = fechaHoy(); val ahora = horaActual()
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(fecha = hoy, datos = datosJson)
                    ?: BorradorEntity(
                        id = borradorSesionId, tipo = getTipoRegistro(),
                        fecha = hoy, hora = ahora,
                        datos = datosJson, estado = "BORRADOR_AUTO"
                    )
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("Autoguardado_${getTipoRegistro()}", "Error: ${e.message}", e)
            }
        }
    }

    fun cargarBorradorPorId(id: String) {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll().find { it.id == id } ?: return@launch
                borradorSesionId = borrador.id
                restaurarDatos(parsearDatos(borrador.datos))
            } catch (e: Exception) {
                Log.e(getTipoRegistro(), "Error al cargar borrador: ${e.message}", e)
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
                Log.e("EliminarBorrador_${getTipoRegistro()}", "Error: ${e.message}", e)
            }
        }
    }

    // ─── Historial ────────────────────────────────────────────────────────────
    protected fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                historialDao.insert(
                    HistorialEntity(
                        id = UUID.randomUUID().toString(),
                        tipo = getTipoRegistro(),
                        fecha = fechaHoy(), hora = horaActual(),
                        datos = Gson().toJson(getDatosFormulario()),
                        resumen = resumen
                    )
                )
            } catch (e: Exception) {
                Log.e("Historial_${getTipoRegistro()}", "Error: ${e.message}", e)
            }
        }
    }

    fun cargarDesdeHistorial(id: String) {
        viewModelScope.launch {
            try {
                val registro = historialDao.getAll().find { it.id == id } ?: return@launch
                restaurarDatos(parsearDatos(registro.datos))
            } catch (e: Exception) {
                Log.e(getTipoRegistro(), "Error al cargar desde historial: ${e.message}", e)
            }
        }
    }

    // ─── Helper: lanzador de llamadas API con manejo de errores unificado ─────
    /**
     * Ejecuta [bloque] dentro de viewModelScope con manejo centralizado de
     * SocketTimeoutException, IOException y Exception genérica.
     * Gestiona automáticamente _estadoCarga antes y después.
     */
    protected fun launchApiCall(bloque: suspend () -> Unit) {
        viewModelScope.launch {
            _estadoCarga.postValue(true)
            try {
                bloque()
            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _operacionExitosa.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _operacionExitosa.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _estadoCarga.value = false
                    _operacionExitosa.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    e.printStackTrace()
                }
            }
        }
    }

    // ─── Helper: parsear respuesta de error de API ────────────────────────────
    protected fun parsearMensajeError(response: retrofit2.Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
            errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
        } catch (e: Exception) {
            "Error al procesar respuesta"
        }
    }

    // ─── Utilidades de fecha ──────────────────────────────────────────────────
    @SuppressLint("DefaultLocale")
    protected fun fechaMillisAString(fechaMillis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = fechaMillis }
        return String.format(
            "%02d/%02d/%04d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }

    protected fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        return try {
            if (fecha.length == 10 && hora.length == 5) {
                val (dia, mes, anio) = fecha.split("/")
                val (horas, minutos) = hora.split(":")
                "$anio$mes$dia$horas$minutos"
            } else ""
        } catch (e: Exception) {
            Log.e("BaseBovinoVM", "Error conversión fecha/hora: ${e.message}"); ""
        }
    }

    fun fechaHoy(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    fun horaActual(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    // ─── Reset de estado común ────────────────────────────────────────────────
    fun resetearEstado() {
        _operacionExitosa.value = false
        _mensajeError.value = ""
        _codiError.value = null
    }

    // ─── Métodos que los hijos pueden necesitar sobreescribir ─────────────────
    open fun tieneContenido(): Boolean = false

    // ─── Utilidad interna ─────────────────────────────────────────────────────
    private fun parsearDatos(json: String): Map<String, Any?> =
        Gson().fromJson(json, object : TypeToken<Map<String, Any?>>() {}.type)
}