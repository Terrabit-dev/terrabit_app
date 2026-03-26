package com.example.terrabit_app.ui.pantallas

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.components.TarjetaAccion
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.bluetooth.BluetoothUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiasMovimientos(navController: NavController) {
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

    val launcherBluetooth = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    LaunchedEffect(Unit) {
        if (BluetoothUtils.deberiasPedirActivar(context)) {
            mostrarDialogo = true
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                BluetoothUtils.marcarCancelado(context)
            },
            title = { Text("Bluetooth desactivado") },
            text = { Text("La app necesita Bluetooth para comunicarse con el ESP32. ¿Quieres activarlo?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    launcherBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                }) { Text("Activar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    BluetoothUtils.marcarCancelado(context)
                }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.name_guias_movimientos),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HomeBovinos.route) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainOrange)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.subtitle_guias_movimientos),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Create,
                        titulo = stringResource(R.string.action_create_guide),
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.GestionGuias.nuevo()) }
                    )
                    TarjetaAccion(
                        icono = Icons.Default.Send,
                        titulo = stringResource(R.string.action_confirm_movement),
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Movimientos.nuevo()) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Checklist,
                        titulo = "Lista Guias",
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.GuiasBovinos.route) }
                    )
                    TarjetaAccion(
                        icono = Icons.Default.Checklist,
                        titulo = "Lista Movimientos",
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.MovimientosBovinos.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}