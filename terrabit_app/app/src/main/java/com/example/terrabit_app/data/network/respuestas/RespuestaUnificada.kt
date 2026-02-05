package com.example.terrabit_app.data.network.respuestas

data class RespuestaUnificada(
    val codi: String? = null,
    val descripcio: String? = null,
    val errors: List<ResBasica>? = null
)