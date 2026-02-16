package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos

data class EditarGuiasPorcinosUiState(
    val listaGuiasPorcinos: List<GuiaMobilitatPorcinos> = emptyList()
)
