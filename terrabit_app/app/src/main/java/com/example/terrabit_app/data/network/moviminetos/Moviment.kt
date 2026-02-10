package com.example.terrabit_app.data.network.moviminetos.modelos

import com.example.terrabit_app.data.network.Identificadores.IdenBovi

data class Moviment(
    val codiAtes: String,
    val codiRemo: String,
    val dataArribada: String,
    val dataSortida: String,
    val especie: String,
    val identificadors: List<IdenBovi>,
    val matricula: String?,
    val mitjaTransport: String,
    val moDestinacio: String,
    val moOrigen: String?,
    val nifConductor: String?,
    val nomConductor: String?,
    val nomTransportista: String?,
    val regaDestinacio: String,
    val regaOrigen: String
)