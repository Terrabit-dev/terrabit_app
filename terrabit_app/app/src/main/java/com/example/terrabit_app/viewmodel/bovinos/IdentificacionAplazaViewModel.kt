package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.utils.DateUtils
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao


@HiltViewModel
class IdentificacionAplazaViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao
) : BaseBovinoViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _identificadorAnimal = MutableLiveData("")
    val identificadorAnimal: LiveData<String> = _identificadorAnimal

    private val _fechaIdentificacion = MutableLiveData("")
    val fechaIdentificacion: LiveData<String> = _fechaIdentificacion

    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker: LiveData<Boolean> = _mostrarDatePicker

    init {
        borradorSesionId = "identificacion_aplazada_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "IDENTIFICACION_APLAZADA"

    override fun getDatosFormulario() = mapOf(
        "identificador" to _identificadorAnimal.value,
        "fechaIdentificacion" to _fechaIdentificacion.value
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        _identificadorAnimal.value = datos["identificador"] as? String ?: ""
        _fechaIdentificacion.value = datos["fechaIdentificacion"] as? String ?: ""
    }

    override fun limpiarFormulario() {
        _identificadorAnimal.value = ""
        _fechaIdentificacion.value = ""
        borradorSesionId = ""
    }

    override fun tieneContenido() =
        !_identificadorAnimal.value.isNullOrEmpty() ||
                !_fechaIdentificacion.value.isNullOrEmpty()

    // ─── Lógica específica ────────────────────────────────────────────────────
    fun onBovinoSelected(animal: Animal) {
        _identificadorAnimal.value = animal.identificador
        limpiarSugerencias()
    }

    fun actualizarIdentificador(nuevoId: String) { _identificadorAnimal.value = nuevoId }
    fun mostrarDatePicker() { _mostrarDatePicker.value = true }
    fun ocultarDatePicker() { _mostrarDatePicker.value = false }

    fun seleccionarFecha(fechaMillis: Long) {
        _fechaIdentificacion.value = fechaMillisAString(fechaMillis)
        _mostrarDatePicker.value = false
    }

    fun esFormularioValido() =
        !_identificadorAnimal.value.isNullOrEmpty() &&
                !_fechaIdentificacion.value.isNullOrEmpty()

    fun registrar() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = if (_identificadorAnimal.value.isNullOrEmpty()) 12 else 13
            return
        }
        launchApiCall {
            val request = PetIdentificacion(
                identificador = _identificadorAnimal.value ?: "",
                nif = nif, passwordMobilitat = password,
                dataIdentificacio = DateUtils.convertirFechaAFormatoAPI(_fechaIdentificacion.value ?: "")
            )
            val response = repositorio.putIdentificacionPendiente(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()?.let { it.codi == "0" || it.descripcio == "OK" } == true -> {
                        _operacionExitosa.value = true
                        _mensajeError.value = ""
                        guardarEnHistorial("Identificación aplazada registrada")
                        eliminarBorradorAutomatico()
                        limpiarFormulario()
                    }
                    !response.isSuccessful -> {
                        _mensajeError.value = parsearMensajeError(response)
                        _operacionExitosa.value = false
                    }
                    else -> {
                        _operacionExitosa.value = false
                        _mensajeError.value = "Error: Respuesta vacía del servidor"
                    }
                }
            }
        }
    }
}