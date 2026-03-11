package com.example.terrabit_app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "historial")
data class HistorialEntity(
    @PrimaryKey val id: String,
    val tipo: String,
    val fecha: String,
    val hora: String,
    val datos: String,
    val resumen: String = ""
)

data class Historial(
    val id: String,
    val tipo: String,
    val fecha: String,
    val hora: String,
    val datos: String,
    val resumen: String = ""
)

fun HistorialEntity.toHistorial() = Historial(id, tipo, fecha, hora, datos, resumen)
fun Historial.toEntity() = HistorialEntity(id, tipo, fecha, hora, datos, resumen)