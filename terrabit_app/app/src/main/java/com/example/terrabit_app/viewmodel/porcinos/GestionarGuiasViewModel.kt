package com.example.terrabit_app.viewmodel.porcinos

import androidx.lifecycle.ViewModel
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GestionarGuiasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GestionarGuiasPorcinosUiState())
    val uiState : StateFlow<GestionarGuiasPorcinosUiState> = _uiState.asStateFlow()

}