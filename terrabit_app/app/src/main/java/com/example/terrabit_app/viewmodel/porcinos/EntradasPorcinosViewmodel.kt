package com.example.terrabit_app.viewmodel.porcinos

import androidx.lifecycle.ViewModel
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinosUiState
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EntradasPorcinosViewmodel : ViewModel() {
    private val _uiState = MutableStateFlow(EntradasPorcinosUiState())
    val uiState : StateFlow<EntradasPorcinosUiState> = _uiState.asStateFlow()

    fun confirmarGuia() {
        // En caso de que la api devuelva exitoso, borrar de la lista
        TODO()
    }
}