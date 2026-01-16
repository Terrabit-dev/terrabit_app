package com.example.terrabit_app.data.network.modelos

data class IdenIntercanvi(
    val identificador: String,
    val dataNaixement: String,
    val paisNaixement: String,
    val estatArribada: String,
    val identificadorElectronic: String?,
    val tipusidentificadorElectronic: String?,
    val identificadorAnterior: String?,
    val sexe: String?,
    val raza: String?,
    val explotacioNaixement: String?,
    val identificadorMare: String?,
    val dataSacrMort: String?,
    val pesCanal: String?,
    val classCanal: String?,
    val tipusPresentacio: String?
)