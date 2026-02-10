package com.example.terrabit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences
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

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repositorio()
    private val userPreferences = UserPreferences(application)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _nifError = MutableStateFlow<String?>(null)
    val nifError: StateFlow<String?> = _nifError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _codiMOError = MutableStateFlow<String?>(null)
    val codiMOError: StateFlow<String?> = _codiMOError.asStateFlow()

    private val _savedNif = MutableStateFlow<String?>(null)
    val savedNif: StateFlow<String?> = _savedNif.asStateFlow()

    private val _savedPassword = MutableStateFlow<String?>(null)
    val savedPassword: StateFlow<String?> = _savedPassword.asStateFlow()

    private val _savedCodiMO = MutableStateFlow<String?>(null)
    val savedCodiMO: StateFlow<String?> = _savedCodiMO.asStateFlow()

    private val _savedRememberMe = MutableStateFlow(false)
    val savedRememberMe: StateFlow<Boolean> = _savedRememberMe.asStateFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        _savedNif.value = userPreferences.getNif()
        _savedPassword.value = userPreferences.getPassword()
        _savedCodiMO.value = userPreferences.getCodiMO()
        _savedRememberMe.value = userPreferences.getRememberMe()
    }

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

    fun login(nif: String, password: String, codiMO: String, rememberMe: Boolean) {
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
                        // Guardar o limpiar credenciales según checkbox
                        if (rememberMe) {
                            userPreferences.saveCredentials(nif, password, codiMO)
                        } else {
                            userPreferences.clearCredentials()
                        }

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