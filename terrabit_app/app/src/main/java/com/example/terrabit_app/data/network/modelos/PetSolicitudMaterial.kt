package com.example.terrabit_app.data.network.modelos

data class PetSolicitudMaterial(
    val adreca: String,
    val adrecaLliurament: String,
    val cp: String,
    val empresaSubministradora: String,
    val especie: String,
    val municipi: String,
    val nif: String,
    val oc: String,
    val passwordMobilitat: String,
    val poblacio: String,
    val telefonContacte: String,
    val tipusEnviament: String,
    val tipusMaterial: String,
    val unitats: List<Unitat>
)