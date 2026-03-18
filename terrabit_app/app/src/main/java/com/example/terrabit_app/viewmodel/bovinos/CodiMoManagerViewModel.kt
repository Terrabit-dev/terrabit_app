package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CodiMoManagerViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _codisMoExpandido = MutableLiveData(false)
    val codisMoExpandido = _codisMoExpandido

    // CodiMO actualmente activo en SharedPreferences
    private val _codiMoActivo = MutableLiveData(userPreferences.getCodiMO())
    val codiMoActivo = _codiMoActivo

    fun toggleCodisMoExpandido() {
        _codisMoExpandido.value = !(_codisMoExpandido.value ?: false)
    }

    fun cerrarCodisMo() {
        _codisMoExpandido.value = false
    }

    fun getCodisMos(): List<String> = userPreferences.getUserMOList()

    fun seleccionarCodiMo(nuevoCodi: String) {
        // Actualiza SharedPreferences manteniendo el resto de credenciales
        val nif = userPreferences.getNif() ?: return
        val password = userPreferences.getPassword() ?: return
        val rememberMe = userPreferences.getRememberMe()

        userPreferences.saveCredentials(nif, password, nuevoCodi, rememberMe)
        _codiMoActivo.value = nuevoCodi
        _codisMoExpandido.value = false
    }
}
