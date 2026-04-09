package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao

@HiltViewModel
class CorrecionSexoViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao
) : BaseBovinoViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _identificadorAnimal = MutableLiveData("")
    val identificadorCorreccionSexo: LiveData<String> = _identificadorAnimal

    private val _sexoSeleccionado = MutableLiveData(0)
    val sexoCorreccionSeleccionado: LiveData<Int> = _sexoSeleccionado

    private val _sexoExpandido = MutableLiveData(false)
    val sexoCorreccionExpandido: LiveData<Boolean> = _sexoExpandido

    private var codigoSexo = ""

    init {
        borradorSesionId = "correccion_sexo_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "CORRECCION_SEXO"

    override fun getDatosFormulario() = mapOf(
        "identificador"  to _identificadorAnimal.value,
        "sexoSeleccionado" to _sexoSeleccionado.value,
        "codigoSexo"     to codigoSexo
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        _identificadorAnimal.value = datos["identificador"] as? String ?: ""
        _sexoSeleccionado.value    = (datos["sexoSeleccionado"] as? Double)?.toInt() ?: 0
        codigoSexo                 = datos["codigoSexo"] as? String ?: ""
    }

    override fun limpiarFormulario() {
        _identificadorAnimal.value = ""
        _sexoSeleccionado.value    = 0
        codigoSexo                 = ""
        borradorSesionId           = ""
    }

    override fun tieneContenido() =
        !_identificadorAnimal.value.isNullOrEmpty() ||
                (_sexoSeleccionado.value ?: 0) != 0

    // ─── Selección de bovino ──────────────────────────────────────────────────
    fun onBovinoSelected(animal: Animal) {
        _identificadorAnimal.value = animal.identificador
        limpiarSugerencias()
    }

    // ─── Actualizadores de campos ─────────────────────────────────────────────
    fun actualizarIdentificador(nuevoId: String) { _identificadorAnimal.value = nuevoId }

    // ─── Dropdown sexo ────────────────────────────────────────────────────────
    fun seleccionarSexoCorreccion(sexoId: Int, codigo: String) {
        _sexoSeleccionado.value = sexoId
        codigoSexo = codigo
        _sexoExpandido.value = false
    }

    fun toggleSexoExpandido() { _sexoExpandido.value = !(_sexoExpandido.value ?: false) }
    fun cerrarSexoMenu() { _sexoExpandido.value = false }

    // ─── Carga borrador más reciente (específico de este VM) ──────────────────
    fun cargarBorradorExistente() {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll()
                    .filter { it.tipo == getTipoRegistro() && it.estado == "BORRADOR_AUTO" }
                    .maxByOrNull { it.id.substringAfter("correccion_sexo_auto_").toLongOrNull() ?: 0L }
                    ?: return@launch
                cargarBorradorPorId(borrador.id)
            } catch (e: Exception) {
                Log.e(getTipoRegistro(), "Error al cargar borrador existente: ${e.message}", e)
            }
        }
    }

    // ─── Validación ───────────────────────────────────────────────────────────
    fun esFormularioValido() =
        !_identificadorAnimal.value.isNullOrEmpty() &&
                (_sexoSeleccionado.value ?: 0) != 0

    // ─── Registro principal ───────────────────────────────────────────────────
    fun corregirSexoAnimal() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = when {
                _identificadorAnimal.value.isNullOrEmpty()  -> 12
                (_sexoSeleccionado.value ?: 0) == 0         -> 4
                else                                        -> 0
            }
            return
        }
        launchApiCall {
            val request = PetModicarAnimal(
                identificador     = _identificadorAnimal.value ?: "",
                nif               = nif,
                passwordMobilitat = password,
                sexe              = codigoSexo
            )
            val response = repositorio.putMoficarAnimal(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()
                        ?.let { it.codi == "0" || it.descripcio == "OK" } == true -> {
                        _operacionExitosa.value = true
                        _mensajeError.value = ""
                        guardarEnHistorial("Corrección de sexo registrada")
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
    fun precargarAnimal(id: String) {
        _identificadorAnimal.value = id
    }
}