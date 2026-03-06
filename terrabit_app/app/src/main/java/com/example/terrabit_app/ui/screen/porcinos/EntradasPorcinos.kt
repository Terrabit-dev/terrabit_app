package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.viewmodel.porcinos.EntradasPorcinosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradasPorcinos(
    navController: NavController
) {

    val viewModelEntradasGuias = viewModel<EntradasPorcinosViewModel>()
    val uiState by viewModelEntradasGuias.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModelEntradasGuias.inicializarUserPreferences(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.name_confirmar_entradas), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        // Contenedor principal para manejar el estado de carga
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                // PANTALLA DE CARGA
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MainGreen,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.gest_porcinos_cargando_entradas),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // LISTA DE DATOS
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.listaEntradasPorcinos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.form_porcinos_no_entr),
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        items(uiState.listaEntradasPorcinos) { guia ->
                            EntradaCard(guia, viewModelEntradasGuias)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntradaCard(
    guia: MovimentPteDetail,
    viewModelGestionarGuias: EntradasPorcinosViewModel = viewModel(),
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    // Código REMO
                    Text(
                        text = guia.codiRemo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    // MO Destinación
                    Text(
                        text = guia.moDesti,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Column {
                    // Fecha Salida
                    Text(
                        text = stringResource(R.string.form_porcino_entradas_fecha_salida) + guia.dataSortida,
                        fontSize = 16.sp
                    )
                    // Fecha Llegada
                    Text(
                        text = stringResource(R.string.form_porcinos_entradas_fecha_llegada) + guia.dataArribada,
                        fontSize = 16.sp
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón confirmar entrada
                FilledIconButton(
                    onClick = {
                        viewModelGestionarGuias.confirmarEntrada(guia)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MainGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.form_porcinos_but_confirmar),
                        tint = Color.White
                    )
                }
            }
        }
    }
}