package com.example.terrabit_app.viewmodel.bovinos


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guias.Guia
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarGuiasBoviViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val historialCamposManager: HistorialCamposManager
) : BaseListarViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _listaGuias = MutableLiveData<List<Guia>>(emptyList())
    val listaGuias: LiveData<List<Guia>> = _listaGuias

    private val _codiRega = MutableLiveData("")
    val codiRega: LiveData<String> = _codiRega

    private val _guiaSeleccionada = MutableLiveData<Guia?>(null)
    val guiaSeleccionada: LiveData<Guia?> = _guiaSeleccionada

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun onResetearLista() { _listaGuias.value = emptyList() }

    override fun validarCampos(): String? = when {
        _codiRega.value.orEmpty().isBlank() -> "El código REGA es obligatorio."
        fechaDisplay.value.orEmpty().isBlank() -> "La fecha de salida es obligatoria."
        else -> null
    }

    override fun cargarDatos() {
        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val response = repositorio.getDescargaGuiasMobilitat(
                    nif               = nif,
                    passwordMobilitat = password,
                    codiMo            = codiMo,
                    codiRega          = _codiRega.value.orEmpty(),
                    dataSortida       = displayToApiFormat(fechaDisplay.value.orEmpty())
                )
                if (response.isSuccessful) {
                    _listaGuias.postValue(response.body()?.guies ?: emptyList())
                    _cargando.postValue(false)
                    guardarHistorialCampos()
                } else {
                    _error.postValue(extraerDescripcion(response.errorBody()?.string() ?: "", response.code()))
                    _cargando.postValue(false)
                    _consultaIniciada.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error de conexión: ${e.localizedMessage}")
                _cargando.postValue(false)
                _consultaIniciada.postValue(false)
            }
        }
    }

    // ─── Acciones específicas ─────────────────────────────────────────────────
    fun onRegaChange(valor: String) { _codiRega.value = valor }
    fun seleccionarGuia(guia: Guia) { _guiaSeleccionada.value = guia }

    private suspend fun guardarHistorialCampos() {
        historialCamposManager.guardarValor("codi_rega", _codiRega.value ?: "")
    }
}