package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.moviminetos.modelos.Moviment
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.viewmodel.bovinos.ListarMovisBoviViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarMovimientosBovi(
    navController: NavController,
    viewModel: ListarMovisBoviViewModel = hiltViewModel()
) {
    val listaMovimientos     by viewModel.listaMovimientos.observeAsState(emptyList())
    val cargando             by viewModel.cargando.observeAsState(false)
    val consultaIniciada     by viewModel.consultaIniciada.observeAsState(false)
    val error                by viewModel.error.observeAsState(null)
    val codiExplotacionDesti by viewModel.codiExplotacionDesti.observeAsState("")
    val mostrarDatePicker    by viewModel.mostrarDatePicker.observeAsState(false)
    val mostrarTimePicker    by viewModel.mostrarTimePicker.observeAsState(false)
    val fechaDisplay         by viewModel.fechaDisplay.observeAsState("")
    val navBackStackEntry    by navController.currentBackStackEntryAsState()
    val historialManager     = viewModel.historialCamposManager

    LaunchedEffect(navBackStackEntry) {
        if (
            navBackStackEntry?.destination?.route == Routes.MovimientosBovinos.route &&
            consultaIniciada &&
            !cargando
        ) {
            viewModel.cargarMovimientos()
        }
    }

    if (mostrarDatePicker) {
        val dpState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePicker() },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { viewModel.seleccionarFecha(it) }
                }) { Text("Aceptar", color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state  = dpState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainOrange,
                    todayDateBorderColor      = MainOrange
                )
            )
        }
    }

    if (mostrarTimePicker) {
        val tpState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePicker() },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.seleccionarHora(tpState.hour, tpState.minute)
                }) { Text("Aceptar", color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePicker() }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                TimePicker(
                    state  = tpState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor                = MainOrange
                    )
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de Movimientos de bovinos",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (consultaIniciada) viewModel.resetearConsulta()
                        else navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor            = MainOrange,
                    titleContentColor         = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                !consultaIniciada -> {
                    MiniFormulario(
                        rega             = codiExplotacionDesti,
                        fechaDisplay     = fechaDisplay,
                        error            = error,
                        historialManager = historialManager,
                        onRegaChange     = viewModel::onCodiChange,
                        onFechaClick     = viewModel::mostrarDatePicker,
                        onConsultar      = viewModel::validarPeticion
                    )
                }

                cargando -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color       = MainOrange,
                            strokeWidth = 4.dp,
                            modifier    = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text     = "Cargando movimientos...",
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    if (listaMovimientos.isEmpty()) {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier           = Modifier.size(48.dp)
                                )
                                Text(
                                    text     = "No se encontraron movimientos",
                                    fontSize = 16.sp,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(onClick = viewModel::resetearConsulta) {
                                    Text("Nueva consulta", color = MainOrange)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding      = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text     = "${listaMovimientos.size} movimiento(s) encontrado(s)",
                                    fontSize = 13.sp,
                                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                )
                            }
                            itemsIndexed(listaMovimientos) { _, movimiento ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                                    exit    = fadeOut()
                                ) {
                                    MovimientoCardBovi(
                                        guia          = movimiento,
                                        navController = navController,
                                        onEditarClick = {
                                            viewModel.seleccionarMovi(movimiento)
                                            navController.navigate(Routes.ConfirmarMovimientoBovi.route)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovimientoCardBovi(
    guia: Moviment,
    navController: NavController,
    onEditarClick: () -> Unit
) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier              = Modifier.weight(1f)
                ) {
                    Text(
                        text       = guia.moOrigen ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = MaterialTheme.colorScheme.onSurface,
                        maxLines   = 1
                    )
                    Icon(
                        imageVector        = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint               = MainOrange,
                        modifier           = Modifier.size(16.dp)
                    )
                    Text(
                        text       = guia.moDestinacio,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = MaterialTheme.colorScheme.onSurface,
                        maxLines   = 1
                    )
                }
                FilledIconButton(
                    onClick  = { onEditarClick() },
                    shape    = RoundedCornerShape(8.dp),
                    colors   = IconButtonDefaults.iconButtonColors(containerColor = MainOrange),
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

            Text(
                text = guia.codiRemo,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                fontFamily = FontFamily.Monospace
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Fecha de salida",  fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = guia.dataSortida.ifBlank { "--/--/----" },  fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Fecha de llegada", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = guia.dataArribada.ifBlank { "--/--/----" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BoviInfoChip(icon = Icons.Default.Pets,          label = "${guia.identificadors.size} animales")
                BoviInfoChip(icon = Icons.Default.LocalShipping, label = guia.matricula ?: "Sin matrícula")
                if (guia.nifConductor != null) {
                    BoviInfoChip(icon = Icons.Default.Person, label = guia.nifConductor)
                }
            }
        }
    }
}