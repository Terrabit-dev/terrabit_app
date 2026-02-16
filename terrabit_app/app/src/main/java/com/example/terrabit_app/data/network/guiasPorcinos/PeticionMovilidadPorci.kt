package com.example.terrabit_app.data.network.guiasPorcinos

import com.google.gson.annotations.SerializedName

data class GuiaMobilitat(
    val moOrigen: String,
    val remo: String,
    val moDesti: String,
    val categoria: String,
    val nombreAnimals: Int,
    val transportista: String?,
    val responsable: String?,
    val vehicle: String?,
    val dataSortida: Long,   // viene numérico
    val dataArribada: Long
)
