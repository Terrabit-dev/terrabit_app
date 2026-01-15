package com.example.terrabit_app.data.network.modelos

import kotlinx.serialization.Serializable

@Serializable
data class Identificador(
    val identificador: String,
    val identificadorElectronic: String
)