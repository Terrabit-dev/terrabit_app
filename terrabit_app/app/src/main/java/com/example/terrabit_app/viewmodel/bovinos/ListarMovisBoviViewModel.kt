package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.movimientos.modelos.Moviment
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarMovisBoviViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val historialCamposManager: HistorialCamposManager
) : BaseListarViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _listaMovimientos = MutableLiveData<List<Moviment>>(emptyList())
    val listaMovimientos: LiveData<List<Moviment>> = _listaMovimientos

    private val _codiExplotacionDesti = MutableLiveData("")
    val codiExplotacionDesti: LiveData<String> = _codiExplotacionDesti

    private val _movimientoSeleccionado = MutableLiveData<Moviment?>(null)
    val movimientoSeleccionado: LiveData<Moviment?> = _movimientoSeleccionado

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun onResetearLista() { _listaMovimientos.value = emptyList() }

    override fun validarCampos(): String? = when {
        _codiExplotacionDesti.value.orEmpty().isBlank() -> "El código REGA es obligatorio."
        fechaDisplay.value.orEmpty().isBlank() -> "La fecha de salida es obligatoria."
        else -> null
    }

    override fun cargarDatos() {
        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val response = repositorio.getConfirmacionMovimientos(
                    nif                  = nif,
                    passwordMobilitat    = password,
                    explotacioDestinacio = _codiExplotacionDesti.value.orEmpty(),
                    dataSortida          = displayToApiFormat(fechaDisplay.value.orEmpty())
                )
                if (response.isSuccessful) {
                    _listaMovimientos.postValue(response.body()?.moviments ?: emptyList())
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
    fun onCodiChange(valor: String) { _codiExplotacionDesti.value = valor }
    fun seleccionarMovi(movimiento: Moviment) { _movimientoSeleccionado.value = movimiento }

    private suspend fun guardarHistorialCampos() {
        historialCamposManager.guardarValor("codi_rega", _codiExplotacionDesti.value ?: "")
    }
}