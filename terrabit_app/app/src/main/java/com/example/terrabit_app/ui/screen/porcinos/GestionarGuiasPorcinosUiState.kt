package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista


data class GestionarGuiasPorcinosUiState(
    val listaGuiasPorcinos: List<GuiaGTRLista> = mutableListOf(),
    val isLoading: Boolean = false,
    val mensajeError: String? = null,

    val guiaSeleccionada: GuiaGTRLista? = null
)