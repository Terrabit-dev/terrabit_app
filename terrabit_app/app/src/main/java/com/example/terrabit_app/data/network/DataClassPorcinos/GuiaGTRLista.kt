package com.example.terrabit_app.data.network.DataClassPorcinos


/**
 * Objecte que representa cada guia dins del llistat (Multiplicitat [0..N])
 */
data class GuiaGTRLista(
    val moOrigen: String, // Codi MO origen (6 car.)

    val remo: String, // Codi moviment (20 car.)

    val moDesti: String, // Codi MO destí (6 car.)

    val categoria: String, // '00' a '05'

    val nombreAnimals: Int, // Fins a 8 dígits (al JSON ve com número)

    val transportista: String?, // Codi ATES (Opcional/Nul·lable)

    val responsable: String?, // NIF Conductor (Opcional/Nul·lable)

    val vehicle: String?, // Matrícula (Opcional/Nul·lable)

    val dataSortida: Long, // Al JSON de l'exemple ve com número llarg

    val dataArribada: Long
)

/**
 * Estructura per capturar els errors segons el punt 5.2.7
 */
data class GtrErrorResponseLista(
    val codi: String,
    val descripcio: String
)