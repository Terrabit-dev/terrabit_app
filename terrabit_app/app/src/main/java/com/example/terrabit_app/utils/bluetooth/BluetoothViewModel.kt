package com.example.terrabit_app.utils.bluetooth



import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.R
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Estado del proceso de lectura Bluetooth.

sealed class BluetoothScanState {
    // Estado inicial / inactivo
    object Idle : BluetoothScanState()
    // Esperando mensaje del Arduino
    object Esperando : BluetoothScanState()
    // Mensaje recibido correctamente
    data class Recibido(val mensaje: String) : BluetoothScanState()
    // Error durante la conexión o lectura
    data class Error(val mensaje: Int) : BluetoothScanState()
    // Bluetooth no disponible o sin permisos
    object SinBluetooth : BluetoothScanState()
    // Mostrar selector de dispositivos emparejados
    object SeleccionandoDispositivo : BluetoothScanState()
}

/**
 * ViewModel compartido para la funcionalidad Bluetooth.
 * Se puede inyectar en cualquier pantalla que necesite leer del Arduino.
 *
 * Flujo de uso:
 * 1. UI llama a iniciarEscaneo()
 * 2. Si ya hay un dispositivo guardado → pasa a Esperando directamente
 * 3. Si no hay dispositivo → pasa a SeleccionandoDispositivo (muestra lista)
 * 4. Usuario selecciona → se guarda la MAC y pasa a Esperando
 * 5. Recibe mensaje → estado Recibido(mensaje)
 * 6. UI consume el mensaje llamando a consumirMensaje()
 */
class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _scanState = MutableStateFlow<BluetoothScanState>(BluetoothScanState.Idle)
    val scanState: StateFlow<BluetoothScanState> = _scanState.asStateFlow()

    // Lista de dispositivos emparejados para mostrar en el selector
    private val _dispositivosEmparejados = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val dispositivosEmparejados: StateFlow<List<Pair<String, String>>> = _dispositivosEmparejados.asStateFlow()

    // MAC del dispositivo Arduino guardado (persiste entre sesiones)
    private var macGuardada: String?
        get() = userPreferences.getArduinoMac()
        set(value) { userPreferences.saveArduinoMac(value) }

    /**
     * Punto de entrada desde la UI cuando el usuario pulsa el icono Bluetooth.
     * Determina si tiene permisos, si hay dispositivo guardado, etc.
     */
    fun iniciarEscaneo(context: Context) {
        if (!ArduinoBluetoothManager.bluetoothDisponible(context)) {
            _scanState.value = BluetoothScanState.SinBluetooth
            return
        }

        if (!ArduinoBluetoothManager.tienePermisos(context)) {
            _scanState.value = BluetoothScanState.Error(R.string.bluethooth_error_permissions)
            return
        }

        val mac = macGuardada
        if (mac != null) {
            // Ya tenemos un dispositivo guardado → conectar directamente
            conectarYEsperar(context, mac)
        } else {
            // Mostrar selector de dispositivos emparejados
            val dispositivos = ArduinoBluetoothManager.dispositivosEmparejados(context)
            _dispositivosEmparejados.value = dispositivos
            _scanState.value = BluetoothScanState.SeleccionandoDispositivo
        }
    }

    //Llamado cuando el usuario selecciona un dispositivo de la lista.
    //Guarda la MAC y comienza la espera.
    fun seleccionarDispositivo(context: Context, nombre: String, mac: String) {
        macGuardada = mac
        conectarYEsperar(context, mac)
    }

    //Olvida el dispositivo guardado.
    //La próxima vez volverá a pedir selección.

    fun olvidarDispositivo() {
        macGuardada = null
    }

    //Conecta al Arduino y espera recibir un mensaje.

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

    //Cancela la espera actual (usuario cierra el diálogo).

    fun cancelarEscaneo() {
        ArduinoBluetoothManager.cerrarSocket()
        _scanState.value = BluetoothScanState.Idle
    }

    //Resetea el estado a Idle después de consumir el mensaje recibido.
    //Llamar desde la UI después de copiar el valor al campo.

    fun resetearEstado() {
        _scanState.value = BluetoothScanState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        ArduinoBluetoothManager.cerrarSocket()
    }
}