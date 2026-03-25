package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


// Centralizar las funciones en comun de los Bovinos.

abstract class BaseBovinoViewModel: ViewModel() {

    protected abstract val _identificadorAnimal: MutableLiveData<String>

    fun precargarAnimal(animalId: String) {
        _identificadorAnimal.value = animalId
    }

    val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    fun resetearCodiError() {
        _codiError.value = null
    }
}