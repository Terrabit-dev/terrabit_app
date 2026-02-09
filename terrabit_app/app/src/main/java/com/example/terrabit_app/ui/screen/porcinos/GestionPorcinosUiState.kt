package com.example.terrabit_app.ui.screen.porcinos

data class GestionPorcinosUiState(
    var explotacion: String = "",
    var categoriaExpandido: Boolean = false,
    var categoriaSeleccionada: String = "",
    var categoriaApiSeleccionada: String = "",
    var numAnimales: String = "",
    var fechaSalida: String = "",
    var fechaLlegada: String = "",
    var mostrarDatePickerSalida: Boolean = false,
    var mostrarDatePickerLlegada: Boolean = false,
    var codigoSIR: String = "",
    var medioTransporteExpandido: Boolean = false,
    var medioTransporteSeleccionado: String = "",
    var medioTransporteApiSeleccionado: String = "",
    var matricula: String = "",
    var nifConductor: String = ""
)
