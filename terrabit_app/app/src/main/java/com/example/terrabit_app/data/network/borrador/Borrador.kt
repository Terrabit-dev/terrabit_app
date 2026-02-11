package com.example.terrabit_app.data

data class Borrador(
    val id: String,
    val tipo: String,
    val fecha: String,
    val hora: String,
    val datos: String,
    val estado: String = "PENDIENTE"
)