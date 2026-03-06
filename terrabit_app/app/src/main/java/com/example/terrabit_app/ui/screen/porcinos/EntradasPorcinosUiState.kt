package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.DataClassPorcinos.ConsultaMovimientosPorConfirmar
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail

data class EntradasPorcinosUiState(
    val listaEntradasPorcinos: List<MovimentPteDetail> = mutableListOf(),
    val isLoading: Boolean = false
)