package com.example.terrabit_app.data.network.guias.guiasPorcinos

data class PeticioMobilitatPorci(

    val nif: String,
    val password: String,
    val tipusEspecie: String = "02", // Porcí
    val tipusAccio: String = "NO",
    val tipusMoviment: String = "01",
    val explotacioSortida: String,
    val explotacioEntrada: String,
    val codiCategoria: String,
    val numAnimals: Int,
    val dataSortida: String, // Format: yyyymmddHHMM
    val dataArribada: String, // Format: yyyymmddHHMM
    val codiSirentra: String? = null,
    val mitjaTransport: String? = null, // 01 Camió, 99 Altres
    val matricula: String? = null,
    val nifConductor: String? = null,
    val mobilitat: String  = "SI"
)