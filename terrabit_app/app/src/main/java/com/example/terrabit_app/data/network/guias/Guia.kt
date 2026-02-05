package com.example.terrabit_app.data.network.guias

data class Guia(
    val codiTransportista: String,
    val dataArribada: String,
    val dataSortida: String,
    val explotacioDestinacio: String,
    val explotacioOrigen: String,
    val identificadors: List<String>,
    val matricula: String,
    val nifConductor: String,
    val numeroAnimals: Int,
    val remo: String
)