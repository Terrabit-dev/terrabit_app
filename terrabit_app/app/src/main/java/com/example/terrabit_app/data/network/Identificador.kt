package com.example.terrabit_app.data.network

import kotlinx.serialization.Serializable

@Serializable
data class Identificador(
    val identificador: String,
    val identificadorElectronic: String
)