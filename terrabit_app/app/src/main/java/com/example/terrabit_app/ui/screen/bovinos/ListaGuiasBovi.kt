package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.terrabit_app.R
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.network.guias.Guia
import com.example.terrabit_app.ui.components.HistorialAutoCompleteField
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.viewmodel.bovinos.ListarGuiasBoviViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaGuiasBovi(
    navController: NavController,
    viewModel: ListarGuiasBoviViewModel = hiltViewModel()
) {
    val listaGuias       by viewModel.listaGuias.observeAsState(emptyList())
    val cargando         by viewModel.cargando.observeAsState(false)
    val consultaIniciada by viewModel.consultaIniciada.observeAsState(false)
    val error            by viewModel.error.observeAsState(null)
    val rega             by viewModel.codiRega.observeAsState("")
    val mostrarDatePicker by viewModel.mostrarDatePicker.observeAsState(false)
    val mostrarTimePicker by viewModel.mostrarTimePicker.observeAsState(false)
    val fechaDisplay      by viewModel.fechaDisplay.observeAsState("")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val historialManager = viewModel.historialCamposManager

    LaunchedEffect(navBackStackEntry) {
        if (
            navBackStackEntry?.destination?.route == Routes.GuiasBovinos.route &&
            consultaIniciada &&
            !cargando
        ) {
            viewModel.cargarDatos()
        }
    }

    if (mostrarDatePicker) {
        val dpState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePicker() },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { viewModel.seleccionarFecha(it) }
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePicker() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        text = stringResource(R.string.name_manage_guides),
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
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                            tint = Color.White
                        )
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
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                !consultaIniciada -> {
                    MiniFormulario(
                        rega             = rega,
                        fechaDisplay     = fechaDisplay,
                        error            = error,
                        historialManager = historialManager,
                        onRegaChange     = viewModel::onRegaChange,
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
                        CircularProgressIndicator(color = MainOrange, strokeWidth = 4.dp, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.gest_porcinos_cargando_guides), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }

                else -> {
                    if (listaGuias.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                                Text(text = stringResource(R.string.no_found_result), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                TextButton(onClick = viewModel::resetearConsulta) {
                                    Text(stringResource(R.string.new_query), color = MainOrange)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "${listaGuias.size} guía(s) encontrada(s)",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                )
                            }
                            itemsIndexed(listaGuias) { _, guia ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                                    exit    = fadeOut()
                                ) {
                                    GuiaCardBovi(
                                        guia          = guia,
                                        navController = navController,
                                        onEditarClick = {
                                            viewModel.seleccionarGuia(guia)
                                            navController.navigate(Routes.EditarGuiaBovi.route)
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
fun MiniFormulario(
    rega: String,
    fechaDisplay: String,
    error: String?,
    historialManager: HistorialCamposManager,
    onRegaChange: (String) -> Unit,
    onFechaClick: () -> Unit,
    onConsultar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier  = Modifier.fillMaxWidth(),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape     = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MainOrange.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint     = MainOrange,
                            modifier = Modifier.padding(8.dp).size(20.dp)
                        )
                    }
                    Text(
                        text       = stringResource(R.string.consult_bovine_guide),
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                HistorialAutoCompleteField(
                    valor            = rega,
                    onValorChange    = onRegaChange,
                    label            = "Código REGA",
                    clave            = "codi_rega",
                    historialManager = historialManager,
                    modifier         = Modifier.fillMaxWidth()
                )

                Column {
                    Text(
                        text       = stringResource(R.string.form_date_departure).dropLast(1), //quitamos el *
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().clickable { onFechaClick() }) {
                        OutlinedTextField(
                            value         = fechaDisplay,
                            onValueChange = {},
                            modifier      = Modifier.fillMaxWidth(),
                            placeholder   = {
                                Text(stringResource(R.string.form_porcinos_descr_fechaS), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            leadingIcon   = {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange)
                            },
                            readOnly      = true,
                            enabled       = false,
                            shape         = MaterialTheme.shapes.medium,
                            colors        = OutlinedTextFieldDefaults.colors(
                                disabledTextColor        = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor      = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MainOrange,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledContainerColor   = MaterialTheme.colorScheme.surface
                            ),
                            singleLine    = true
                        )
                    }
                }

                AnimatedVisibility(visible = !error.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint     = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = error.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }
                }

                Button(
                    onClick  = onConsultar,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape    = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Consultar guías", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun GuiaCardBovi(
    guia: Guia,
    navController: NavController,
    onEditarClick: () -> Unit
) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = guia.explotacioOrigen, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = MainOrange, modifier = Modifier.size(16.dp))
                    Text(text = guia.explotacioDestinacio, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                }
                FilledIconButton(
                    onClick  = { onEditarClick() },
                    shape    = RoundedCornerShape(8.dp),
                    colors   = IconButtonDefaults.iconButtonColors(containerColor = MainOrange),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.content_description_edit), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Text(text = guia.remo, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp, fontFamily = FontFamily.Monospace)

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.form_porcinos_fecha_salida), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = guia.dataSortida.ifBlank { "--/--/----" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Fecha de llegada", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = guia.dataArribada.ifBlank { "--/--/----" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BoviInfoChip(icon = Icons.Default.Pets,          label = "${guia.numeroAnimals} ${stringResource(R.string.animals)}")
                BoviInfoChip(icon = Icons.Default.LocalShipping, label = guia.matricula.ifBlank { stringResource(R.string.no_registration) })
                if (guia.nifConductor.isNotBlank()) {
                    BoviInfoChip(icon = Icons.Default.Person, label = guia.nifConductor)
                }
            }
        }
    }
}

@Composable
fun BoviInfoChip(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MainOrange, modifier = Modifier.size(12.dp))
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        }
    }
}