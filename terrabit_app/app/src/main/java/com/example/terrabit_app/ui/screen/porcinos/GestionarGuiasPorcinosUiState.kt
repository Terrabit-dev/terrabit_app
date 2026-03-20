package com.example.terrabit_app.ui.screen.porcinos

import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista


data class GestionarGuiasPorcinosUiState(
    val rega: String = "",
    val fechaCorte: String = "",         // formato API "yyyyMMddHHmm"
    val fechaCorteDisplay: String = "",  //  formato display "dd/MM/yyyy HH:mm"
    val mostrarDatePicker: Boolean = false,
    val mostrarTimePicker: Boolean = false,
    val consultaIniciada: Boolean = false,
    val isLoading: Boolean = false,
    val listaGuiasPorcinos: List<GuiaGTRLista> = emptyList(),
    val guiaSeleccionada: GuiaGTRLista? = null,
    val mensajeError: String? = null
)