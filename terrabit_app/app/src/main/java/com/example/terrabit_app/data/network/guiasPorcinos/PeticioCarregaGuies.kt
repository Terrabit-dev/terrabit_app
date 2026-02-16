package com.example.terrabit_app.data.network.guiasPorcinos

data class PeticioCarregaGuies(

    val nif: String,
    val password: String,
    val codiMo: String,
    val codiRega: String,
    val dataSortida: String? = null // AAAAMMDDHHMM
)
