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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.terrabit_app.ui.components.TarjetaAccion
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.bluetooth.BluetoothUtils
import com.example.terrabit_app.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialCategoria(navController: NavController) {
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
            title = { stringResource(R.string.bluetooth_disabled_title) },
            text = { stringResource(R.string.bluetooth_description_activation_menu) },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    launcherBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                }) { stringResource(R.string.bluetooth_activate_button) }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    BluetoothUtils.marcarCancelado(context)
                }) { stringResource(R.string.cancel_buttom) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HomeBovinos.route) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainGreen)
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
                stringResource(R.string.gestion_subtitle_bovinos),
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
                        icono = Icons.Default.ShoppingCart,
                        titulo = stringResource(R.string.name_request_material),
                        subtitulo = "",
                        colorFondo = MainGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Material.nuevo()) }
                    )
                    TarjetaAccion(
                        icono = Icons.Default.ContentCopy,
                        titulo = stringResource(R.string.btn_duplicate_request),
                        subtitulo = "",
                        colorFondo = MainGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.MaterialDuplicado.nuevo()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}