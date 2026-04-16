// utils/bluetooth/BluetoothUtils.kt
package com.example.terrabit_app.utils.bluetooth

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

// Dialogo de Activacion de Bluetooth al usuario

object BluetoothUtils {

    private const val PREFS_BLUETOOTH = "bluetooth_prefs"
    private const val KEY_BT_CANCELADO = "bt_cancelado"

    fun deberiasPedirActivar(context: Context): Boolean {
        val cancelado = context
            .getSharedPreferences(PREFS_BLUETOOTH, Context.MODE_PRIVATE)
            .getBoolean(KEY_BT_CANCELADO, false)
        if (cancelado) return false

        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter ?: return false

        val permisoOk = ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        return permisoOk && !adapter.isEnabled
    }

    fun marcarCancelado(context: Context) {
        context.getSharedPreferences(PREFS_BLUETOOTH, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_BT_CANCELADO, true).apply()
    }
}