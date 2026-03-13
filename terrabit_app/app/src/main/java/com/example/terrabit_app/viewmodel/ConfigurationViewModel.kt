package com.example.terrabit_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CodiMoState{
    object  Idle : CodiMoState()

    object Esperando : CodiMoState()

    object Succes : CodiMoState()

    data class Error(val mensaje: String) : CodiMoState()
}
@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val repository: Repositorio,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _codiMoState = MutableStateFlow<CodiMoState>(CodiMoState.Idle)

    val codiMoState: StateFlow<CodiMoState> = _codiMoState

    private val _codiMo = MutableLiveData("")
    val codiMo = _codiMo
    private val _isLoading = MutableLiveData(false)
    val isLoading = _isLoading

    private val _isError = MutableLiveData(false)
    val isError = _isError

    private val _isSuccess = MutableLiveData(false)
    val isSuccess = _isSuccess

    private val _errorMessage = MutableLiveData("")
    val errorMessage = _errorMessage




    private val _isDarkTheme = MutableStateFlow(userPreferences.getDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    val nif: String = userPreferences.getNif() ?: "No disponible"

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        userPreferences.saveDarkTheme(isDark)
    }

    fun actualizarCodiMo(nuevoCodiMo: String) {
        _codiMo.value = nuevoCodiMo
    }

    fun verificarCodiMo(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getIdentificadoresDisponibles(
                    nif = userPreferences.getNif()!!,
                    passwordMobilitat = userPreferences.getPassword()!!,
                    codiMO = _codiMo.value!!
                )
                if (response.isSuccessful  && response.body() != null ) {
                    val body = response.body()!!
                    if (body.errors == null) {
                        userPreferences.addMOToUserList(_codiMo.value!!)
                        _isSuccess.value = true
                        _isLoading.value = false
                    } else{
                        _isError.value = true
                        _errorMessage.value = "Codigo invalido"
                        _isLoading.value = false
                    }
                } else {
                    _isError.value = true
                    _errorMessage.value = "Error: ${response.code()}"
                    _isLoading.value = false
                }
            }
            catch (e: Exception) {
                _isError.value = true
                _errorMessage.value = "Error de conexión: ${e.message}"
                _isLoading.value = false
            }
        }

    }

    fun resetState(){
        _isError.value = false
        _isSuccess.value = false
        _errorMessage.value = ""
        _codiMo.value = ""
    }

}