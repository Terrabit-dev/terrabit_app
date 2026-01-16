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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.viewmodel.MainViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fallecimiento(navController: NavController, viewModel: MainViewmodel) {
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

    // Observar estado de registro para mostrar mensajes
    val registroExitoso by viewModel.registroMuerteExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorMuerte.observeAsState("")

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar cuando hay mensaje de éxito o error
    LaunchedEffect(registroExitoso, mensajeError) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = "✅ Muerte reportada exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistroMuerte()
        } else if (mensajeError.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = mensajeError,
                duration = SnackbarDuration.Long
            )
            viewModel.resetearEstadoRegistroMuerte()
        }
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
                    Text("Aceptar", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerMuerte() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Reportar Muerte",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Sección 5.3",
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
                    containerColor = if (data.visuals.message.contains("✅")) {
                        Color(0xFF4A7C59) // Verde para éxito
                    } else {
                        Color(0xFFD32F2F) // Rojo para error
                    },
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
                            "Tipo *",
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
                                        "Seleccionar tipo",
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
                            ExposedDropdownMenu(
                                expanded = tipoExpandido,
                                onDismissRequest = { viewModel.cerrarTipoMuerteMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.listaTiposMuerte.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                tipo,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B),
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        onClick = { viewModel.seleccionarTipoMuerte(tipo) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 14.dp
                                        )
                                    )
                                    if (tipo != viewModel.listaTiposMuerte.last()) {
                                        HorizontalDivider(
                                            color = Color(0xFFF1F5F9),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ID Animal / ID Madre (según tipo)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (tipoSeleccionado.contains("Avortament")) "ID Madre *" else "ID Animal *",
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
                                    if (tipoSeleccionado.contains("Avortament"))
                                        "Introducir o escanear ID de la madre"
                                    else
                                        "Introducir o escanear ID del animal",
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
                            // CORRECCIÓN 1: Configurar tipo de teclado y acciones IME
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Fecha de Muerte - CORRECCIÓN 2: Usar Box con clickable
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Fecha de Muerte *",
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
                                        "Seleccionar fecha",
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
                    if (tipoSeleccionado.contains("Avortament")) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Meses de Gestación *",
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
                                        "Número de meses (1-9)",
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
                                "Cadáver Inaccesible",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Activar si la ubicación no puede ser accedida",
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
                                        "Coordenadas GPS",
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
                                        "Obtener Ubicación Actual",
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
                                            "Latitud (X)",
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
                                                    "X coordinate",
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
                                            "Longitud (Y)",
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
                                                    "Y coordinate",
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

            // Botón Reportar Muerte - SIEMPRE HABILITADO
            Button(
                onClick = { viewModel.reportarMuerte() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Text(
                    "Reportar Muerte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}