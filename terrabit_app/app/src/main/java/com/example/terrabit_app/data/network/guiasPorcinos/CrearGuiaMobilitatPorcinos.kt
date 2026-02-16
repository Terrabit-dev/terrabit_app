package com.example.terrabit_app.data.network.guiasPorcinos

data class CrearGuiaMobilitatPorcinos(
    val nif: String?,
    val password: String?,

    val tipoEspecie: String = "02",
    val tipoAccion: String = "NO",
    val tipoMovimiento: String = "01",

    val moOrigen: String?,
    val moDesti: String,

    val categoria: String,
    val nombreAnimals: Int,
    val dataSortida: Long,
    val dataArribada: Long,

    val codiSir: String,
    val medioTransporte: String?,
    val matricula: String?,
    val nifConductor: String?,

    val mobilitat: String = "SI"
)
