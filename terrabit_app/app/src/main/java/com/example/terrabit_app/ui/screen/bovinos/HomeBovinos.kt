package com.example.terrabit_app.ui.screen.bovinos

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.MintCreamGreen
import com.example.terrabit_app.ui.theme.WhiteBackground
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun Home(
    tipoAnimalSeleccionado: String,
    onMenuClick: () -> Unit,
    navController: NavController
) {


    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

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

    // Comprueba si el Bluetooth está activado al entrar a la pantalla
    LaunchedEffect(Unit) {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter


        val permisoOk = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoOk && bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcherBluetooth.launch(intent)
        }
    }



    LaunchedEffect(Unit) {
        launcherPermiso.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
    }

    // Diálogo manual si el usuario rechazó el popup del sistema
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
                }) {
                    Text("Activar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    Scaffold(
        containerColor = WhiteBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con gradiente y bienvenida
            HeaderBienvenida(
                tipoAnimal = tipoAnimalSeleccionado,
                onMenuClick = onMenuClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título de sección
            Text(
                stringResource(R.string.subtitle_home),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlueGrey,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de tarjetas organizadoras
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Tarjeta Listado de Bovinos
                TarjetaMenu(
                    icono = Icons.Default.List,
                    titulo = stringResource(R.string.list_bovinos),
                    descripcion = stringResource(R.string.list_bovinos_subtitle),
                    colorFondo = Color(0xFFE28F41),
                    onClick = { navController.navigate(Routes.ListarBovinos.route) }
                )

                // Tarjeta Gestión de Bovinos
                TarjetaMenu(
                    icono = Icons.Default.Agriculture,
                    titulo = stringResource(R.string.card_name_animals),
                    descripcion = stringResource(R.string.card_description_animals),
                    colorFondo = MainGreen,
                    onClick = { navController.navigate(Routes.GestionBovinos.route) }
                )

                // Tarjeta Guías/Movimientos
                TarjetaMenu(
                    icono = Icons.Default.LocalShipping,
                    titulo = stringResource(R.string.card_name_guias),
                    descripcion = stringResource(R.string.card_description_guias),
                    colorFondo = MainOrange,
                    contadorBadge = 2,
                    onClick = { navController.navigate(Routes.GuiasMovimientos.route) }
                )

                // Tarjeta Material
                TarjetaMenu(
                    icono = Icons.Default.ShoppingCart,
                    titulo = stringResource(R.string.card_name_material),
                    descripcion = stringResource(R.string.card_description_material),
                    colorFondo = MainGreen,
                    onClick = { navController.navigate(Routes.MaterialCategoria.route) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card de Información del Sistema
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MintCreamGreen
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MainGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MainGreen,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.information_title_home),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MainGreen,
                            letterSpacing = 0.2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            stringResource(R.string.information_description_home),
                            fontSize = 14.sp,
                            color = BlueGrey,
                            lineHeight = 20.sp,
                            letterSpacing = 0.1.sp
                        )
                    }
                }
            }

            // Espaciado inferior
            Spacer(modifier = Modifier.height(24.dp))
        }
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con badge
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorFondo,
                    modifier = Modifier.size(70.dp),
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        icono,
                        contentDescription = titulo,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }

                // Badge de notificación
                if (contadorBadge != null) {
                    Badge(
                        containerColor = ErrorRed,
                        modifier = Modifier
                            .offset(x = 4.dp, y = (-4).dp)
                    ) {
                        Text(
                            contadorBadge.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Textos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = DarkBlueGrey,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    descripcion,
                    fontSize = 14.sp,
                    color = BlueGrey,
                    lineHeight = 18.sp
                )
            }

            // Icono flecha
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver más",
                tint = BlueGrey,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// Función mágica que cambia el idioma
fun cambiarIdioma(codigoIdioma: String) {
    val appLocale = LocaleListCompat.forLanguageTags(codigoIdioma)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@Composable
fun HeaderBienvenida(
    tipoAnimal: String,
    onMenuClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var botonActivo by remember { mutableStateOf(false) }

    LaunchedEffect(botonActivo) {
        kotlinx.coroutines.delay(10000)
        botonActivo = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(color = MainGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Barra superior con menú y notificaciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de menú
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Botón de configuración
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White
                        )
                    }

                    // Menú Desplegable
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Castellano") },
                            onClick = {
                                expanded = false
                                cambiarIdioma("es")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Català") },
                            onClick = {
                                expanded = false
                                cambiarIdioma("ca")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto de bienvenida
            Text(
                stringResource(R.string.title_home),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tipo de animal seleccionado
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        tipoAnimal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
