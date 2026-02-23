package com.example.terrabit_app.data.network.DataClassPorcinos

data class ModificarMovimentsAGias(
    val nif: String,

    val password: String,

    val remo: String, // Codi del moviment (20 car.)

    val categoria: String, // '00' a '05'

    val nombreAnimals: String, // A l'exemple JSON surt com a String "50"

    val transportista: String, // Codi ATES

    val responsable: String, // NIF Conductor

    val vehicle: String, // Matrícula

    val dataSortida: String, // Format [yyyymmddHHMM]

    val dataArribada: String
)

data class GtrStandardResponse(
    val codi: String, // "OK" en cas d'èxit, o codi numèric en cas d'error

    val descripcio: String // Buit "" en cas d'èxit, o missatge d'error
)