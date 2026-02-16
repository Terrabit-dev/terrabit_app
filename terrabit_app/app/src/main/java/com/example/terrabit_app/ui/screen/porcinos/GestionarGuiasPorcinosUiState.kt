package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos

data class GestionarGuiasPorcinosUiState(
    val listaGuiasPorcinos: List<GuiaMobilitatPorcinos> = emptyList()
)
