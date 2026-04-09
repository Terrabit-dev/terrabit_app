package com.example.terrabit_app.utils.bluetooth

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.R
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.AndroidViewModel

sealed class BluetoothScanState {
    object Idle : BluetoothScanState()
    object Esperando : BluetoothScanState()
    data class Recibido(val mensaje: String) : BluetoothScanState()
    data class Error(val mensaje: Int) : BluetoothScanState()
    object SinBluetooth : BluetoothScanState()
    object SeleccionandoDispositivo : BluetoothScanState()
}

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    application: Application,
    private val userPreferences: UserPreferences
) : AndroidViewModel(application) {

    private val _scanState = MutableStateFlow<BluetoothScanState>(BluetoothScanState.Idle)
    val scanState: StateFlow<BluetoothScanState> = _scanState.asStateFlow()

    private val _dispositivosEmparejados = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val dispositivosEmparejados: StateFlow<List<Pair<String, String>>> = _dispositivosEmparejados.asStateFlow()

    fun iniciarEscaneo(context: Context) {
        if (!ArduinoBluetoothManager.bluetoothDisponible(context)) {
            _scanState.value = BluetoothScanState.SinBluetooth
            return
        }

        if (!ArduinoBluetoothManager.tienePermisos(context)) {
            _scanState.value = BluetoothScanState.Error(R.string.bluethooth_error_permissions)
            return
        }

        viewModelScope.launch {
            val mac = userPreferences.getArduinoMac()
            if (mac != null) {
                conectarYEsperar(context, mac)
            } else {
                val dispositivos = ArduinoBluetoothManager.dispositivosEmparejados(context)
                _dispositivosEmparejados.value = dispositivos
                _scanState.value = BluetoothScanState.SeleccionandoDispositivo
            }
        }
    }

    fun seleccionarDispositivo(context: Context, nombre: String, mac: String) {
        viewModelScope.launch {
            userPreferences.saveArduinoMac(mac)
            conectarYEsperar(context, mac)
        }
    }

    fun olvidarDispositivo() {
        viewModelScope.launch {
            userPreferences.deleteArduinoMac()
        }
    }

    private fun conectarYEsperar(context: Context, mac: String) {
        _scanState.value = BluetoothScanState.Esperando

        viewModelScope.launch {
            val resultado = ArduinoBluetoothManager.esperarMensaje(context, mac)

            resultado.fold(
                onSuccess = { mensaje ->
                    _scanState.value = BluetoothScanState.Recibido(mensaje)
                },
                onFailure = { error ->
                    val mensajeError = when {
                        error.message?.contains("Unable to connect") == true ->
                            R.string.bluetooth_error_unable_to_connect
                        error.message?.contains("Permission") == true ||
                                error is SecurityException ->
                            R.string.bluetooth_error_no_permissions
                        else ->
                            R.string.bluetooth_error_unknown
                    }
                    _scanState.value = BluetoothScanState.Error(mensajeError)
                }
            )
        }
    }

    fun cancelarEscaneo() {
        ArduinoBluetoothManager.cerrarSocket()
        _scanState.value = BluetoothScanState.Idle
    }

    fun resetearEstado() {
        _scanState.value = BluetoothScanState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        ArduinoBluetoothManager.cerrarSocket()
    }

    fun buscarDispositivos() {
        viewModelScope.launch {
            userPreferences.deleteArduinoMac()
            val dispositivos = ArduinoBluetoothManager.dispositivosEmparejados(getApplication())
            _dispositivosEmparejados.value = dispositivos
            _scanState.value = BluetoothScanState.SeleccionandoDispositivo
        }
    }
}