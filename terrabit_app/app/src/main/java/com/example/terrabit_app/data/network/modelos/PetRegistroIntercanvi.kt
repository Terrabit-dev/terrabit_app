package com.example.terrabit_app.data.network.modelos

data class PetRegistroIntercanvi(
    val codiAtes: String,
    val codiExplotacio: String,
    val dataArribada: String,
    val dataCertificat: String,
    val dataSortida: String,
    val especie: String,
    val explotacioDestinacio: String,
    val identificadors: List<IdenIntercanvi>,
    val matricula: String,
    val mitjaTransport: String,
    val nif: String,
    val nifConductor: String,
    val nomConductor: String,
    val nomTransportista: String,
    val numCertificat: String,
    val paisOrigen: String,
    val passwordMobilitat: String,
    val pif: String?,
    val temporal: String
)