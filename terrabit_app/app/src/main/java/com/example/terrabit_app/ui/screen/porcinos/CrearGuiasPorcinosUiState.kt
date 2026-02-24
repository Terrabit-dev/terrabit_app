package com.example.terrabit_app.ui.screen.porcinos

data class CrearGuiasPorcinosUiState(
    // Datos del Formulario
    val explotacion: String = "",
    val categoriaExpandido: Boolean = false,
    val categoriaSeleccionada: String = "",
    val categoriaApiSeleccionada: String = "",
    val numAnimales: String = "",
    val fechaSalida: String = "",
    val fechaLlegada: String = "",
    val horaSalida: String = "",
    val horaLlegada: String = "",
    val mostrarDatePickerSalida: Boolean = false,
    val mostrarTimePickerSalida: Boolean = false,
    val mostrarDatePickerLlegada: Boolean = false,
    val mostrarTimePickerLlegada: Boolean = false,
    val codigoSIR: String = "",
    val medioTransporteExpandido: Boolean = false,
    val medioTransporteSeleccionado: String = "",
    val medioTransporteApiSeleccionado: String = "",
    val matricula: String = "",
    val nifConductor: String = "",

    // --- Estados de Control de Flujo ---
    val isLoading: Boolean = false,          // Muestra un ProgressBar mientras la API responde
    val mensajeError: String? = null,        // Para mostrar un Snackbar o texto en rojo si algo falla
    val mensajeExito: String? = null,        // Para confirmar que la guía se creó (con el ID del GTR)
    val formularioValido: Boolean = false    // (Opcional) Para habilitar/deshabilitar el botón de envío
)