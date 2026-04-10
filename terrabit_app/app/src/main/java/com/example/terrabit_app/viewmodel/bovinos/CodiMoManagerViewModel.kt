package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CodiMoManagerViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _codisMoExpandido = MutableLiveData(false)
    val codisMoExpandido = _codisMoExpandido

    private val _codiActualizado = MutableLiveData(false)
    val codiActualizado = _codiActualizado


    private val _codiMoActivo = MutableLiveData<String?>(null)
    val codiMoActivo = _codiMoActivo

    private val _codisMoList = MutableLiveData<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            _codiMoActivo.value = userPreferences.getCodiMO()
            _codisMoList.value = userPreferences.getUserMOList()
        }
    }

    fun toggleCodisMoExpandido() {
        _codisMoExpandido.value = !(_codisMoExpandido.value ?: false)
        _codiActualizado.value = false
    }

    fun cerrarCodisMo() {
        _codisMoExpandido.value = false
        _codiActualizado.value = false
    }

    fun getCodisMos(): List<String> = _codisMoList.value ?: emptyList()

    fun seleccionarCodiMo(nuevoCodi: String) {
        viewModelScope.launch {
            val nif = userPreferences.getNif() ?: return@launch
            val password = userPreferences.getPassword() ?: return@launch
            val rememberMe = userPreferences.getRememberMe()
            userPreferences.saveCredentials(nif, password, nuevoCodi, rememberMe)
            _codiMoActivo.value = nuevoCodi
            _codisMoExpandido.value = false
            _codiActualizado.value = true
        }
    }
}