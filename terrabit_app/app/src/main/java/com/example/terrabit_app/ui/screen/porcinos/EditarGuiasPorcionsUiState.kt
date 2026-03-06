package com.example.terrabit_app.ui.screen.porcinos

data class EditarGuiasPorcionsUiState (
    // Datos del Formulario
    val categoriaSeleccionada: String = "",
    val categoriaCodigo: String = "", // Aquí guardaremos el '00'-'05' para la API
    val numAnimales: String = "",
    val fechaSalida: String = "",
    val horaSalida: String = "",
    val fechaLlegada: String = "",
    val horaLlegada: String = "",
    val codigoSIR: String = "", // Transportista (Codi ATES)
    val matricula: String = "", // Vehículo
    val nifConductor: String = "", // Responsable

    // Estados de la Interfaz (Dropdowns y Pickers)
    val categoriaExpandido: Boolean = false,
    val mostrarDatePickerSalida: Boolean = false,
    val mostrarTimePickerSalida: Boolean = false,
    val mostrarDatePickerLlegada: Boolean = false,
    val mostrarTimePickerLlegada: Boolean = false,

    // Estados de Carga y Error
    val isLoading: Boolean = false,
    val error: String? = null,
    val esExitoso: Boolean = false,

    val remoActual: String=""
)