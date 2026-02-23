package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.DataClassPorcinos.ConsultaMovimientosPorConfirmar
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail

data class EntradasPorcinosUiState(
    // CAMBIAR "GuiaMobilitatPorcinos" POR EL DATACLASS INDICADO (aun no se ha creado)
    val listaEntradasPorcinos: List<MovimentPteDetail> = mutableListOf()
)
