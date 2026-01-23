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
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.viewmodel.MainViewmodel
import com.example.terrabit_app.viewmodel.NacimientoViewmodel
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nacimiento(navController: NavController, viewModel: NacimientoViewmodel) {
    // Observar todas las variables del ViewModel
    val idMadre by viewModel.idMadre.observeAsState("")
    val idCria by viewModel.idCria.observeAsState("")
    val fechaNacimiento by viewModel.fechaNacimiento.observeAsState("")
    val sexoSeleccionado by viewModel.sexoSeleccionado.observeAsState("")
    val razaSeleccionada by viewModel.razaSeleccionada.observeAsState("")
    val aptitudSeleccionada by viewModel.aptitudSeleccionada.observeAsState("")
    val sexoExpandido by viewModel.sexoExpandido.observeAsState(false)
    val razaExpandida by viewModel.razaExpandida.observeAsState(false)
    val aptitudExpandida by viewModel.aptitudExpandida.observeAsState(false)
    val mostrarDatePicker by viewModel.mostrarDatePicker.observeAsState(false)
    val fechaIdentificacion by viewModel.fechaIdentificacion.observeAsState("")
    val mostrarDatePickerIdentificadores by viewModel.mostrarDatePickerIdentificacion.observeAsState(false)

    // Observar identificadores
    val identificadores: Identificadores by viewModel.identificadores.observeAsState(
        Identificadores(emptyList())
    )

    // Observar estado de registro para mostrar mensajes
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val estadoCarga by viewModel.cargandoNacimiento.observeAsState(false)

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    // Mostrar Snackbar cuando hay éxito
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = "Nacimiento registrado exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistro()
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
                viewModel.resetearEstadoRegistro()
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
                    text = "Error al Registrar Nacimiento",
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
                        containerColor = Color(0xFFD32F2F)
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

    // Obtener identificadores al cargar la pantalla
    LaunchedEffect(Unit) {
        viewModel.getIdentificadores("S0800608B", "L1855m58", "1410AK")
    }

    // DatePickerDialog para fecha de nacimiento
    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePicker() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFecha(millis)
                        }
                    }
                ) {
                    Text("Aceptar", color = Color(0xFF4A7C59))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF4A7C59),
                    todayDateBorderColor = Color(0xFF4A7C59)
                )
            )
        }
    }

    // DatePickerDialog para fecha de identificación
    if (mostrarDatePickerIdentificadores) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerIdentificacion() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaIdentificacion(millis)
                        }
                    }
                ) {
                    Text("Aceptar", color = Color(0xFF4A7C59))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerIdentificacion() }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF4A7C59),
                    todayDateBorderColor = Color(0xFF4A7C59)
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
                            color = Color(0xFF4A7C59),
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
    else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Registrar Nacimiento",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Sección 5.1",
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
                        containerColor = Color(0xFF4A7C59),
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
                        // ID Madre
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "ID Madre *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = idMadre,
                                onValueChange = { viewModel.actualizarIdMadre(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Introducir o escanear ID de la madre",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { /* Acción de cámara */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = "Escanear",
                                            tint = Color(0xFF4A7C59)
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4A7C59),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFF4A7C59)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false
                                )
                            )
                        }

                        // ID Cría
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "ID Cría *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = idCria,
                                onValueChange = { viewModel.actualizarIdCria(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Introducir o escanear ID de la cría",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { /* Acción de cámara */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = "Escanear",
                                            tint = Color(0xFF4A7C59)
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4A7C59),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    cursorColor = Color(0xFF4A7C59)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false
                                )
                            )
                        }

                        // Fecha de Nacimiento
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Fecha de Nacimiento *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.mostrarDatePicker() }
                            ) {
                                OutlinedTextField(
                                    value = fechaNacimiento,
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
                                            tint = Color(0xFF4A7C59)
                                        )
                                    },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color(0xFF1E293B),
                                        disabledBorderColor = Color(0xFFCBD5E1),
                                        disabledLeadingIconColor = Color(0xFF4A7C59),
                                        disabledPlaceholderColor = Color(0xFF94A3B8)
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Fecha de Identificación
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Fecha de Identificación *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.mostrarDatePickerIdentificacion() }
                            ) {
                                OutlinedTextField(
                                    value = fechaIdentificacion,
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
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Calendario",
                                            tint = Color(0xFF4A7C59)
                                        )
                                    },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color(0xFF1E293B),
                                        disabledBorderColor = Color(0xFFCBD5E1),
                                        disabledLeadingIconColor = Color(0xFF4A7C59),
                                        disabledPlaceholderColor = Color(0xFF94A3B8)
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Sexo
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Sexo *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = sexoExpandido,
                                onExpandedChange = { viewModel.toggleSexoExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = sexoSeleccionado,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Seleccionar sexo",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = sexoExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4A7C59),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = sexoExpandido,
                                    onDismissRequest = { viewModel.cerrarSexoMenu() },
                                    modifier = Modifier
                                        .background(Color.White)
                                ) {
                                    viewModel.listaSexos.forEach { sexo ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    sexo,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarSexo(sexo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            ),
                                            colors = MenuDefaults.itemColors(
                                                textColor = Color(0xFF1E293B),
                                                leadingIconColor = Color(0xFF1E293B),
                                                trailingIconColor = Color(0xFF1E293B),
                                                disabledTextColor = Color(0xFF94A3B8)
                                            )
                                        )
                                        if (sexo != viewModel.listaSexos.last()) {
                                            HorizontalDivider(
                                                color = Color(0xFFF1F5F9),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Raza
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Raza *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = razaExpandida,
                                onExpandedChange = { viewModel.toggleRazaExpandida() }
                            ) {
                                OutlinedTextField(
                                    value = razaSeleccionada,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Seleccionar raza",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = razaExpandida
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4A7C59),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = razaExpandida,
                                    onDismissRequest = { viewModel.cerrarRazaMenu() },
                                    modifier = Modifier
                                        .background(Color.White)
                                ) {
                                    viewModel.razasBovinas.forEach { raza ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    raza.nombre,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.seleccionarRaza(raza.nombre, raza.codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            ),
                                            colors = MenuDefaults.itemColors(
                                                textColor = Color(0xFF1E293B),
                                                leadingIconColor = Color(0xFF1E293B),
                                                trailingIconColor = Color(0xFF1E293B),
                                                disabledTextColor = Color(0xFF94A3B8)
                                            )
                                        )
                                        if (raza != viewModel.razasBovinas.last()) {
                                            HorizontalDivider(
                                                color = Color(0xFFF1F5F9),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Aptitud
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Aptitud *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = aptitudExpandida,
                                onExpandedChange = { viewModel.toggleAptitudExpandida() }
                            ) {
                                OutlinedTextField(
                                    value = aptitudSeleccionada,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Seleccionar aptitud",
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = aptitudExpandida
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4A7C59),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = aptitudExpandida,
                                    onDismissRequest = { viewModel.cerrarAptitudMenu() },
                                    modifier = Modifier
                                        .background(Color.White)
                                ) {
                                    viewModel.listaAptitudes.forEach { aptitud ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    aptitud,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarAptitud(aptitud) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            ),
                                            colors = MenuDefaults.itemColors(
                                                textColor = Color(0xFF1E293B),
                                                leadingIconColor = Color(0xFF1E293B),
                                                trailingIconColor = Color(0xFF1E293B),
                                                disabledTextColor = Color(0xFF94A3B8)
                                            )
                                        )
                                        if (aptitud != viewModel.listaAptitudes.last()) {
                                            HorizontalDivider(
                                                color = Color(0xFFF1F5F9),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Botón Registrar - Deshabilitado mientras carga
                Button(
                    onClick = { viewModel.registrarNacimiento() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga, // Deshabilitar mientras carga
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A7C59),
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text(
                        "Registrar Nacimiento",
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