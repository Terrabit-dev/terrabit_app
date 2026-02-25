package com.example.terrabit_app.utils.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

//Singleton que gestiona la conexión Bluetooth clásica (SPP)



object ArduinoBluetoothManager {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") //UUID estándar de Serial Port Profile — NO cambiar.
    private const val TAG = "ArduinoBluetooth"

    private var socket: BluetoothSocket? = null

    // SharedFlow: cada String emitido es una línea completa enviada por el Arduino
    private val _mensajesFlow = MutableSharedFlow<String>(replay = 0)
    val mensajesFlow = _mensajesFlow.asSharedFlow()

    //Devuelve true si el dispositivo tiene Bluetooth y está activado.

    fun bluetoothDisponible(context: Context): Boolean {
        val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        return bm?.adapter?.isEnabled == true
    }

    //Devuelve la lista de dispositivos ya emparejados.
    //El usuario debe seleccionar el Arduino desde la UI.
    fun dispositivosEmparejados(context: Context): List<Pair<String, String>> {
        if (!tienePermisos(context)) return emptyList()
        val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bm?.adapter ?: return emptyList()
        return try {
            adapter.bondedDevices?.map { device ->
                Pair(device.name ?: "Desconocido", device.address)
            } ?: emptyList()
        } catch (e: SecurityException) {
            Log.e(TAG, "Sin permiso para listar dispositivos: ${e.message}")
            emptyList()
        }
    }

    //Conecta al dispositivo con la MAC dada y espera UN mensaje.
    //Emite el String recibido por mensajesFlow y cierra el socket.

    //@return Result.success con el string recibido, o Result.failure con el error.
    suspend fun esperarMensaje(context: Context, macAddress: String): Result<String> {
        return withContext(Dispatchers.IO) {
            if (!tienePermisos(context)) {
                return@withContext Result.failure(SecurityException("Permisos Bluetooth no concedidos"))
            }

            val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            val adapter = bm?.adapter
                ?: return@withContext Result.failure(IOException("Bluetooth no disponible"))

            try {
                // Cancelar discovery si estuviera activo (mejora la conexión)
                try { adapter.cancelDiscovery() } catch (_: SecurityException) {}

                val device = adapter.getRemoteDevice(macAddress)
                    ?: return@withContext Result.failure(IOException("Dispositivo no encontrado: $macAddress"))

                // Cerrar socket anterior si existiera
                cerrarSocket()

                socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                socket!!.connect()

                Log.d(TAG, "Conectado a $macAddress")

                // Leer hasta encontrar salto de línea (\n) o retorno (\r\n)
                val inputStream = socket!!.inputStream
                val buffer = StringBuilder()
                val byteArray = ByteArray(1024)

                var mensaje = ""
                var encontrado = false

                while (!encontrado) {
                    val bytesLeidos = inputStream.read(byteArray)
                    if (bytesLeidos > 0) {
                        buffer.append(String(byteArray, 0, bytesLeidos))
                        // El Arduino normalmente termina con \n
                        val lineaCompleta = buffer.toString().trimEnd('\n', '\r')
                        if (buffer.contains('\n') || buffer.contains('\r')) {
                            mensaje = lineaCompleta.trim()
                            encontrado = true
                        }
                    }
                }

                Log.d(TAG, "Mensaje recibido: $mensaje")
                _mensajesFlow.emit(mensaje)
                cerrarSocket()

                Result.success(mensaje)

            } catch (e: IOException) {
                Log.e(TAG, "Error de conexión: ${e.message}")
                cerrarSocket()
                Result.failure(e)
            } catch (e: SecurityException) {
                Log.e(TAG, "Error de permisos: ${e.message}")
                cerrarSocket()
                Result.failure(e)
            }
        }
    }

    fun cerrarSocket() {
        try {
            socket?.close()
        } catch (_: IOException) {}
        socket = null
    }

    //Comprueba los permisos Bluetooth según la versión de Android.
    //Android 12+ requiere BLUETOOTH_CONNECT.
    //Android <12 requiere BLUETOOTH.
    fun tienePermisos(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}