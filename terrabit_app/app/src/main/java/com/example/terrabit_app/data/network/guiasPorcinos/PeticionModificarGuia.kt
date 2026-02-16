package com.example.terrabit_app.data.network.guiasPorcinos

data class PeticionModificarGuiaPorcinos(
    val nif: String,
    val password: String,
    val remo: String,
    val categoria: String,       // "00" a "05"
    val nombreAnimals: String,   // String según ejemplo
    val transportista: String,
    val responsable: String,
    val vehicle: String,
    val dataSortida: String,     // AAAAMMDDHHMM
    val dataArribada: String     // AAAAMMDDHHMM
)

