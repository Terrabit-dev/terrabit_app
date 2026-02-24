package com.example.terrabit_app.viewmodel.porcinos

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.ConfirmarMovimientosRequest
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun confirmarEntrada(guia: MovimentPteDetail) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Mapeo desde MovimentPteDetail a ConfirmarMovimientosRequest
            val request = ConfirmarMovimientosRequest(
                nif = "37370803N", // TODO: userPreferences.getNif()
                password = "5Q62h4rP", // TODO: userPreferences.getPass()
                moDesti = guia.moDesti,
                remo = guia.codiRemo,
                codiAtes = guia.codiAtes, // Ajustar si MovimentPteDetail tiene transportista
                nifConductor = guia.nifConductor?: "",
                matricula = guia.matricula?: "",
                nombreAnimals = guia.numAnimals?: "0" // Ajustar si el detalle incluye cantidad
            )

            try {
                val response = repositorio.confirmarEntradaPorcina(request)

                if (response.isSuccessful && response.body()?.codi == "OK") {
                    // Filtramos la lista eliminando la guía confirmada por su código REMO
                    val listaActualizada = _uiState.value.listaEntradasPorcinos.filter {
                        it.codiRemo != guia.codiRemo
                    }

                    _uiState.update { it.copy(
                        listaEntradasPorcinos = listaActualizada,
                        isLoading = false
                    )}
                    Log.d("DEBUG_API", "Confirmación OK: ${guia.codiRemo}")
                } else {
                    val errorMsg = response.body()?.descripcio ?: "Error al confirmar"
                    _uiState.update { it.copy(isLoading = false) }
                    Log.e("DEBUG_API", "Error Negocio: $errorMsg")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e("DEBUG_API", "Error Conexión: ${e.localizedMessage}")
            }
        }
    }
}