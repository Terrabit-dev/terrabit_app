package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos

data class EntradasPorcinosUiState(
    // CAMBIAR "GuiaMobilitatPorcinos" POR EL DATACLASS INDICADO (aun no se ha creado)
    val listaEntradasPorcinos: List<GuiaMobilitatPorcinos> = emptyList()
)
