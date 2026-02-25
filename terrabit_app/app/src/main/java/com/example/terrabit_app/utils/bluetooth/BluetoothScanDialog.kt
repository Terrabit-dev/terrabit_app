package com.example.terrabit_app.utils.bluetooth


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen


@Composable
fun BluetoothScanDialog(
    bluetoothViewModel: BluetoothViewModel,
    onMensajeRecibido: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val state by bluetoothViewModel.scanState.collectAsState()
    val dispositivos by bluetoothViewModel.dispositivosEmparejados.collectAsState()

    // Lanzador de solicitud de permisos
    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        val concedidos = resultados.values.all { it }
        if (concedidos) {
            bluetoothViewModel.iniciarEscaneo(context)
        }
    }

    // Cuando se recibe un mensaje, notificar a la UI y cerrar
    LaunchedEffect(state) {
        if (state is BluetoothScanState.Recibido) {
            onMensajeRecibido((state as BluetoothScanState.Recibido).mensaje)
            bluetoothViewModel.resetearEstado()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = {
            bluetoothViewModel.cancelarEscaneo()
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = state !is BluetoothScanState.Esperando
        )
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {

                    // ---- ESPERANDO ----
                    is BluetoothScanState.Esperando -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = MainGreen,
                            strokeWidth = 5.dp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Esperando identificador",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueGrey
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Acerca el lector al crotal o identificador del animal",
                            fontSize = 14.sp,
                            color = BlueGrey,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = {
                                bluetoothViewModel.cancelarEscaneo()
                                onDismiss()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueGrey)
                        ) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ---- SELECCIONAR DISPOSITIVO ----
                    is BluetoothScanState.SeleccionandoDispositivo -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Seleccionar lector",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBlueGrey
                            )
                            IconButton(onClick = {
                                bluetoothViewModel.cancelarEscaneo()
                                onDismiss()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = BlueGrey)
                            }
                        }
                        Text(
                            "Selecciona el dispositivo Arduino emparejado",
                            fontSize = 13.sp,
                            color = BlueGrey
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (dispositivos.isEmpty()) {
                            Icon(
                                Icons.Default.BluetoothDisabled,
                                contentDescription = null,
                                tint = BlueGrey,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No hay dispositivos Bluetooth emparejados.\nEmpareja el Arduino en los ajustes del sistema.",
                                fontSize = 14.sp,
                                color = BlueGrey,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                items(dispositivos) { (nombre, mac) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                bluetoothViewModel.seleccionarDispositivo(context, nombre, mac)
                                            }
                                            .padding(vertical = 14.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Bluetooth,
                                            contentDescription = null,
                                            tint = MainGreen,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                nombre,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = DarkBlueGrey
                                            )
                                            Text(
                                                mac,
                                                fontSize = 12.sp,
                                                color = BlueGrey
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = DarkWhiteBackground, thickness = 1.dp)
                                }
                            }
                        }
                    }

                    // ---- ERROR ----
                    is BluetoothScanState.Error -> {
                        val errorMsg = (state as BluetoothScanState.Error).mensaje
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(
                            Icons.Default.BluetoothDisabled,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Error de conexión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueGrey
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMsg,
                            fontSize = 14.sp,
                            color = BlueGrey,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    bluetoothViewModel.cancelarEscaneo()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueGrey)
                            ) {
                                Text("Cerrar")
                            }
                            Button(
                                onClick = { bluetoothViewModel.iniciarEscaneo(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
                            ) {
                                Text("Reintentar")
                            }
                        }
                        // Si necesita permisos, mostrar botón para pedirlos
                        if (errorMsg.contains("permiso", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    val permisos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        arrayOf(
                                            Manifest.permission.BLUETOOTH_CONNECT,
                                            Manifest.permission.BLUETOOTH_SCAN
                                        )
                                    } else {
                                        arrayOf(
                                            Manifest.permission.BLUETOOTH,
                                            Manifest.permission.BLUETOOTH_ADMIN
                                        )
                                    }
                                    permisosLauncher.launch(permisos)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MainGreen)
                            ) {
                                Text("Conceder permisos")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ---- SIN BLUETOOTH ----
                    is BluetoothScanState.SinBluetooth -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(
                            Icons.Default.BluetoothDisabled,
                            contentDescription = null,
                            tint = BlueGrey,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Bluetooth desactivado",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueGrey
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Activa el Bluetooth del dispositivo para poder leer identificadores.",
                            fontSize = 14.sp,
                            color = BlueGrey,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                bluetoothViewModel.cancelarEscaneo()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
                        ) {
                            Text("Entendido")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Idle o Recibido — no mostrar nada (el LaunchedEffect ya cerró el diálogo)
                    else -> {}
                }
            }
        }
    }
}