package com.example.terrabit_app.data.network.modelos

data class PetConfirmacionMovi(
    val codiAtes: String,
    val codiRemo: String,
    val dataArribada: String,
    val especie: String,
    val explotacioDestinacio: String,
    val identificadors: List<IdenMovimiento>,
    val matricula: String,
    val mitjaTransport: String,
    val nif: String,
    val nifConductor: String,
    val nomConductor: String,
    val nomTransportista: String,
    val passwordMobilitat: String
)