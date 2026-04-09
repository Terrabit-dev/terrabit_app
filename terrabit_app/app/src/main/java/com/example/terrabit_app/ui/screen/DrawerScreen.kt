package com.example.terrabit_app.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.NavigationDrawer
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.viewmodel.bovinos.DrawerViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DrawerScreen(
    bluetooth: BluetoothViewModel,
    mainNavController: androidx.navigation.NavController,
    drawerViewModel: DrawerViewModel
) {
    val drawerNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tipoAnimalSeleccionado by drawerViewModel.tipoAnimalSeleccionado.observeAsState("Bovinos")
    val currentRoute by drawerNavController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                tipoSeleccionado = tipoAnimalSeleccionado,
                currentRoute = currentDestination,
                onTipoSeleccionado = { tipo ->
                    drawerViewModel.seleccionarTipoAnimal(tipo)
                    when (tipo) {
                        "Bovinos" -> drawerNavController.navigate(Routes.HomeBovinos.route) {
                            popUpTo(Routes.HomeBovinos.route) { inclusive = true }
                        }
                        "Porcinos" -> drawerNavController.navigate(Routes.HomePorcinos.route) {
                            popUpTo(Routes.HomePorcinos.route) { inclusive = true }
                        }
                    }
                    scope.launch { drawerState.close() }
                },
                onBorradoresClick = {
                    drawerNavController.navigate("borradores") {
                        popUpTo(drawerNavController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                    scope.launch { drawerState.close() }
                },
                onHistorialClick = {
                    drawerNavController.navigate("historial") {
                        popUpTo(drawerNavController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                    scope.launch { drawerState.close() }
                },
                onConfigClick = {
                    drawerNavController.navigate(Routes.Configuration.route) {
                        popUpTo(drawerNavController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    drawerViewModel.logout {
                        mainNavController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    ) {
        NavigationDrawer(
            bluetooth = bluetooth,
            navController = drawerNavController,
            onMenuClick = { scope.launch { drawerState.open() } }
        )
    }
}

@Composable
fun DrawerContent(
    tipoSeleccionado: String,
    currentRoute: String?,
    onTipoSeleccionado: (String) -> Unit,
    onBorradoresClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onConfigClick: () -> Unit,
    onLogout: () -> Unit
) {
    val colorPrincipal = if (tipoSeleccionado == "Porcinos") MainOrange else MainGreen

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text("Terrabit", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorPrincipal)
                Text(
                    stringResource(R.string.drawer_subtitle),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.drawer_explained),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            OpcionDrawer(
                icono = Icons.Default.Agriculture,
                titulo = stringResource(R.string.bovinos_name),
                seleccionado = tipoSeleccionado == "Bovinos" && currentRoute == Routes.HomeBovinos.route,
                onClick = { onTipoSeleccionado("Bovinos") },
                colorSeleccion = MainGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            OpcionDrawer(
                icono = Icons.Default.EmojiNature,
                titulo = stringResource(R.string.porcionos_name),
                seleccionado = tipoSeleccionado == "Porcinos" && currentRoute == Routes.HomePorcinos.route,
                onClick = { onTipoSeleccionado("Porcinos") },
                colorSeleccion = MainOrange
            )

            Spacer(modifier = Modifier.height(8.dp))

            OpcionDrawer(
                icono = Icons.Default.Drafts,
                titulo = stringResource(R.string.draft_name),
                seleccionado = currentRoute == "borradores",
                onClick = onBorradoresClick,
                colorSeleccion = colorPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            OpcionDrawer(
                icono = Icons.Default.History,
                titulo = stringResource(R.string.history_name),
                seleccionado = currentRoute == "historial",
                onClick = onHistorialClick,
                colorSeleccion = colorPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            OpcionDrawer(
                icono = Icons.Default.Settings,
                titulo = stringResource(R.string.configuration_title),
                seleccionado = currentRoute == Routes.Configuration.route,
                onClick = onConfigClick,
                colorSeleccion = colorPrincipal
            )

            Spacer(modifier = Modifier.weight(1f))

            OpcionDrawer(
                icono = Icons.AutoMirrored.Filled.ExitToApp,
                titulo = stringResource(R.string.close_session_title),
                seleccionado = false,
                onClick = onLogout,
                colorSeleccion = colorPrincipal
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${stringResource(R.string.version_title)} 2.61.2", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("© 2026 Terrabit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun OpcionDrawer(
    icono: ImageVector,
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    colorSeleccion: Color
) {
    val backgroundColor = if (seleccionado) colorSeleccion.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (seleccionado) colorSeleccion else MaterialTheme.colorScheme.onSurface
    val iconColor = if (seleccionado) colorSeleccion else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = titulo, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(titulo, fontSize = 15.sp, fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal, color = textColor)
            Spacer(modifier = Modifier.weight(1f))
            if (seleccionado) {
                Icon(Icons.Default.Check, contentDescription = "Seleccionado", tint = colorSeleccion, modifier = Modifier.size(20.dp))
            }
        }
    }
}