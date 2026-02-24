package com.example.terrabit_app.viewmodel.porcinos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.ApiInterface
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GestionarGuiasViewModel(
    private val repo: Repositorio,
    private val userPreferences: UserPreferences // Para obtener NIF/Pass
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionarGuiasPorcinosUiState())
    val uiState: StateFlow<GestionarGuiasPorcinosUiState> = _uiState.asStateFlow()

    fun cargarMovimientosDesdeApi() {
        viewModelScope.launch {
            Log.d("DEBUG_API", "--- Iniciant crida a la API ---")
            _uiState.update { it.copy(isLoading = true) }

            // Dades de l'administrador (Recorda canviar-ho per userPreferences després)
            val nif = "37370803N"
            val pass = "5Q62h4rP"
            val codiMo = "1880AE"
            val rega = "ES080470001881"
            val fechaCorte = "202401010000"

            Log.d("DEBUG_API", "Params enviats: NIF=$nif, MO=$codiMo, REGA=$rega, Data=$fechaCorte")

            try {
                val response = repo.getGuiasMobilitatPorcinas(nif, pass, codiMo, rega, fechaCorte)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("DEBUG_API", "Resposta Exitosa (Codi ${response.code()})")
                    Log.d("DEBUG_API", "Número d'elements rebuts: ${body?.size ?: 0}")

                    if (body != null) {
                        // Loguegem el primer element per veure si els noms de camps coincideixen
                        if (body.isNotEmpty()) {
                            Log.d("DEBUG_API", "Exemple primer element: ${body[0]}")
                        }

                        _uiState.update { it.copy(
                            listaGuiasPorcinos = body,
                            isLoading = false
                        )}
                    }
                } else {
                    // Error de la API (ex: 404, 500, 401)
                    val errorMsg = response.errorBody()?.string()
                    Log.e("DEBUG_API", "Error de la API: Codi ${response.code()}")
                    Log.e("DEBUG_API", "Cos de l'error: $errorMsg")

                    _uiState.update { it.copy(
                        isLoading = false,
                        mensajeError = "Error ${response.code()}: $errorMsg"
                    )}
                }
            } catch (e: Exception) {
                // Error de connexió o crash de l'App
                Log.e("DEBUG_API", "EXCEPCIÓ CRÍTICA: ${e.message}")
                e.printStackTrace() // Això imprimeix tota la traça a la consola

                _uiState.update { it.copy(
                    isLoading = false,
                    mensajeError = "Excepció: ${e.localizedMessage}"
                )}
            }
            Log.d("DEBUG_API", "--- Finalització crida a la API ---")
        }
    }

    fun editarYConfirmarGuia() {
        // En caso de que la api devuelva exitoso, borrar de la lista
        TODO()
    }

    fun confirmarGuia() {
        // En caso de que la api devuelva exitoso, borrar de la lista
        TODO()
    }
}