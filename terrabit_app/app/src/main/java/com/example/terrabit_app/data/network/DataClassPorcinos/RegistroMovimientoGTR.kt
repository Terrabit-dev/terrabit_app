package com.example.terrabit_app.data.network.DataClassPorcinos

data class AltaMovimientoGTR(
    val nif: String?, // NIF Vinculat a la MO (9 car.)

    val password: String?, // Password mobilitat (fins a 20 car.)

    val tipusEspecie: String = "02", // '02' – Porcí

    val tipusAccio: String = "NO",

    val tipusMoviment: String = "01", // '01' - Entrada

    val explotacioSortida: String?, // Origen: MO o codi REGA (14 car.)

    val explotacioEntrada: String, // Destí: MO o codi REGA (14 car.)

    val codiCategoria: String, // '00' a '05'

    val numAnimals: Int, // Màxim 6 dígits

    val dataSortida: String, // Format [yyyymmddHHMM]

    val dataArribada: String, // Format [yyyymmddHHMM]

    val codiSirentra: String? = null, // Opcional (15 car.)

    val mitjaTransport: String? = "01", // S/S (Opcional/Nulable)

    val matricula: String? = null, // S/S (10 car.)

    val nifConductor: String? = null, // S/S (9 car.)

    val mobilitat: String = "SI" // 'SI' o 'NO'
)

data class AltaGuiaExitoResponse(
    val descripcio: List<String>
    // descripcio[0] -> "OK"
    // descripcio[1] -> "92523410999992950" (El código de la guía)
)

data class AltaGuiaErrorResponse(
    val resultat: List<GtrErrorDetail>
)
