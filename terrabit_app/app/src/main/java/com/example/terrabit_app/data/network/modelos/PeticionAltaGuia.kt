package com.example.terrabit_app.data.network.modelos

data class PeticionAltaGuia(
    // Campos Obligatorios (Opc = N)
    val nif: String,
    val passwordMobilitat: String,
    val especie: String,
    val explotacioOrigen: String,
    val explotacioDestinacio: String,
    val temporal: String,
    val dataSortida: String,
    val dataArribada: String,
    val mobilitat: String,
    val pais: String? = null,
    val codiExplotacio: String? = null,
    val codiAtes: String? = null,
    val nomTransportista: String? = null,
    val mitjaTransport: String? = null,
    val matricula: String? = null,
    val nifConductor: String? = null,
    val nomConductor: String? = null,
    val identificadors: List<String>? = null
)