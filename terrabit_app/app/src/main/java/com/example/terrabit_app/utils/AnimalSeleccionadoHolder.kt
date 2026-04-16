package com.example.terrabit_app.utils

object AnimalSeleccionadoHolder {
    var animalId: String = ""

    fun set(id: String) { animalId = id }

    fun consume(): String {
        val id = animalId
        animalId = ""
        return id
    }
}