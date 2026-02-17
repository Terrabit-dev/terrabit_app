package com.example.terrabit_app.data.network.lista_bovinos

import com.google.gson.annotations.SerializedName

data class Animal (
    val identificador: String,
    val identificadorElectronic: String?,
    val tipusIdentificadorElectronic: String?,
    val dataNaixement: String,
    val sexe: String,
    @SerializedName("raza")
    val raca: String,
    val identificadorMare: String?,
    val explotacioNaixement: String?,
    val paisNaixement: String?
)