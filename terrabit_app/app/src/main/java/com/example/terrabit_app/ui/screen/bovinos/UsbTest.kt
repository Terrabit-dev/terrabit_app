package com.example.terrabit_app.ui.screen.bovinos

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ACTION_USB_PERMISSION = "com.example.terrabit_app.USB_PERMISSION"

@Composable
fun UsbTestScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var estadoConexion by remember { mutableStateOf("Sin conectar") }
    var mensajesRecibidos by remember { mutableStateOf("") }
    var serialDevice by remember { mutableStateOf<UsbSerialDevice?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                Log.d("USB_DEBUG", "BroadcastReceiver disparado: ${intent.action}")
                if (intent.action != ACTION_USB_PERMISSION) return

                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                Log.d("USB_DEBUG", "granted=$granted device=${device?.deviceName}")

                if (device == null) {
                    Log.e("USB_DEBUG", "EXTRA_DEVICE es null")
                    return
                }
                if (!granted) {
                    Log.w("USB_DEBUG", "Permiso denegado por el usuario")
                    return
                }

                estadoConexion = "Permiso concedido — abriendo puerto..."
                scope.launch(Dispatchers.IO) {
                    abrirPuertoUsb(
                        context = context,
                        device = device,
                        onConectado = { serial ->
                            serialDevice = serial
                            estadoConexion = "✅ Conectado por USB"
                        },
                        onMensaje = { msg ->
                            mensajesRecibidos = "$msg\n$mensajesRecibidos"
                        },
                        onError = { err ->
                            estadoConexion = "❌ Error: $err"
                        }
                    )
                }
            }
        }

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        ContextCompat.registerReceiver(
            context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose { context.unregisterReceiver(receiver) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Test USB OTG", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Estado", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(estadoConexion, fontSize = 14.sp)
            }
        }

        Button(
            onClick = {
                estadoConexion = "Buscando dispositivo USB..."
                solicitarPermiso(context, onNoEncontrado = {
                    estadoConexion = "⚠️ No se encontró ningún dispositivo USB"
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Conectar por USB")
        }

        OutlinedButton(
            onClick = {
                serialDevice?.close()
                serialDevice = null
                estadoConexion = "Desconectado"
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = serialDevice != null
        ) {
            Text("Desconectar")
        }

        Text("Mensajes recibidos:", fontWeight = FontWeight.SemiBold)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (mensajesRecibidos.isEmpty()) {
                    Text(
                        "Aquí aparecerán los mensajes del ESP32...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(mensajesRecibidos, fontSize = 13.sp)
                }
            }
        }

        TextButton(
            onClick = { mensajesRecibidos = "" },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Limpiar")
        }
    }
}

private fun solicitarPermiso(context: Context, onNoEncontrado: () -> Unit) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val deviceList = usbManager.deviceList

    Log.d("USB_DEBUG", "Dispositivos encontrados: ${deviceList.size}")
    deviceList.forEach { (name, dev) ->
        Log.d("USB_DEBUG", "  -> $name VID=${dev.vendorId} PID=${dev.productId}")
    }

    if (deviceList.isEmpty()) {
        onNoEncontrado()
        return
    }

    val device = deviceList.values.first()
    Log.d("USB_DEBUG", "Solicitando permiso para: ${device.deviceName}")

    // Intent explícito con setPackage — obligatorio en Android 12+ para recibir UsbDevice
    val intent = Intent(ACTION_USB_PERMISSION).apply {
        setPackage(context.packageName)  // explícito → evita el warning
    }
    val permissionIntent = PendingIntent.getBroadcast(
        context, 0, intent,
        PendingIntent.FLAG_MUTABLE  // mutable → el sistema puede añadir EXTRA_DEVICE
    )
    usbManager.requestPermission(device, permissionIntent)
    Log.d("USB_DEBUG", "requestPermission lanzado")
}

private fun abrirPuertoUsb(
    context: Context,
    device: UsbDevice,
    onConectado: (UsbSerialDevice) -> Unit,
    onMensaje: (String) -> Unit,
    onError: (String) -> Unit
) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    Log.d("USB_DEBUG", "Abriendo: ${device.deviceName} VID=${device.vendorId} PID=${device.productId}")
    Log.d("USB_DEBUG", "¿Tiene permiso?: ${usbManager.hasPermission(device)}")

    val connection = usbManager.openDevice(device)
    if (connection == null) {
        Log.e("USB_DEBUG", "openDevice devolvió null")
        return onError("No se pudo abrir la conexión")
    }
    Log.d("USB_DEBUG", "Conexión abierta — fd=${connection.fileDescriptor}")

    val serial = UsbSerialDevice.createUsbSerialDevice(device, connection)
    if (serial == null) {
        Log.e("USB_DEBUG", "Driver no soportado para VID=${device.vendorId} PID=${device.productId}")
        connection.close()
        return onError("Driver no soportado para este dispositivo")
    }
    Log.d("USB_DEBUG", "Serial creado: ${serial.javaClass.simpleName}")

    if (!serial.open()) {
        Log.e("USB_DEBUG", "serial.open() devolvió false")
        connection.close()
        return onError("No se pudo abrir el puerto serie")
    }

    serial.setBaudRate(115200)
    serial.setDataBits(UsbSerialInterface.DATA_BITS_8)
    serial.setStopBits(UsbSerialInterface.STOP_BITS_1)
    serial.setParity(UsbSerialInterface.PARITY_NONE)
    serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
    Log.d("USB_DEBUG", "Puerto configurado 115200 8N1")

    val buffer = StringBuilder()
    serial.read { bytes ->
        if (bytes == null || bytes.isEmpty()) return@read
        val texto = String(bytes, Charsets.UTF_8)
        Log.v("USB_DEBUG", "Recibido (${bytes.size} bytes): $texto")
        buffer.append(texto)
        while (buffer.contains('\n')) {
            val idx = buffer.indexOf('\n')
            val linea = buffer.substring(0, idx).trim()
            buffer.delete(0, idx + 1)
            if (linea.isNotEmpty()) {
                Log.d("USB_DEBUG", "Línea: $linea")
                onMensaje(linea)
            }
        }
    }

    Log.d("USB_DEBUG", "Escucha activa → onConectado")
    onConectado(serial)
}