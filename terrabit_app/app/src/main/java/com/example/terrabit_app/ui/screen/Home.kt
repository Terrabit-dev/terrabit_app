package com.example.terrabit_app.ui.pantallas

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.viewmodel.DrawerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    drawerViewModel: DrawerViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tipoAnimalSeleccionado by drawerViewModel.tipoAnimalSeleccionado.observeAsState("Bovinos")

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
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
                HeaderBienvenida(
                    tipoAnimal = tipoAnimalSeleccionado,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Título de sección
                Text(
                    "Menú Principal",
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
                    // Tarjeta Gestión de Bovinos
                    TarjetaMenu(
                        icono = Icons.Default.Agriculture,
                        titulo = "Gestión de Bovinos",
                        descripcion = "Registrar nacimientos y reportar muertes",
                        colorFondo = Color(0xFF4A7C59),
                        onClick = { navController.navigate(Routes.GestionBovinos.route) }
                    )

                    // Tarjeta Guías/Movimientos
                    TarjetaMenu(
                        icono = Icons.Default.LocalShipping,
                        titulo = "Guías y Movimientos",
                        descripcion = "Gestionar guías y confirmar movimientos",
                        colorFondo = Color(0xFF2196F3),
                        contadorBadge = 2,
                        onClick = { navController.navigate(Routes.GuiasMovimientos.route) }
                    )

                    // Tarjeta Material
                    TarjetaMenu(
                        icono = Icons.Default.ShoppingCart,
                        titulo = "Material",
                        descripcion = "Solicitar material y crotaleras",
                        colorFondo = Color(0xFF9C27B0),
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
                                tint = Color(0xFF4A7C59),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Información del Sistema",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF2E5C3E),
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Todos los datos se sincronizan automáticamente con el sistema de registro de la Generalitat de Catalunya.",
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
                        containerColor = Color(0xFFFF5252),
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
                    color = Color(0xFF1E293B),
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    descripcion,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }

            // Icono flecha
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver más",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun DrawerContent(
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
                    "Terrabit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A7C59)
                )
                Text(
                    "Gestión Ganadera",
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
            OpcionTipoAnimal(
                icono = Icons.Default.Agriculture,
                titulo = "Bovinos",
                seleccionado = tipoSeleccionado == "Bovinos",
                onClick = { onTipoSeleccionado("Bovinos") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Porcinos
            OpcionTipoAnimal(
                icono = Icons.Default.EmojiNature,
                titulo = "Porcinos",
                seleccionado = tipoSeleccionado == "Porcinos",
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
                        "Versión 1.0.0",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        "© 2026 Terrabit",
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
fun OpcionTipoAnimal(
    icono: ImageVector,
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (seleccionado) Color(0xFF4A7C59).copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (seleccionado) Color(0xFF4A7C59) else Color(0xFF64748B)
    val iconColor = if (seleccionado) Color(0xFF4A7C59) else Color(0xFF94A3B8)

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
                    tint = Color(0xFF4A7C59),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderBienvenida(
    tipoAnimal: String,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A7C59),
                        Color(0xFF5D9470)
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
                        contentDescription = "Menú",
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
                            contentDescription = "Notificaciones",
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
                "¡Bienvenido!",
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