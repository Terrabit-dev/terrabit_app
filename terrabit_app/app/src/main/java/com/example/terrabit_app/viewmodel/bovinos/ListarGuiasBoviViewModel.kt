package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guias.Guia
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarGuiasBoviViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    // ─── Estado observable ───────────────────────────────────────────────────
    private val _listaGuias = MutableLiveData<List<Guia>>(emptyList())
    val listaGuias: LiveData<List<Guia>> = _listaGuias

    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _consultaIniciada = MutableLiveData(false)
    val consultaIniciada: LiveData<Boolean> = _consultaIniciada

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // ─── Campos del formulario ───────────────────────────────────────────────
    private val _codiRega = MutableLiveData("")
    val codiRega: LiveData<String> = _codiRega

    private val _dataSortida = MutableLiveData("")
    val dataSortida: LiveData<String> = _dataSortida

    // ─── Credenciales ────────────────────────────────────────────────────────
    private val nif      = userPreferences.getNif()      ?: ""
    private val password = userPreferences.getPassword() ?: ""
    private val codiMo   = userPreferences.getCodiMO()   ?: ""

    // ─── Actualización de campos ─────────────────────────────────────────────
    fun onRegaChange(valor: String)  { _codiRega.value    = valor }
    fun onFechaChange(valor: String) { _dataSortida.value = valor }

    // ─── Lógica de negocio ───────────────────────────────────────────────────

    private val _guiaSeleccionada = MutableLiveData<Guia?>(null)
    val guiaSeleccionada: LiveData<Guia?> = _guiaSeleccionada

    /** Valida los campos antes de lanzar la petición. */

    fun seleccionarGuia(guia: Guia) {
        _guiaSeleccionada.value = guia
    }
    fun validarPeticion() {
        val rega  = _codiRega.value.orEmpty().trim()
        val fecha = _dataSortida.value.orEmpty().trim()

        if (rega.isBlank() || fecha.isBlank()) {
            _error.value = "El código REGA y la fecha de salida son obligatorios."
            return
        }

        _error.value = null
        _consultaIniciada.value = true
        cargarGuias()
    }

    /** Llama al repositorio y actualiza el estado con el resultado. */
    fun cargarGuias() {
        viewModelScope.launch {
            _cargando.postValue(true)

            try {
                val response = repositorio.getDescargaGuiasMobilitat(
                    nif = nif,
                    passwordMobilitat = password,
                    codiMo = codiMo,
                    codiRega = _codiRega.value.orEmpty(),
                    dataSortida = _dataSortida.value.orEmpty()
                )

                if (response.isSuccessful) {
                    val guias = response.body()?.guies ?: emptyList()
                    Log.d("BOVI_VM", "Guías recibidas: ${guias.size}")
                    _listaGuias.postValue(guias)
                    _cargando.postValue(false)
                } else {
                    val msg = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("BOVI_VM", "HTTP ${response.code()}: $msg")
                    _error.postValue("Error ${response.code()}: $msg")
                    _cargando.postValue(false)
                    _consultaIniciada.postValue(false)
                }

            } catch (e: Exception) {
                Log.e("BOVI_VM", "Excepción: ${e.message}")
                _error.postValue("Error de conexión: ${e.localizedMessage}")
                _cargando.postValue(false)
                _consultaIniciada.postValue(false)
            }
        }
    }

    /** Vuelve al formulario y limpia el estado. */
    fun resetearConsulta() {
        _consultaIniciada.value = false
        _listaGuias.value       = emptyList()
        _error.value            = null
    }
}