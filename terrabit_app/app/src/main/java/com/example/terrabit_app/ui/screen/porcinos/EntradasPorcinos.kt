package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
fun EntradasPorcinos(navController: NavController) {

    val viewModel = viewModel<EntradasPorcinosViewModel>()
    val uiState         by viewModel.uiState.collectAsState()
    val consultaIniciada by viewModel.consultaIniciada.collectAsState()
    val fechaDisplay    by viewModel.fechaDisplay.collectAsState()
    val mostrarDatePicker by viewModel.mostrarDatePicker.collectAsState()
    val mostrarTimePicker by viewModel.mostrarTimePicker.collectAsState()
    val error           by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.inicializarUserPreferences(context) }

    // ── DatePicker ────────────────────────────────────────────────────────────
    if (mostrarDatePicker) {
        val dpState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePicker() },
            confirmButton = {
                TextButton(onClick = { dpState.selectedDateMillis?.let { viewModel.seleccionarFecha(it) } }) {
                    Text("Acceptar", color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text("Cancel·lar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = dpState, colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange
            ))
        }
    }

    // ── TimePicker ────────────────────────────────────────────────────────────
    if (mostrarTimePicker) {
        val tpState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePicker() },
            confirmButton = {
                TextButton(onClick = { viewModel.seleccionarHora(tpState.hour, tpState.minute) }) {
                    Text("Acceptar", color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePicker() }) {
                    Text("Cancel·lar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                TimePicker(state = tpState, colors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = Color.White, selectorColor = MainOrange
                ))
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.name_confirmar_entradas), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (consultaIniciada) viewModel.resetearConsulta()
                        else navController.popBackStack()
                    }) {
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                // ── 1. Formulario de fecha ────────────────────────────────────
                !consultaIniciada -> {
                    MiniFormularioPorcinos(
                        fechaDisplay = fechaDisplay,
                        error        = error,
                        onFechaClick = viewModel::mostrarDatePicker,
                        onConsultar  = viewModel::validarYConsultar
                    )
                }
                // ── 2. Cargando ───────────────────────────────────────────────
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MainOrange, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.gest_porcinos_cargando_entradas),
                            color = Color.Gray, fontSize = 14.sp)
                    }
                }
                // ── 3. Lista ──────────────────────────────────────────────────
                else -> {
                    if (uiState.listaEntradasPorcinos.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(R.string.form_porcinos_no_entr),
                                    fontSize = 16.sp, color = Color.Gray)
                                TextButton(onClick = viewModel::resetearConsulta) {
                                    Text("Nova consulta", color = MainOrange)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text("${uiState.listaEntradasPorcinos.size} entrada(es) trobades",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
                            }
                            items(uiState.listaEntradasPorcinos) { guia ->
                                EntradaCard(guia, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Mini-formulario ───────────────────────────────────────────────────────────
@Composable
private fun MiniFormularioPorcinos(
    fechaDisplay: String,
    error: String?,
    onFechaClick: () -> Unit,
    onConsultar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Consultar entrades pendents",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface)

                // Campo fecha (solo lectura, abre picker al pulsar)
                Text("Data de sortida", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Box(modifier = Modifier.fillMaxWidth().clickable { onFechaClick() }) {
                    OutlinedTextField(
                        value = fechaDisplay,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("dd/mm/aaaa hh:mm",
                            color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.DateRange,
                            contentDescription = null, tint = MainOrange) },
                        readOnly = true,
                        enabled = false,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MainOrange,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true
                    )
                }

                if (!error.isNullOrBlank()) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Button(
                    onClick = onConsultar,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Consultar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
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
