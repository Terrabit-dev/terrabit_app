package com.example.terrabit_app.data.network.guias.guiasPorcinos

data class RespuestaMovilidadPorcinos(
    val descripcio: List<String>? = null,
    val resultat: List<ErrorItem>? = null
)

data class ErrorItem(
    val codi: String,
    val descripcio: String
)