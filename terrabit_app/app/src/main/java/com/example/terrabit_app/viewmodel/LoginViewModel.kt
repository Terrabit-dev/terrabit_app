package com.example.terrabit_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.DemoCredentialsLoader
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val nif: String, val password: String, val codiMO: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repositorio,
    private val userPreferences: UserPreferences,
    private val demoLoader: DemoCredentialsLoader
) : ViewModel() {

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

    /** True si el APK incluye credenciales demo cifradas. */
    private val _demoDisponible = MutableStateFlow(false)
    val demoDisponible: StateFlow<Boolean> = _demoDisponible.asStateFlow()

    /** True mientras se está usando el flujo demo (los 3 campos se muestran como "*****"). */
    private val _credencialesEnmascaradas = MutableStateFlow(false)
    val credencialesEnmascaradas: StateFlow<Boolean> = _credencialesEnmascaradas.asStateFlow()

    init {
        loadSavedCredentials()
        _demoDisponible.value = demoLoader.isAvailable()
    }

    private fun loadSavedCredentials() {
        viewModelScope.launch {
            if (userPreferences.getRememberMe()) {
                _savedNif.value = userPreferences.getNif()
                _savedPassword.value = userPreferences.getPassword()
                _savedCodiMO.value = userPreferences.getCodiMO()
                _savedRememberMe.value = true
            }
        }
    }

    fun clearFieldError(field: String) {
        when (field) {
            "nif" -> _nifError.value = null
            "password" -> _passwordError.value = null
            "codiMO" -> _codiMOError.value = null
        }
    }

    /** Sale del modo enmascarado (para volver a editar tras un error en login demo). */
    fun limpiarMascara() {
        _credencialesEnmascaradas.value = false
    }

    private fun validateFields(nif: String, password: String, codiMO: String): Boolean {
        var isValid = true
        if (nif.isBlank()) { _nifError.value = "El NIF es obligatorio"; isValid = false }
        else _nifError.value = null
        if (password.isBlank()) { _passwordError.value = "La contraseña es obligatoria"; isValid = false }
        else _passwordError.value = null
        if (codiMO.isBlank()) { _codiMOError.value = "El código MO es obligatorio"; isValid = false }
        else _codiMOError.value = null
        return isValid
    }

    fun login(nif: String, password: String, codiMO: String, rememberMe: Boolean) {
        if (!validateFields(nif, password, codiMO)) return
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            ejecutarLogin(nif, password, codiMO, rememberMe)
        }
    }

    fun guardarYContinuar(nif: String, password: String, codiMO: String, rememberMe: Boolean) {
        if (!validateFields(nif, password, codiMO)) return
        viewModelScope.launch {
            userPreferences.saveCredentials(nif, password, codiMO, rememberMe)
            _loginState.value = LoginState.Success(nif, password, codiMO)
        }
    }

    /**
     * Carga las credenciales demo cifradas del APK, enmascara los campos
     * de la UI ("*****") y ejecuta el login con las credenciales reales.
     * Tras el éxito, las credenciales reales quedan en SecureStorage para
     * que todos los endpoints (BaseBovinoViewModel, BasePorcinosViewModel, ...)
     * las usen exactamente igual que en un login normal.
     */
    fun loginConCuentaDemo() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val demo = demoLoader.load()
            if (demo == null) {
                _loginState.value = LoginState.Error("No se pudo cargar la cuenta demo")
                return@launch
            }
            _credencialesEnmascaradas.value = true
            ejecutarLogin(demo.nif, demo.password, demo.codiMO, rememberMe = true)
        }
    }

    private suspend fun ejecutarLogin(
        nif: String,
        password: String,
        codiMO: String,
        rememberMe: Boolean
    ) {
        try {
            val response = repository.getIdentificadoresDisponibles(nif, password, codiMO)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.errors == null) {
                    userPreferences.saveCredentials(nif, password, codiMO, rememberMe)
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

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}