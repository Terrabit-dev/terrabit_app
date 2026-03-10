package com.example.terrabit_app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.terrabit_app.data.Borrador


@Entity(tableName = "borradores")
data class BorradorEntity(
    @PrimaryKey
    val id: String,
    val tipo: String,
    val fecha: String,
    val hora: String,
    val datos: String,
    val estado: String = "PENDIENTE"
)

fun BorradorEntity.toBorrador() = Borrador(
    id = id,
    tipo = tipo,
    fecha = fecha,
    hora = hora,
    datos = datos,
    estado = estado
)

fun Borrador.toEntity() = BorradorEntity(
    id = id,
    tipo = tipo,
    fecha = fecha,
    hora = hora,
    datos = datos,
    estado = estado
)