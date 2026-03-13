package com.example.terrabit_app.data.network.Identificadores

import com.example.terrabit_app.data.network.respuestas.ResBasica
import kotlinx.serialization.Serializable

@Serializable
data class Identificadores(
    val identificadors: List<Identificador>? = null,
    val errors: List<ResBasica>? = null
)