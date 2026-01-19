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
fun MaterialCategoria(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Material",
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
                    containerColor = Color(0xFF9C27B0)
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
                // Solicitar Material (centrada)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.ShoppingCart,
                        titulo = "Solicitar Material",
                        subtitulo = "",
                        colorFondo = Color(0xFF9C27B0),
                        modifier = Modifier.fillMaxWidth(0.48f),
                        onClick = { navController.navigate(Routes.Material.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}