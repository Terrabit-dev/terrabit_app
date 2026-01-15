package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column (
                        modifier = Modifier
                        .padding(top = 15.dp)
                    ){
                        Text(
                            "Terrabit",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp,

                        )

                        Text(
                            "Gestión de Ganado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.92f),
                            letterSpacing = 0.2.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A7C59)
                ),
                actions = {
                    IconButton(
                        onClick = { /* Notificaciones */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Badge(
                            containerColor = Color(0xFFFF5252),
                            modifier = Modifier
                                .offset(x = (-4).dp, y = 8.dp)

                        ) {
                            Text(
                                "3",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Espaciado superior
            Spacer(modifier = Modifier.height(24.dp))

            // Título de sección
            Text(
                "Acciones Rápidas",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de tarjetas con más espacio
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primera fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Add,
                        titulo = "Registrar Nacimiento",
                        subtitulo = "Sección 5.1",
                        colorFondo = Color(0xFF4A7C59),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Nacimiento.route) }
                    )

                    TarjetaAccion(
                        icono = Icons.Default.Clear,
                        titulo = "Reportar Muerte",
                        subtitulo = "Sección 5.3",
                        colorFondo = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Fallecimiento.route) }
                    )
                }

                // Segunda fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Create,
                        titulo = "Gestionar Guías",
                        subtitulo = "Sección 5.4",
                        colorFondo = Color(0xFF4A7C59),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.GestionGuias.route) }
                    )

                    TarjetaAccion(
                        icono = Icons.Default.Send,
                        titulo = "Confirmar Movimientos",
                        subtitulo = "Sección 5.8",
                        colorFondo = Color(0xFF4A7C59),
                        contadorBadge = 2,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Movimientos.route) }
                    )
                }

                // Tercera fila - tarjeta centrada
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.ShoppingCart,
                        titulo = "Solicitar Material",
                        subtitulo = "Sección 5.14",
                        colorFondo = Color(0xFF4A7C59),
                        modifier = Modifier
                            .fillMaxWidth(0.48f), // Mismo ancho que las otras
                        onClick = { navController.navigate(Routes.Material.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card de Información del Sistema (mejorada)
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

@Composable
fun TarjetaAccion(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    colorFondo: Color,
    modifier: Modifier = Modifier,
    contadorBadge: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icono con badge
                Box(contentAlignment = Alignment.TopEnd) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = colorFondo,
                        modifier = Modifier.size(72.dp),
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            icono,
                            contentDescription = titulo,
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                        )
                    }

                    // Badge de notificación
                    if (contadorBadge != null) {
                        Badge(
                            containerColor = Color(0xFFFF5252),
                            modifier = Modifier
                                .offset(x = 8.dp, y = (-8).dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    letterSpacing = 0.2.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtítulo
                Text(
                    subtitulo,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.1.sp
                )
            }
        }
    }
}