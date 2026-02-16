package com.example.terrabit_app.data.network.lista_bovinos

data class Animal (
    val identificador: String,
    val identificadorElectronic: String?,
    val tipusIdentificadorElectronic: String?,
    val dataNaixement: String,
    val sexe: String,
    val raca: String,
    val identificadorMare: String?,
    val explotacioNaixement: String?,
    val paisNaixement: String?
)