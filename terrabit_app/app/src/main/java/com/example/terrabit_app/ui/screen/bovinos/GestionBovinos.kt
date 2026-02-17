package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.WhiteBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionBovinos(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.gestion_name_bovinos),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HomeBovinos.route) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainGreen
                )
            )
        },
        containerColor = WhiteBackground
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
                stringResource(R.string.gestion_subtitle_bovinos),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlueGrey,
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
                        titulo = stringResource(R.string.action_register_born),
                        subtitulo = "",
                        colorFondo = MainGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Nacimiento.route) }
                    )

                    TarjetaAccion(
                        icono = Icons.Default.Clear,
                        titulo = stringResource(R.string.action_report_dead),
                        subtitulo = "",
                        colorFondo = ErrorRed,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.Fallecimiento.route) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaAccion(
                        icono = Icons.Default.Edit,
                        titulo = stringResource(R.string.action_correct_sex),
                        subtitulo = "",
                        colorFondo = MainGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.CorregirBovino.route) }
                    )

                    TarjetaAccion(
                        icono = Icons.Default.AddCircle,
                        titulo = stringResource(R.string.action_identify_animal),
                        subtitulo = "",
                        colorFondo = MainGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Routes.IdentificacionAplazada.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}