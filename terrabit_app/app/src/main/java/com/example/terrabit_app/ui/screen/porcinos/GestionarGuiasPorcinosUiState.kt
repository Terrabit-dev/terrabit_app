package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.DataClassPorcinos.AltaMovimientoGTR
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail

data class GestionarGuiasPorcinosUiState(
    val listaGuiasPorcinos: List<GuiaGTRLista> = emptyList(),
    val isLoading: Boolean = false,
    val mensajeError: String? = null
)