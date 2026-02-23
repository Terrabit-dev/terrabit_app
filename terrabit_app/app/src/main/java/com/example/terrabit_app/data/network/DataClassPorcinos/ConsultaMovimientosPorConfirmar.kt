package com.example.terrabit_app.data.network.DataClassPorcinos

data class ConsultaMovimientosPorConfirmar(
    val codi: String, // Valor: "OK"

    val llistat: List<MovimentPteDetail>? = null
)

data class MovimentPteDetail(
    val moOrigen: String,

    val regaOrigen: String,

    val moDesti: String,

    val regaDesti: String,

    val codiRemo: String,

    val categoria: String,

    val nombreAnimals: String, // Al JSON de l'exemple ve com a String "1"

    val dataSortida: String, // Format DD/MM/YYYY segons l'exemple

    val dataArribada: String,

    val codiAtes: String?,

    val nomTransportista: String?,

    val matricula: String?,

    val nifConductor: String?
)

data class GtrErrorPteResponse(
    val guiaSolicitudResponse: List<GtrErrorPteDetail>
)

data class GtrErrorPteDetail(
    val codi: String, // Ex: "Error003"

    val descripcio: String
)