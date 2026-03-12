package com.example.terrabit_app.utils.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.R
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ACTION_USB_PERMISSION = "com.example.terrabit_app.USB_PERMISSION"
private const val TAG = "USB_DEBUG"

data class UsbSerialState(
    val conectado: Boolean = false,
    val error: Int? = null,
    val cargando: Boolean = false
)

@HiltViewModel
class UsbSerialViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UsbSerialState())
    val state: StateFlow<UsbSerialState> = _state.asStateFlow()

    // SharedFlow para mensajes — los colectores activos los reciben,
    // los que no están suscritos no los acumulan
    private val _mensajes = MutableSharedFlow<String>(replay = 0)
    val mensajes = _mensajes.asSharedFlow()

    private var serialDevice: UsbSerialDevice? = null
    private var receiver: BroadcastReceiver? = null
    private val byteBuffer = mutableListOf<Byte>()

    init {
        registrarReceiver()
    }

    private fun registrarReceiver() {
        // Evita registrar dos veces
        if (receiver != null) return

        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                        Log.d(TAG, "Cable desconectado")
                        cerrarConexion()
                        _state.value = UsbSerialState()
                    }
                    ACTION_USB_PERMISSION -> {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        val granted = intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false
                        )
                        Log.d(TAG, "Permiso: granted=$granted device=${device?.deviceName}")
                        when {
                            device == null -> _state.value = UsbSerialState(error = R.string.usb_error_not_found)
                            !granted -> _state.value = UsbSerialState(error = R.string.usb_error_permission_denied)
                            else -> {
                                _state.value = UsbSerialState(cargando = true)
                                viewModelScope.launch(Dispatchers.IO) {
                                    abrirPuerto(device)
                                }
                            }
                        }
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        ContextCompat.registerReceiver(
            context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
        Log.d(TAG, "Receiver registrado")
    }

    fun conectar() {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val device = usbManager.deviceList.values.firstOrNull()

        if (device == null) {
            _state.value = UsbSerialState(error = R.string.usb_error_not_found)
            return
        }

        // Si ya estaba conectado al mismo device, cierra primero
        if (serialDevice != null) {
            cerrarConexion()
        }

        _state.value = UsbSerialState(cargando = true)

        if (usbManager.hasPermission(device)) {
            viewModelScope.launch(Dispatchers.IO) { abrirPuerto(device) }
        } else {
            val intent = Intent(ACTION_USB_PERMISSION).apply {
                setPackage(context.packageName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_MUTABLE
            )
            usbManager.requestPermission(device, pendingIntent)
        }
    }

    private fun cerrarConexion() {
        serialDevice?.close()
        serialDevice = null
        byteBuffer.clear()
        Log.d(TAG, "Conexión cerrada")
    }

    private fun abrirPuerto(device: UsbDevice) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        val connection = try {
            usbManager.openDevice(device)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Device obsoleto: ${e.message}")
            _state.value = UsbSerialState(error = R.string.usb_error_reconnect_cable) //"Desconecta y vuelve a conectar el cable"
            return
        } ?: run {
            Log.e(TAG, "openDevice devolvió null")
            _state.value = UsbSerialState(error = R.string.usb_error_cannot_open_connection)
            return
        }

        val serial = UsbSerialDevice.createUsbSerialDevice(device, connection) ?: run {
            Log.e(TAG, "Driver no soportado VID=${device.vendorId} PID=${device.productId}")
            connection.close()
            _state.value = UsbSerialState(error = R.string.usb_error_unsupported_driver)
            return
        }

        if (!serial.open()) {
            Log.e(TAG, "serial.open() devolvió false")
            connection.close()
            _state.value = UsbSerialState(error = R.string.usb_error_cannot_open_port)
            return
        }

        serial.setBaudRate(9600)
        serial.setDataBits(UsbSerialInterface.DATA_BITS_8)
        serial.setStopBits(UsbSerialInterface.STOP_BITS_1)
        serial.setParity(UsbSerialInterface.PARITY_NONE)
        serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
        Log.d(TAG, "Puerto configurado")

        byteBuffer.clear()

        serial.read { bytes ->
            if (bytes == null || bytes.isEmpty()) return@read
            byteBuffer.addAll(bytes.toList())
            while (byteBuffer.contains('\n'.code.toByte())) {
                val idx = byteBuffer.indexOf('\n'.code.toByte())
                val lineaBytes = byteBuffer.subList(0, idx).toByteArray()
                repeat(idx + 1) { byteBuffer.removeAt(0) }
                val linea = String(lineaBytes, Charsets.UTF_8).trim()
                if (linea.isNotEmpty()) {
                    Log.d(TAG, "Línea recibida: $linea")
                    viewModelScope.launch { _mensajes.emit(linea) }
                }
            }
        }

        serialDevice = serial
        _state.value = UsbSerialState(conectado = true)
        Log.d(TAG, "Conectado y escuchando")
    }

    override fun onCleared() {
        super.onCleared()
        cerrarConexion()
        receiver?.let { context.unregisterReceiver(it) }
        receiver = null
        Log.d(TAG, "ViewModel destruido, conexión cerrada")
    }
}