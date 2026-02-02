package com.example.terrabit_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.viewmodel.GuiasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionGuias(navController: NavController, viewModel: GuiasViewModel) {
    // Observar variables del ViewModel
    val explotacioOrigen by viewModel.explotacioOrigen.observeAsState("")
    val explotacioDestinacio by viewModel.explotacioDestinacio.observeAsState("")
    val temporal by viewModel.temporal.observeAsState("")
    val dataSortida by viewModel.dataSortida.observeAsState("")
    val horaSortida by viewModel.horaSortida.observeAsState("")
    val dataArribada by viewModel.dataArribada.observeAsState("")
    val horaArribada by viewModel.horaArribada.observeAsState("")
    val mobilitat by viewModel.mobilitat.observeAsState("")
    val pais by viewModel.pais.observeAsState("")
    val codiExplotacio by viewModel.codiExplotacio.observeAsState("")
    val codiAtes by viewModel.codiAtes.observeAsState("")
    val nomTransportista by viewModel.nomTransportista.observeAsState("")
    val mitjaTransport by viewModel.mitjaTransport.observeAsState("")
    val matricula by viewModel.matricula.observeAsState("")
    val nifConductor by viewModel.nifConductor.observeAsState("")
    val nomConductor by viewModel.nomConductor.observeAsState("")
    val identificadorsText by viewModel.identificadorsText.observeAsState("")

    // Estados de expansión
    val temporalExpandido by viewModel.temporalExpandido.observeAsState(false)
    val mobilitatExpandido by viewModel.mobilitatExpandido.observeAsState(false)
    val codiAtesExpandido by viewModel.codiAtesExpandido.observeAsState(false)
    val mitjaTransportExpandido by viewModel.mitjaTransportExpandido.observeAsState(false)

    // DatePicker y TimePicker
    val mostrarDatePickerSortida by viewModel.mostrarDatePickerSortida.observeAsState(false)
    val mostrarTimePickerSortida by viewModel.mostrarTimePickerSortida.observeAsState(false)
    val mostrarDatePickerArribada by viewModel.mostrarDatePickerArribada.observeAsState(false)
    val mostrarTimePickerArribada by viewModel.mostrarTimePickerArribada.observeAsState(false)

    // Estados de registro
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val estadoCarga by viewModel.cargandoGuia.observeAsState(false)

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    // Efectos
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = "Guía creada exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistro()
        }
    }

    LaunchedEffect(mensajeError) {
        if (mensajeError.isNotEmpty()) {
            mostrarDialogoError = true
        }
    }

    // Diálogo de Error
    if (mostrarDialogoError && mensajeError.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoError = false
                viewModel.resetearEstadoRegistro()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFFE28F41),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Error al Crear Guía",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text = mensajeError,
                    fontSize = 16.sp,
                    color = Color(0xFF475569),
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoError = false
                        viewModel.resetearEstadoRegistro()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE28F41)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DatePickerDialog Sortida
    if (mostrarDatePickerSortida) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerSortida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaSortida(millis)
                        }
                    }
                ) {
                    Text("Aceptar", color = Color(0xFFE28F41))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerSortida() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFFE28F41),
                    todayDateBorderColor = Color(0xFFE28F41)
                )
            )
        }
    }

    // TimePickerDialog Sortida
    if (mostrarTimePickerSortida) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerSortida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.actualizarHoraSortida(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModel.ocultarTimePickerSortida()
                    }
                ) {
                    Text("Aceptar", color = Color(0xFFE28F41))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerSortida() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor = Color(0xFFE28F41)
                    )
                )
            }
        )
    }

    // DatePickerDialog Arribada
    if (mostrarDatePickerArribada) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerArribada() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaArribada(millis)
                        }
                    }
                ) {
                    Text("Aceptar", color = Color(0xFFE28F41))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerArribada() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFFE28F41),
                    todayDateBorderColor = Color(0xFFE28F41)
                )
            )
        }
    }

    // TimePickerDialog Arribada
    if (mostrarTimePickerArribada) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerArribada() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.actualizarHoraArribada(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModel.ocultarTimePickerArribada()
                    }
                ) {
                    Text("Aceptar", color = Color(0xFFE28F41))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerArribada() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor = Color(0xFFE28F41)
                    )
                )
            }
        )
    }

    // Indicador de carga
    if (estadoCarga) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFFE28F41),
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Procesando...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Gestionar Guías",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Sección 5.4",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE28F41),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF4A7C59),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            containerColor = Color(0xFFF5F7FA)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Card principal - Datos Obligatorios
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            "Datos Obligatorios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        // Explotación Origen
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Explotación Origen *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = explotacioOrigen,
                                onValueChange = { viewModel.actualizarExplotacioOrigen(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Formato MO o REGA",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Explotación Destino
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Explotación Destino *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = explotacioDestinacio,
                                onValueChange = { viewModel.actualizarExplotacioDestinacio(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Formato MO o REGA",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Temporal
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Temporal *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = temporalExpandido,
                                onExpandedChange = { viewModel.toggleTemporalExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = temporal,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "SI;NO",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = temporalExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE28F41),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = temporalExpandido,
                                    onDismissRequest = { viewModel.cerrarTemporalMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaTemporalOpciones.forEach { opcion ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    opcion,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarTemporal(opcion) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                        if (opcion != viewModel.listaTemporalOpciones.last()) {
                                            HorizontalDivider(
                                                color = Color(0xFFF1F5F9),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Fecha y Hora de Sortida
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Fecha Sortida *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.mostrarDatePickerSortida() }
                                ) {
                                    OutlinedTextField(
                                        value = dataSortida,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                "Fecha",
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Calendario",
                                                tint = Color(0xFFE28F41)
                                            )
                                        },
                                        readOnly = true,
                                        enabled = false,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color(0xFF1E293B),
                                            disabledBorderColor = Color(0xFFCBD5E1),
                                            disabledLeadingIconColor = Color(0xFFE28F41),
                                            disabledPlaceholderColor = Color(0xFF94A3B8)
                                        ),
                                        singleLine = true
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Hora *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.mostrarTimePickerSortida() }
                                ) {
                                    OutlinedTextField(
                                        value = horaSortida,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                "Hora",
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = "Reloj",
                                                tint = Color(0xFFE28F41)
                                            )
                                        },
                                        readOnly = true,
                                        enabled = false,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color(0xFF1E293B),
                                            disabledBorderColor = Color(0xFFCBD5E1),
                                            disabledLeadingIconColor = Color(0xFFE28F41),
                                            disabledPlaceholderColor = Color(0xFF94A3B8)
                                        ),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        // Fecha y Hora de Arribada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Fecha Arribada *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.mostrarDatePickerArribada() }
                                ) {
                                    OutlinedTextField(
                                        value = dataArribada,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                "Fecha",
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Calendario",
                                                tint = Color(0xFFE28F41)
                                            )
                                        },
                                        readOnly = true,
                                        enabled = false,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color(0xFF1E293B),
                                            disabledBorderColor = Color(0xFFCBD5E1),
                                            disabledLeadingIconColor = Color(0xFFE28F41),
                                            disabledPlaceholderColor = Color(0xFF94A3B8)
                                        ),
                                        singleLine = true
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Hora *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.mostrarTimePickerArribada() }
                                ) {
                                    OutlinedTextField(
                                        value = horaArribada,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                "Hora",
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = "Reloj",
                                                tint = Color(0xFFE28F41)
                                            )
                                        },
                                        readOnly = true,
                                        enabled = false,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color(0xFF1E293B),
                                            disabledBorderColor = Color(0xFFCBD5E1),
                                            disabledLeadingIconColor = Color(0xFFE28F41),
                                            disabledPlaceholderColor = Color(0xFF94A3B8)
                                        ),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        // Mobilitat
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Guía per mobilitat *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = mobilitatExpandido,
                                onExpandedChange = { viewModel.toggleMobilitatExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = mobilitat,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "SI;NO",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = mobilitatExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE28F41),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = mobilitatExpandido,
                                    onDismissRequest = { viewModel.cerrarMobilitatMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaMobilitatOpciones.forEach { opcion ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    opcion,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarMobilitat(opcion) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                        if (opcion != viewModel.listaMobilitatOpciones.last()) {
                                            HorizontalDivider(
                                                color = Color(0xFFF1F5F9),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // País
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Codi país per guies amb destí PIF *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = pais,
                                onValueChange = { viewModel.actualizarPais(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Tipus explotació: Centre d'Inspecció",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Código Explotación
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Codi explotació, per guies amb destí PIF",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = codiExplotacio,
                                onValueChange = { viewModel.actualizarCodiExplotacio(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Obligatori si explotacioDestinacio és un tipus explotació",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card de Datos Opcionales del Transporte
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            "Datos Opcionales del Transporte",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        // Código ATES
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Código ATES Transportista",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = codiAtesExpandido,
                                onExpandedChange = { viewModel.toggleCodiAtesExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = codiAtes,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Seleccionar código",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = codiAtesExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE28F41),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = codiAtesExpandido,
                                    onDismissRequest = { viewModel.cerrarCodiAtesMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaCodigosAtes.forEach { ates ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "${ates.codigo} - ${ates.nombre}",
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarCodiAtes(ates.codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Nombre Transportista
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Nombre del Transportista",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = nomTransportista,
                                onValueChange = { viewModel.actualizarNomTransportista(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Si existeix ATES a GTR no s'actualitzará",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Medio de Transporte
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Medio de Transporte",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = mitjaTransportExpandido,
                                onExpandedChange = { viewModel.toggleMitjaTransportExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = mitjaTransport,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Seleccionar medio",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = mitjaTransportExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE28F41),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = mitjaTransportExpandido,
                                    onDismissRequest = { viewModel.cerrarMitjaTransportMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaMitjaTransport.forEach { medio ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    medio,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarMitjaTransport(medio) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                        if (medio != viewModel.listaMitjaTransport.last()) {
                                            HorizontalDivider(
                                                color = Color(0xFFF1F5F9),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Matrícula
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Matrícula del Vehículo",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = matricula,
                                onValueChange = { viewModel.actualizarMatricula(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Ejemplo: 1234ABC",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // NIF Conductor
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "NIF del Conductor",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = nifConductor,
                                onValueChange = { viewModel.actualizarNifConductor(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Si existeix NIF al mestre persones GTR no s'actualitzará",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Nombre Conductor
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Nombre del Conductor",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = nomConductor,
                                onValueChange = { viewModel.actualizarNomConductor(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Nombre completo",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Identificadores
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Identificadores de los Animales",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = identificadorsText,
                                onValueChange = { viewModel.actualizarIdentificadorsText(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                placeholder = {
                                    Text(
                                        "Separados por comas o saltos de línea",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE28F41),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFE28F41)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                maxLines = 5
                            )
                        }
                    }
                }

                // Botón Crear Guía
                Button(
                    onClick = { viewModel.confirmarAltaGuia() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE28F41),
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text(
                        "Crear Guía",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}