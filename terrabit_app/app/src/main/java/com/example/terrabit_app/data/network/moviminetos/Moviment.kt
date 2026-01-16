package com.example.terrabit_app.data.network.moviminetos.modelos

import com.example.terrabit_app.data.network.Identificadores.IdenBovi

data class Moviment(
    val codiAtes: String,
    val codiRemo: String,
    val dataArribada: String,
    val dataSortida: String,
    val especie: String,
    val identificadors: List<IdenBovi>,
    val matricula: Any,
    val mitjaTransport: String,
    val moDestinacio: String,
    val moOrigen: Any,
    val nifConductor: Any,
    val nomConductor: Any,
    val nomTransportista: Any,
    val regaDestinacio: String,
    val regaOrigen: String
)