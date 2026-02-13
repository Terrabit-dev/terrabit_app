package com.example.terrabit_app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.MainViewmodel
import kotlinx.coroutines.launch

@Composable
fun DrawerScreen(
    mainNavController: androidx.navigation.NavController,
    drawerViewModel: DrawerViewModel,
    mainViewModel: MainViewmodel
) {
    val drawerNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tipoAnimalSeleccionado by drawerViewModel.tipoAnimalSeleccionado.observeAsState("Bovinos")
    val currentRoute by drawerNavController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    // Modal Navigation Drawer (sin TopAppBar negro)
    ModalNavigationDrawer(
        gesturesEnabled = false, // Deshabilitamos gestos para evitar conflictos
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                tipoSeleccionado = tipoAnimalSeleccionado,
                currentRoute = currentDestination,
                onTipoSeleccionado = { tipo ->
                    drawerViewModel.seleccionarTipoAnimal(tipo)
                    when(tipo) {
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
                onLogout = {
                    // Navegar de vuelta al login
                    mainNavController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    ) {
        // Llamar directamente a NavigationDrawer sin Scaffold/TopAppBar
        NavigationDrawer(
            myViewmodel = mainViewModel,
            drawerViewModel = drawerViewModel,
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
    onLogout: () -> Unit
) {
    // Color dinámico según el tipo de animal
    val colorPrincipal = when(tipoSeleccionado) {
        "Porcinos" -> MainOrange
        else -> MainGreen
    }

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header del drawer con color dinámico
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    "Terrabit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorPrincipal  // ← Color dinámico
                )
                Text(
                    stringResource(R.string.drawer_subtitle),
                    fontSize = 14.sp,
                    color = BlueGrey,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(color = DarkWhiteBackground)

            Spacer(modifier = Modifier.height(24.dp))

            // Título de sección
            Text(
                stringResource(R.string.drawer_explained),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlueGrey,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            // Opción Bovinos
            OpcionDrawer(
                icono = Icons.Default.Agriculture,
                titulo = stringResource(R.string.bovinos_name),
                seleccionado = tipoSeleccionado == "Bovinos" && currentRoute == Routes.HomeBovinos.route,
                onClick = { onTipoSeleccionado("Bovinos") },
                colorSeleccion = MainGreen  // Verde para Bovinos
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Porcinos
            OpcionDrawer(
                icono = Icons.Default.EmojiNature,
                titulo = stringResource(R.string.porcionos_name),
                seleccionado = tipoSeleccionado == "Porcinos" && currentRoute == Routes.HomePorcinos.route,
                onClick = { onTipoSeleccionado("Porcinos") },
                colorSeleccion = MainOrange  // Naranja para Porcinos
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Borradores (usa el color del tipo actual)
            OpcionDrawer(
                icono = Icons.Default.Drafts,
                titulo = stringResource(R.string.draft_name),
                seleccionado = currentRoute == "borradores",
                onClick = onBorradoresClick,
                colorSeleccion = colorPrincipal
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Logout
            OpcionDrawer(
                icono = Icons.AutoMirrored.Filled.ExitToApp,
                titulo = "Cerrar sesión",
                seleccionado = false,
                onClick = onLogout,
                colorSeleccion = colorPrincipal
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = WhiteBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Versión 1.0.0",
                        fontSize = 12.sp,
                        color = DarkBlueGrey
                    )
                    Text(
                        "© 2026 Terrabit",
                        fontSize = 10.sp,
                        color = BlueGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
    colorSeleccion: Color  // ← Color específico para esta opción
) {
    val backgroundColor = if (seleccionado) colorSeleccion.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (seleccionado) colorSeleccion else DarkBlueGrey
    val iconColor = if (seleccionado) colorSeleccion else BlueGrey

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
            Icon(
                icono,
                contentDescription = titulo,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                titulo,
                fontSize = 15.sp,
                fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))
            if (seleccionado) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = colorSeleccion,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
