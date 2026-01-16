package com.example.terrabit_app.data.network.modelos

data class PetModificacioMovi(
    val codiAtes: String,
    val codiExplotacio: String,
    val codiRemo: String,
    val especie: String,
    val identificadors: List<IdenModificacionMovi>,
    val matricula: String,
    val mitjaTransport: String,
    val nif: String,
    val nifConductor: String,
    val nomConductor: String,
    val nomTransportista: String,
    val passwordMobilitat: String
)