package com.example.terrabit_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val nif: String, val password: String, val codiMO: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val repository = Repositorio()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _nifError = MutableStateFlow<String?>(null)
    val nifError: StateFlow<String?> = _nifError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _codiMOError = MutableStateFlow<String?>(null)
    val codiMOError: StateFlow<String?> = _codiMOError.asStateFlow()

    fun clearFieldError(field: String) {
        when (field) {
            "nif" -> _nifError.value = null
            "password" -> _passwordError.value = null
            "codiMO" -> _codiMOError.value = null
        }
    }

    private fun validateFields(nif: String, password: String, codiMO: String): Boolean {
        var isValid = true

        if (nif.isBlank()) {
            _nifError.value = "El NIF es obligatorio"
            isValid = false
        } else {
            _nifError.value = null
        }

        if (password.isBlank()) {
            _passwordError.value = "La contraseña es obligatoria"
            isValid = false
        } else {
            _passwordError.value = null
        }

        if (codiMO.isBlank()) {
            _codiMOError.value = "El código MO es obligatorio"
            isValid = false
        } else {
            _codiMOError.value = null
        }

        return isValid
    }

    fun login(nif: String, password: String, codiMO: String) {
        if (!validateFields(nif, password, codiMO)) {
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = repository.getIdentificadoresDisponibles(
                    nif = nif,
                    passwordMobilitat = password,
                    codiMO = codiMO
                )

                if (response.isSuccessful) {
                    val identificadores = response.body()
                    if (identificadores != null && identificadores.identificadors.isNotEmpty()) {
                        _loginState.value = LoginState.Success(nif, password, codiMO)
                    } else {
                        _loginState.value = LoginState.Error("Credenciales incorrectas")
                    }
                } else {
                    _loginState.value = LoginState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}