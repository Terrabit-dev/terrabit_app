package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawerViewModel : ViewModel() {
    // Estado del drawer y tipo de animal
    private val _tipoAnimalSeleccionado = MutableLiveData("Bovinos")
    val tipoAnimalSeleccionado = _tipoAnimalSeleccionado

    fun seleccionarTipoAnimal(tipo: String) {
        _tipoAnimalSeleccionado.value = tipo
    }
}