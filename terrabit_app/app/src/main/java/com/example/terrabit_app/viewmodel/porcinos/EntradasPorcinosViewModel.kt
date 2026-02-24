package com.example.terrabit_app.viewmodel.porcinos

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.ConfirmarMovimientosRequest
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class EntradasPorcinosViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EntradasPorcinosUiState())
    val uiState : StateFlow<EntradasPorcinosUiState> = _uiState.asStateFlow()

    private val repositorio = Repositorio()

    private lateinit var userPreferences: UserPreferences

    fun inicializarUserPreferences(context: Context) {
        userPreferences = UserPreferences(context)

        cargarGuiasPendientes()
    }

    private fun cargarGuiasPendientes() {
        val fechaFin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

        viewModelScope.launch {
            // 1. Iniciamos carga
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val response = repositorio.getPendientesConfirmarEntradaPorcina(
                    /*nif = userPreferences.getNif(),
                    password = userPreferences.getPassword(),
                    moDesti = userPreferences.getCodiMO(),*/
                    nif = "37370803N",
                    password = "5Q62h4rP",
                    moDesti = "1880AE",
                    desde = "000101010000",
                    fins = fechaFin
                )

                if (response.isSuccessful) {
                    val nuevasGuias = response.body()?.llistat ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        listaEntradasPorcinos = nuevasGuias,
                        isLoading = false // 2. Éxito: quitamos carga
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false) // Error de API
                    Log.e("EntradasPorcinosViewModel", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false) // Error de Red
                Log.e("EntradasPorcinosViewModel", "Error en la llamada", e)
            }
        }
    }

    fun confirmarGuia() {
        // Provisional
        TODO()
    }
}