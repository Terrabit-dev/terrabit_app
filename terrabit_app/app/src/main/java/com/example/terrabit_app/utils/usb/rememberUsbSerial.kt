package com.example.terrabit_app.utils.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val ACTION_USB_PERMISSION = "com.example.terrabit_app.USB_PERMISSION"
private const val TAG = "USB_DEBUG"

data class UsbSerialState(
    val conectado: Boolean = false,
    val error: String? = null,
    val cargando: Boolean = false,
    val dispositivoDetectado: Boolean = false
)

@Composable
fun rememberUsbSerial(
    onMensaje: (String) -> Unit
): Pair<UsbSerialState, () -> Unit> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var state by remember { mutableStateOf(UsbSerialState()) }
    var serialDevice by remember { mutableStateOf<UsbSerialDevice?>(null) }
    // Siempre guardamos el device más reciente aquí
    var deviceActual by remember { mutableStateOf<UsbDevice?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {

                    // ── Dispositivo conectado físicamente ──────────────────
                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                            ?: return
                        Log.d(TAG, "Dispositivo conectado: ${device.deviceName}")
                        deviceActual = device
                        state = UsbSerialState(dispositivoDetectado = true)
                        // Solicita permiso automáticamente al conectar el cable
                        solicitarPermiso(ctx, device)
                    }

                    // ── Dispositivo desconectado físicamente ───────────────
                    UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        Log.d(TAG, "Dispositivo desconectado: ${device?.deviceName}")
                        serialDevice?.close()
                        serialDevice = null
                        deviceActual = null
                        state = UsbSerialState()
                    }

                    // ── Respuesta al diálogo de permiso ────────────────────
                    ACTION_USB_PERMISSION -> {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        val granted = intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false
                        )
                        Log.d(TAG, "Permiso: granted=$granted device=${device?.deviceName}")

                        if (device == null) {
                            state = UsbSerialState(error = "Dispositivo no encontrado")
                            return
                        }
                        if (!granted) {
                            state = UsbSerialState(error = "Permiso denegado")
                            return
                        }

                        // Actualizamos deviceActual con el device fresco del intent
                        deviceActual = device
                        state = UsbSerialState(cargando = true)

                        scope.launch(Dispatchers.IO) {
                            abrirPuertoUsb(
                                context = ctx,
                                device = device,
                                scope = scope,
                                onConectado = { serial ->
                                    serialDevice = serial
                                    state = UsbSerialState(conectado = true)
                                },
                                onMensaje = onMensaje,
                                onError = { err ->
                                    state = UsbSerialState(error = err)
                                }
                            )
                        }
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        ContextCompat.registerReceiver(
            context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )

        // Comprueba si ya hay un dispositivo conectado al entrar en la pantalla
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        usbManager.deviceList.values.firstOrNull()?.let { device ->
            Log.d(TAG, "Dispositivo ya conectado al iniciar: ${device.deviceName}")
            deviceActual = device
            state = UsbSerialState(dispositivoDetectado = true)
        }

        onDispose {
            context.unregisterReceiver(receiver)
            serialDevice?.close()
        }
    }

    // Función que llama el botón — usa siempre el device más reciente
    val conectar: () -> Unit = {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val device = usbManager.deviceList.values.firstOrNull()

        if (device == null) {
            state = UsbSerialState(error = "No hay dispositivo USB conectado")
        } else {
            deviceActual = device
            state = UsbSerialState(cargando = true)
            solicitarPermiso(context, device)
        }
    }

    return state to conectar
}

private fun solicitarPermiso(context: Context, device: UsbDevice) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    // Si ya tiene permiso no hace falta pedir el diálogo
    if (usbManager.hasPermission(device)) {
        Log.d(TAG, "Ya tiene permiso, no se muestra diálogo")
        val intent = Intent(ACTION_USB_PERMISSION).apply {
            setPackage(context.packageName)
        }
        // Simula el broadcast de permiso concedido manualmente
        intent.putExtra(UsbManager.EXTRA_DEVICE, device)
        intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true)
        context.sendBroadcast(intent)
        return
    }

    val intent = Intent(ACTION_USB_PERMISSION).apply {
        setPackage(context.packageName)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_MUTABLE
    )
    usbManager.requestPermission(device, pendingIntent)
    Log.d(TAG, "Permiso solicitado para: ${device.deviceName}")
}

private fun abrirPuertoUsb(
    context: Context,
    device: UsbDevice,
    scope: CoroutineScope,
    onConectado: (UsbSerialDevice) -> Unit,
    onMensaje: (String) -> Unit,
    onError: (String) -> Unit
) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    Log.d(TAG, "Abriendo: ${device.deviceName} VID=${device.vendorId} PID=${device.productId}")
    Log.d(TAG, "¿Tiene permiso?: ${usbManager.hasPermission(device)}")

    // Captura la excepción que ocurre cuando el device ya no existe
    val connection = try {
        usbManager.openDevice(device)
    } catch (e: IllegalArgumentException) {
        Log.e(TAG, "Device obsoleto o restringido: ${e.message}")
        return onError("Desconecta y vuelve a conectar el cable")
    }

    if (connection == null) {
        Log.e(TAG, "openDevice devolvió null")
        return onError("No se pudo abrir la conexión")
    }
    Log.d(TAG, "Conexión abierta — fd=${connection.fileDescriptor}")

    val serial = UsbSerialDevice.createUsbSerialDevice(device, connection)
    if (serial == null) {
        Log.e(TAG, "Driver no soportado VID=${device.vendorId} PID=${device.productId}")
        connection.close()
        return onError("Driver no soportado para este dispositivo")
    }

    if (!serial.open()) {
        Log.e(TAG, "serial.open() devolvió false")
        connection.close()
        return onError("No se pudo abrir el puerto serie")
    }

    serial.setBaudRate(9600)
    serial.setDataBits(UsbSerialInterface.DATA_BITS_8)
    serial.setStopBits(UsbSerialInterface.STOP_BITS_1)
    serial.setParity(UsbSerialInterface.PARITY_NONE)
    serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
    Log.d(TAG, "Puerto configurado 115200 8N1")

    val byteBuffer = mutableListOf<Byte>()
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
                scope.launch(Dispatchers.Main) { onMensaje(linea) }
            }
        }
    }

    Log.d(TAG, "Escucha activa → onConectado")
    scope.launch(Dispatchers.Main) { onConectado(serial) }
}