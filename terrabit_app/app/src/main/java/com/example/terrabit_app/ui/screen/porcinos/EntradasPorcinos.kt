package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.bovinos.formatearFecha
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.EntradasPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel

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
                    containerColor = MainOrange,
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
                        color = MainOrange,
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
private fun EntradaCard(
    movimiento: MovimentPteDetail,
    viewModelGestionarMovs: EntradasPorcinosViewModel = viewModel()
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Cabecera: Origen → Destino + botón editar ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Origen
                    Text(
                        text = movimiento.moOrigen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkBlueGrey
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MainOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    // Destino
                    Text(
                        text = movimiento.moDesti,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkBlueGrey
                    )
                }

                FilledIconButton(
                    onClick = {
                        viewModelGestionarMovs.confirmarEntrada(movimiento)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MainOrange),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.content_description_edit),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ── Código REMO (secundario) ──
            Text(
                text = movimiento.codiRemo,
                fontSize = 17.sp,
                color = BlueGrey,
                letterSpacing = 0.5.sp,
                fontFamily = FontFamily.Monospace
            )

            HorizontalDivider(color = DarkWhiteBackground)

            // ── Fechas ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Salida
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {

                    Column {
                        Text(
                            text = stringResource(R.string.form_porcino_entradas_fecha_salida),
                            fontSize = 14.sp,
                            color = BlueGrey
                        )
                        Text(
                            text = formatearFecha(movimiento.dataSortida),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkBlueGrey
                        )
                    }
                }
                // Llegada
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {

                    Column {
                        Text(
                            text = stringResource(R.string.form_porcinos_entradas_fecha_llegada),
                            fontSize = 14.sp,
                            color = BlueGrey
                        )
                        Text(
                            text = formatearFecha(movimiento.dataArribada),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkBlueGrey
                        )
                    }
                }
            }

            // ── Chips: animales + categoría + matrícula ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Pets,
                    label = movimiento.numAnimals
                )

                InfoChip(
                    icon = Icons.Default.Category,
                    label = "Cat. ${ElementosConCodigosPorcinos().categorias()[movimiento.categoria]}"

                )
                Log.d("Guia info", "Informacion: ${movimiento} - ${movimiento.categoria}  ")
                movimiento.matricula?.let {
                    InfoChip(
                        icon = Icons.Default.LocalShipping,
                        label = it
                    )
                }
            }
        }
    }
}
