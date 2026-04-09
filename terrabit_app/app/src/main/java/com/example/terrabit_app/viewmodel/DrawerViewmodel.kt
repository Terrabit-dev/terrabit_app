package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _tipoAnimalSeleccionado = MutableLiveData("Bovinos")
    val tipoAnimalSeleccionado = _tipoAnimalSeleccionado

    fun seleccionarTipoAnimal(tipo: String) {
        _tipoAnimalSeleccionado.value = tipo
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.logout()
            onDone()
        }
    }
}