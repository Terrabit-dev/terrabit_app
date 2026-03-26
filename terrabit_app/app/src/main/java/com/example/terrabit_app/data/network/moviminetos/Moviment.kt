package com.example.terrabit_app.data.network.moviminetos.modelos

import com.example.terrabit_app.data.network.Identificadores.IdenBovi

data class Moviment(
    val codiAtes: String = "",
    val codiRemo: String = "",
    val dataArribada: String = "",
    val dataSortida: String= "",
    val especie: String= "",
    val identificadors: List<IdenBovi> = emptyList(),
    val matricula: String? =null,
    val mitjaTransport: String?=null,
    val moDestinacio: String ="",
    val moOrigen: String?=null,
    val nifConductor: String?=null,
    val nomConductor: String? =null,
    val nomTransportista: String?=null,
    val regaDestinacio: String ="",
    val regaOrigen: String =""
)