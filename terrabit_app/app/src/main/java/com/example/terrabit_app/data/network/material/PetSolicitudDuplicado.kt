package com.example.terrabit_app.data.network.material

import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli

data class PetSolicitudDuplicado(
    val adreca: String?,
    val adrecaLliurament: String,
    val cp: String?,
    val empresaSubministradora: String,
    val especie: String,
    val identificadors: List<IdenSolicitudDupli?>,
    val municipi: String?,
    val nif: String,
    val oc: String?,
    val passwordMobilitat: String,
    val poblacio: String?,
    val telefonContacte: String?,
    val tipusEnviament: String
)