package com.example.terrabit_app.data.network.modelos

data class PeticionModificarGuia(
    val codiAtes: String,
    val codiRemo: String,
    val dataArribada: String,
    val dataSortida: String,
    val especie: String,
    val identificadors: List<String>,
    val matricula: String,
    val mitjaTransport: String,
    val nif: String,
    val nifConductor: String,
    val nomConductor: String,
    val nomTransportista: String,
    val passwordMobilitat: String
)