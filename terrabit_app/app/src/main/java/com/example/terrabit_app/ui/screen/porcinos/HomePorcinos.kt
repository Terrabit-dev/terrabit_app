package com.example.terrabit_app.ui.screen.porcinos

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.TarjetaMenu
import com.example.terrabit_app.viewmodel.DrawerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePorcinos(
    navController: NavController,
    drawerViewModel: DrawerViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tipoAnimalSeleccionado by drawerViewModel.tipoAnimalSeleccionado.observeAsState(
        stringResource(R.string.bovinos_name)
    )

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContentPorcinos(
                navController = navController,
                tipoSeleccionado = tipoAnimalSeleccionado,
                onTipoSeleccionado = { tipo ->
                    drawerViewModel.seleccionarTipoAnimal(tipo)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF5F7FA)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con gradiente y bienvenida
                HeaderBienvenidaPorcinos(
                    tipoAnimal = tipoAnimalSeleccionado,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Título de sección
                Text(
                    text = stringResource(R.string.subtitle_home),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
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
                    // Tarjeta Crear Guias
                    TarjetaMenu(
                        icono = Icons.Default.Agriculture,
                        titulo = stringResource(R.string.card_crear_guias),
                        descripcion = stringResource(R.string.card_description_crear_guias_porcinos),
                        colorFondo = Color(0xFFE28F41),
                        onClick = { navController.navigate(Routes.GestionPorcinos.route) }
                    )

                    // Tarjeta Guías/Movimientos
                    TarjetaMenu(
                        icono = Icons.Default.LocalShipping,
                        titulo = stringResource(R.string.card_name_guias),
                        descripcion = stringResource(R.string.card_description_guias),
                        colorFondo = Color(0xFF3F8F6B),
                        contadorBadge = 2,
                        onClick = { navController.navigate(Routes.GuiasMovimientosPorcinos.route) }
                    )

                    // Tarjeta Material
                    TarjetaMenu(
                        icono = Icons.Default.ShoppingCart,
                        titulo = stringResource(R.string.card_name_material),
                        descripcion = stringResource(R.string.card_description_material),
                        colorFondo = Color(0xFFE28F41),
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
                        containerColor = Color(0xFFE8F5E9)
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
                            color = Color(0xFF4A7C59).copy(alpha = 0.15f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFE28F41),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.information_title_home),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFBA7A3D),
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.information_description_home),
                                fontSize = 14.sp,
                                color = Color(0xFF475569),
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
}

@Composable
fun DrawerContentPorcinos(
    navController: NavController,
    tipoSeleccionado: String,
    onTipoSeleccionado: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header del drawer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE28F41)
                )
                Text(
                    stringResource(R.string.drawer_subtitle),
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            Spacer(modifier = Modifier.height(24.dp))

            // Título de sección
            Text(
                "Tipo de Animal",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            // Opción Bovinos
            OpcionTipoAnimalPorcinos(
                icono = Icons.Default.Agriculture,
                titulo = stringResource(R.string.bovinos_name),
                seleccionado = tipoSeleccionado == stringResource(R.string.bovinos_name),
                onClick = {
                    onTipoSeleccionado("Bovinos")
                    navController.navigate(Routes.HomeBovinos.route)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Porcinos
            OpcionTipoAnimalPorcinos(
                icono = Icons.Default.EmojiNature,
                titulo = stringResource(R.string.porcionos_name),
                seleccionado = tipoSeleccionado == stringResource(R.string.porcionos_name),
                onClick = { onTipoSeleccionado("Porcinos") }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1F5F9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_version),
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = stringResource(R.string.app_copyright),
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OpcionTipoAnimalPorcinos(
    icono: ImageVector,
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (seleccionado) Color(0xFFE28F41).copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (seleccionado) Color(0xFFE28F41) else Color(0xFF64748B)
    val iconColor = if (seleccionado) Color(0xFFE28F41) else Color(0xFF94A3B8)

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
                    contentDescription = stringResource(R.string.content_description_selecionado),
                    tint = Color(0xFF4A7C59),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderBienvenidaPorcinos(
    tipoAnimal: String,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBA7A3D),
                        Color(0xFFE28F41)
                    )
                )
            )
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
                        contentDescription = stringResource(R.string.content_description_menu),
                        tint = Color.White
                    )
                }

                // Notificaciones
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { /* Notificaciones */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.content_description_notificaciones),
                            tint = Color.White
                        )
                    }

                    Badge(
                        containerColor = Color(0xFFFF5252),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                    ) {
                        Text(
                            "3",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto de bienvenida
            Text(
                text = stringResource(R.string.title_home),
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