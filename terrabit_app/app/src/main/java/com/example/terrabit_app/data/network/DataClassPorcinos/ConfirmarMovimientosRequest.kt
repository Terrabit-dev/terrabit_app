package com.example.terrabit_app.data.network.DataClassPorcinos


data class ConfirmarMovimientosRequest(
    val nif: String, // NIF de l'usuari (9 car.)

    val password: String, // Password mobilitat (10 car.)

    val moDesti: String, // Marca Oficial Destí (6 car.)

    val remo: String, // Codi REMO (17 car.)

    val codiAtes: String?, // Codi ATES (Opcional, 15 car.)
    val nifConductor: String, // NIF conductor (9 car.)

    val matricula: String, // Matrícula (30 car.)

    val nombreAnimals: String // Nombre d'animals (Fins a 8 dígits)
)

// Resposta estàndard (Èxit o Error simple)
data class GtrConfirmacioResponse(
    val codi: String, // "OK" o codi d'error

    val descripcio: String?,

    val guiaSolicitudResponse: List<GtrErrorDetail>? = null
)

data class GtrErrorDetail(
    val codi: String,
    val descripcio: String
)