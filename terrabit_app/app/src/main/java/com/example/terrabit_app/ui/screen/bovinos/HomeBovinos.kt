package com.example.terrabit_app.ui.screen.bovinos

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.PantallaCargaIdioma
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(
    tipoAnimalSeleccionado: String,
    onMenuClick: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }
    var cambiandoIdioma by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val launcherBluetooth = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != android.app.Activity.RESULT_OK) {
            mostrarDialogo = true
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                launcherBluetooth.launch(intent)
            }
        }
    }

    LaunchedEffect(Unit) {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter
        val permisoOk = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
        if (permisoOk && bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcherBluetooth.launch(intent)
        }
    }

    LaunchedEffect(Unit) {
        launcherPermiso.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Bluetooth desactivado") },
            text = { Text("La app necesita Bluetooth para comunicarse con el ESP32. ¿Quieres activarlo?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    launcherBluetooth.launch(intent)
                }) { Text("Activar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderBienvenida(
                    tipoAnimal = tipoAnimalSeleccionado,
                    onMenuClick = onMenuClick,
                    onCambiarIdioma = { idioma ->
                        val localeActual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                        if (!localeActual.startsWith(idioma)) {
                            scope.launch {
                                cambiandoIdioma = true
                                delay(300)
                                cambiarIdioma(idioma)
                                delay(300)
                                cambiandoIdioma = false
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    stringResource(R.string.subtitle_home),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaMenu(
                        icono = Icons.Default.List,
                        titulo = stringResource(R.string.list_bovinos),
                        descripcion = stringResource(R.string.list_bovinos_subtitle),
                        colorFondo = Color(0xFFE28F41),
                        onClick = { navController.navigate(Routes.ListarBovinos.route) }
                    )
                    TarjetaMenu(
                        icono = Icons.Default.Agriculture,
                        titulo = stringResource(R.string.card_name_animals),
                        descripcion = stringResource(R.string.card_description_animals),
                        colorFondo = MainGreen,
                        onClick = { navController.navigate(Routes.GestionBovinos.route) }
                    )
                    TarjetaMenu(
                        icono = Icons.Default.LocalShipping,
                        titulo = stringResource(R.string.card_name_guias),
                        descripcion = stringResource(R.string.card_description_guias),
                        colorFondo = MainOrange,
                        onClick = { navController.navigate(Routes.GuiasMovimientos.route) }
                    )
                    TarjetaMenu(
                        icono = Icons.Default.ShoppingCart,
                        titulo = stringResource(R.string.card_name_material),
                        descripcion = stringResource(R.string.card_description_material),
                        colorFondo = MainGreen,
                        onClick = { navController.navigate(Routes.MaterialCategoria.route) }
                    )
                }

                Spacer(modifier = Modifier.height(45.dp))



            }
        }

        PantallaCargaIdioma(visible = cambiandoIdioma)
    }
}

@Composable
fun TarjetaMenu(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    colorFondo: Color,
    contadorBadge: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorFondo,
                    modifier = Modifier.size(70.dp),
                    shadowElevation = 2.dp
                ) {
                    Icon(icono, contentDescription = titulo, tint = Color.White, modifier = Modifier.fillMaxSize().padding(16.dp))
                }
                if (contadorBadge != null) {
                    Badge(containerColor = ErrorRed, modifier = Modifier.offset(x = 4.dp, y = (-4).dp)) {
                        Text(contadorBadge.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.2.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(descripcion, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver más", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
        }
    }
}

fun cambiarIdioma(codigoIdioma: String) {
    val appLocale = LocaleListCompat.forLanguageTags(codigoIdioma)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@Composable
fun HeaderBienvenida(
    tipoAnimal: String,
    onMenuClick: () -> Unit,
    onCambiarIdioma: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(color = MainGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                }

                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Idioma", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Castellano") },
                            onClick = { expanded = false; onCambiarIdioma("es") }
                        )
                        DropdownMenuItem(
                            text = { Text("Català") },
                            onClick = { expanded = false; onCambiarIdioma("ca") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.title_home), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tipoAnimal, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}