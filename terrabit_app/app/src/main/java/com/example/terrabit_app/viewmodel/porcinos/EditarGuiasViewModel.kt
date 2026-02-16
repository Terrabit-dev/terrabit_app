package com.example.terrabit_app.viewmodel.porcinos

import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos
import com.example.terrabit_app.ui.screen.porcinos.EditarGuiasPorcinosUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditarGuiasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditarGuiasPorcinosUiState())
    val uiState : StateFlow<EditarGuiasPorcinosUiState> = _uiState.asStateFlow()

}