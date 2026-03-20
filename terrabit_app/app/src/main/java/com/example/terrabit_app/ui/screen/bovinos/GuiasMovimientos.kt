package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.components.TarjetaAccion
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiasMovimientos(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.name_guias_movimientos),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HomeBovinos.route) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainOrange)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.subtitle_guias_movimientos),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Create,
                        titulo = stringResource(R.string.action_create_guide),
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.GestionGuias.nuevo()) }
                    )
                    TarjetaAccion(
                        icono = Icons.Default.Send,
                        titulo = stringResource(R.string.action_confirm_movement),
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Movimientos.nuevo()) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Checklist,
                        titulo = "Lista Guias",
                        subtitulo = "",
                        colorFondo = MainOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.GuiasBovinos.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}