package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.ui.components.TarjetaAccion
import com.example.terrabit_app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionBovinos(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Bovinos",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A7C59)
                )
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
            Spacer(modifier = Modifier.height(24.dp))

            // Descripción
            Text(
                "Selecciona una acción",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de acciones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Registrar Nacimiento y Reportar Muerte
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Add,
                        titulo = "Registrar Nacimiento",
                        subtitulo = "",
                        colorFondo = Color(0xFF4A7C59),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Nacimiento.route) }
                    )

                    TarjetaAccion(
                        icono = Icons.Default.Clear,
                        titulo = "Reportar Muerte",
                        subtitulo = "",
                        colorFondo = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Fallecimiento.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}