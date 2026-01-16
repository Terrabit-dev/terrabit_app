package com.example.terrabit_app.data.network.Identificadores

import kotlinx.serialization.Serializable

@Serializable
data class Identificador(
    val identificador: String,
    val identificadorElectronic: String
)