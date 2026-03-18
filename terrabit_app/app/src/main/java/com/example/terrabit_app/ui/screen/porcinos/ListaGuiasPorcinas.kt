package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.UserPreferences
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.example.terrabit_app.viewmodel.bovinos.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaGuiasPorcinas(
    navController: NavController,
    viewModelGestionarGuias: GestionarGuiasViewModel,
    viewModelEditarGuias: EditarGuiaPorcinosViewModel
) {
    val context = LocalContext.current

    // 1. Inicializamos la API usando tu propio companion object
    val repo = remember { Repositorio(context) }

    // 2. Inicializamos las preferencias
    val userPrefs = remember { UserPreferences(context) }

    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)
    val codiMoActivo by codiMoViewModel.codiMoActivo.observeAsState(null)
    // 3. Creamos el ViewModel con la Factory



    val uiStateGestionGuias by viewModelGestionarGuias.uiState.collectAsState()

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModelGestionarGuias.cargarMovimientosDesdeApi()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.gest_porcinos_edit_confirm),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
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
        // Contenedor principal

        Column(                          // ← Column en lugar de Box
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                CodiMoSelector(
                    codisMos = codiMoViewModel.getCodisMos(),
                    seleccionado = codiMoActivo,
                    expanded = codisMoExpandido,
                    onToggle = { codiMoViewModel.toggleCodisMoExpandido() },
                    onDismiss = { codiMoViewModel.cerrarCodisMo() },
                    onSeleccionar = { codi -> codiMoViewModel.seleccionarCodiMo(codi) },
                    accentColor = MainOrange
                )
            }

            if (uiStateGestionGuias.isLoading) {
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
                        text = stringResource(R.string.gest_porcinos_cargando_mov),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Si NO está cargando, muestra la lista o el mensaje de vacío
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiStateGestionGuias.listaGuiasPorcinos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.gest_porcinos_no_guias),
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        items(uiStateGestionGuias.listaGuiasPorcinos) { guia ->
                            GuiaCard(
                                navController = navController,
                                guia = guia,
                                viewModelGestionarGuias = viewModelGestionarGuias,
                                viewModelEditarGuias = viewModelEditarGuias
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // CONDICIÓ: Si está cargando, muestra el círculo de carga.

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GuiaCard(
    navController: NavController,
    guia: GuiaGTRLista,
    viewModelGestionarGuias: GestionarGuiasViewModel = viewModel(),
    viewModelEditarGuias: EditarGuiaPorcinosViewModel = viewModel(),
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
                        text = guia.moOrigen,
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
                        text = guia.moDesti,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkBlueGrey
                    )
                }

                FilledIconButton(
                    onClick = {
                        viewModelEditarGuias.cargarDatosGuia(guia)
                        navController.navigate(Routes.EditarGuiaPorcinos.route)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MainOrange),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.content_description_edit),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ── Código REMO (secundario) ──
            Text(
                text = guia.remo,
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
                            text = formatearFecha(guia.dataSortida),
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
                            text = formatearFecha(guia.dataArribada),
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
                    label = "${guia.nombreAnimals}"
                )

                InfoChip(
                    icon = Icons.Default.Category,
                    label = "Cat. ${ElementosConCodigosPorcinos().categorias()[guia.categoria]}"

                )
                Log.d("Guia info", "Informacion: ${guia} - ${guia.categoria}  ")
                guia.vehicle?.let {
                    InfoChip(
                        icon = Icons.Default.LocalShipping,
                        label = it
                    )
                }
            }
        }
    }
}

// ── Chip reutilizable ──
@Composable
fun InfoChip(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DarkWhiteBackground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MainOrange,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = DarkBlueGrey,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatearFecha(dateLong: Long): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val dateTime = LocalDateTime.parse(dateLong.toString(), inputFormatter)
    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}