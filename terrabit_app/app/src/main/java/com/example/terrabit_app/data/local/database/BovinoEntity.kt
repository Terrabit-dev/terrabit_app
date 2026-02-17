package com.example.terrabit_app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.terrabit_app.data.network.lista_bovinos.Animal

@Entity(tableName = "bovinos")
data class BovinoEntity(
    @PrimaryKey
    val identificador: String,
    val identificadorElectronic: String?,
    val tipusIdentificadorElectronic: String?,
    val dataNaixement: String,
    val sexe: String,
    val raca: String,
    val identificadorMare: String?,
    val explotacioNaixement: String?,
    val paisNaixement: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun BovinoEntity.toAnimal() = Animal(
    identificador = identificador,
    identificadorElectronic = identificadorElectronic,
    tipusIdentificadorElectronic = tipusIdentificadorElectronic,
    dataNaixement = dataNaixement,
    sexe = sexe,
    raca = raca,
    identificadorMare = identificadorMare,
    explotacioNaixement = explotacioNaixement,
    paisNaixement = paisNaixement
)

fun Animal.toEntity() = BovinoEntity(
    identificador = identificador,
    identificadorElectronic = identificadorElectronic,
    tipusIdentificadorElectronic = tipusIdentificadorElectronic,
    dataNaixement = dataNaixement,
    sexe = sexe,
    raca = raca,
    identificadorMare = identificadorMare,
    explotacioNaixement = explotacioNaixement,
    paisNaixement = paisNaixement
)