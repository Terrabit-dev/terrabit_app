package com.example.terrabit_app.data.network.respuestas

data class ResAltaGuia(
    val codiRemo: String? = null,
    val descripcio: String? = null,
    val tipusGuia: String? = null,
    val validacions: String? = null,
    val errors: List<ResBasica>? = null

)