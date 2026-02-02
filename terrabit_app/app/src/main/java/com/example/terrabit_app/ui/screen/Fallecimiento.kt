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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.viewmodel.ViewModelMuerteBovi
import com.example.terrabit_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fallecimiento(navController: NavController, viewModel: ViewModelMuerteBovi) {
    // Observar variables del ViewModel para Fallecimiento
    val tipoSeleccionado by viewModel.tipoMuerte.observeAsState("")
    val identificadorAnimal by viewModel.identificadorMuerte.observeAsState("")
    val fechaMuerte by viewModel.fechaMuerte.observeAsState("")
    val mesesGestacion by viewModel.mesesGestacion.observeAsState("")
    val cadaverInaccesible by viewModel.cadaverInaccesible.observeAsState(false)
    val coordenadaX by viewModel.coordenadaX.observeAsState("")
    val coordenadaY by viewModel.coordenadaY.observeAsState("")
    val tipoExpandido by viewModel.tipoMuerteExpandido.observeAsState(false)
    val mostrarDatePickerMuerte by viewModel.mostrarDatePickerMuerte.observeAsState(false)
    val tipoMuerte by viewModel.codigoTipoMuerte.observeAsState("")

    // Observar estado de registro para mostrar mensajes
    val registroExitoso by viewModel.registroMuerteExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorMuerte.observeAsState("")
    val estadoCarga by viewModel.cargandoMuerte.observeAsState(false)

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    //mensajes de respuestas
    val mensajeRegistroExitoso = stringResource(R.string.successful_message_dead)
    val mensajeRegistroError = stringResource(R.string.error_message_dead)

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = mensajeRegistroExitoso,
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistroMuerte()
        }
    }

    // Mostrar diálogo cuando hay error
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
                viewModel.resetearEstadoRegistroMuerte()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Usa un icono de error apropiado
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = mensajeRegistroError,
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
                        viewModel.resetearEstadoRegistroMuerte()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DatePickerDialog
    if (mostrarDatePickerMuerte) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerMuerte() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaMuerte(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerMuerte() }) {
                    Text(stringResource(R.string.cancel_buttom), color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFFD32F2F),
                    todayDateBorderColor = Color(0xFFD32F2F)
                )
            )
        }
    }

    // Indicador de carga en pantalla completa
    if (estadoCarga) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) { }, // Bloquear interacción
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .size(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
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
                            color = Color(0xFFD32F2F),
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
    }
    else{
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                stringResource(R.string.name_report_dead),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFD32F2F),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF4A7C59), // Verde para éxito
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

                // Card principal del formulario
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Tipo (Mort / Avortament)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_type_dead),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = tipoExpandido,
                                onExpandedChange = { viewModel.toggleTipoMuerteExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = tipoSeleccionado,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_type_dead_description),
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = tipoExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFD32F2F),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                val listaTiposMuerte = mapOf<String, String>(
                                    stringResource(R.string.form_type_dead_dead) to "01",
                                    stringResource(R.string.form_type_dead_abort) to "02"

                                )
                                ExposedDropdownMenu(
                                    expanded = tipoExpandido,
                                    onDismissRequest = { viewModel.cerrarTipoMuerteMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    listaTiposMuerte.forEach { (tipo, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    tipo,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarTipoMuerte(tipo,codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // ID Animal / ID Madre (según tipo)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                if (tipoMuerte.contains("01")) stringResource(R.string.form_id_animal) else stringResource(R.string.form_id_mother),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = identificadorAnimal,
                                onValueChange = { viewModel.actualizarIdentificadorMuerte(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        if (tipoMuerte.contains("01"))
                                            stringResource(R.string.form_id_animal_description)
                                        else
                                            stringResource(R.string.form_mother_description),
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { /* Acción de cámara */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = "Escanear",
                                            tint = Color(0xFFD32F2F)
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD32F2F),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFFD32F2F)
                                ),
                                //Configurar tipo de teclado y acciones IME
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false
                                )
                            )
                        }
                        // Fecha de Muerte
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_dead_date),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.mostrarDatePickerMuerte() }
                            ) {
                                OutlinedTextField(
                                    value = fechaMuerte,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_date_description),
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Calendario",
                                            tint = Color(0xFFD32F2F)
                                        )
                                    },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color(0xFF1E293B),
                                        disabledBorderColor = Color(0xFFCBD5E1),
                                        disabledLeadingIconColor = Color(0xFFD32F2F),
                                        disabledPlaceholderColor = Color(0xFF94A3B8)
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Meses de Gestación (solo si es Avortament)
                        if (tipoMuerte.contains("02")) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    stringResource(R.string.form_pregnancy_months),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = mesesGestacion,
                                    onValueChange = { viewModel.actualizarMesesGestacion(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_pregnancy_months_description),
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFD32F2F),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        cursorColor = Color(0xFFD32F2F)
                                    ),
                                    // CORRECCIÓN 3: Teclado numérico para meses
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card de Cadáver Inaccesible
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Switch de Cadáver Inaccesible
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.title_cadaver),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(R.string.description_cadaver),
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 18.sp
                                )
                            }
                            Switch(
                                checked = cadaverInaccesible,
                                onCheckedChange = { viewModel.toggleCadaverInaccesible() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFD32F2F),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }

                        // Sección de GPS Coordinates (solo si cadáver inaccesible)
                        if (cadaverInaccesible) {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Card amarilla de GPS
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFEF3C7)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            stringResource(R.string.title_gps),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Botón obtener ubicación
                                    Button(
                                        onClick = { viewModel.obtenerUbicacionActual() },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 2.dp
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            stringResource(R.string.buttom_gps),
                                            color = Color(0xFF92400E),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Coordenadas
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Coordenada X
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                stringResource(R.string.gps_laltitud),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF92400E)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = coordenadaX,
                                                onValueChange = { viewModel.actualizarCoordenadaX(it) },
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.gps_laltitud_description),
                                                        fontSize = 13.sp,
                                                        color = Color(0xFFA16207)
                                                    )
                                                },
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFD97706),
                                                    unfocusedBorderColor = Color(0xFFFBBF24),
                                                    focusedTextColor = Color(0xFF92400E),
                                                    unfocusedTextColor = Color(0xFF92400E),
                                                    cursorColor = Color(0xFFD97706),
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                // CORRECCIÓN 4: Teclado decimal para coordenadas
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Decimal,
                                                    imeAction = ImeAction.Next
                                                )
                                            )
                                        }

                                        // Coordenada Y
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                stringResource(R.string.gps_longitud),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF92400E)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = coordenadaY,
                                                onValueChange = { viewModel.actualizarCoordenadaY(it) },
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.gps_longitud_description),
                                                        fontSize = 13.sp,
                                                        color = Color(0xFFA16207)
                                                    )
                                                },
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFD97706),
                                                    unfocusedBorderColor = Color(0xFFFBBF24),
                                                    focusedTextColor = Color(0xFF92400E),
                                                    unfocusedTextColor = Color(0xFF92400E),
                                                    cursorColor = Color(0xFFD97706),
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                // CORRECCIÓN 5: Teclado decimal para coordenadas
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Decimal,
                                                    imeAction = ImeAction.Done
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Botón Reportar Muerte - Deshabilitado mientras carga
                Button(
                    onClick = { viewModel.putMuerteBovino() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga, // Deshabilitar mientras carga
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text(
                        stringResource(R.string.buttom_form_dead),
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