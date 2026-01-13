package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
                    Column {
                        Text(
                            "GTR Bovino",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Gestión de Ganado",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0)
                ),
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                "Acciones Rápidas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Primera fila de acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaAccion(
                    icono = Icons.Default.Add,
                    titulo = "Registrar Nacimiento",
                    subtitulo = "Sección 5.1",
                    colorFondo = Color(0xFF1565C0),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Segunda fila de acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaAccion(
                    icono = Icons.Default.Create,
                    titulo = "Gestionar Guías",
                    subtitulo = "Sección 5.4",
                    colorFondo = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Routes.GestionGuias.route) }
                )

                TarjetaAccion(
                    icono = Icons.Default.Send,
                    titulo = "Confirmar Movimientos",
                    subtitulo = "Sección 5.8",
                    colorFondo = Color(0xFF1565C0),
                    contadorBadge = 2,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Routes.Movimientos.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tercera fila - una sola tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaAccion(
                    icono = Icons.Default.ShoppingCart,
                    titulo = "Solicitar Material",
                    subtitulo = "Sección 5.14",
                    colorFondo = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Routes.Material.route) }
                )

                // Espacio vacío para mantener el diseño
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Información del Sistema
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            "Información del Sistema",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Todos los datos se sincronizan con el sistema de registro de la Generalitat de Catalunya.",
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colorFondo,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            icono,
                            contentDescription = titulo,
                            tint = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Badge
                    if (contadorBadge != null) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(contadorBadge.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )

                Text(
                    subtitulo,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}