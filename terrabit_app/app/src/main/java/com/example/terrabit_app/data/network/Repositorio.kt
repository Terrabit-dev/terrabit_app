package com.example.terrabit_app.data.network

class Repositorio {
    val apiInterface = ApiInterface.create()

    suspend fun getIdentificadoresDisponibles(
        nif: String,
        passwordMobilitat: String,
        codiMO: String
    ) = apiInterface.getIdentificadoresDisponibles(nif, passwordMobilitat, codiMO)

}