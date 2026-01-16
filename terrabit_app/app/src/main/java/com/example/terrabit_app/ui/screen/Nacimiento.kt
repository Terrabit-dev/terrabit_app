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
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nacimiento(navController: NavController, viewModel: MainViewmodel) {
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

    // Observar identificadores
    val identificadores: Identificadores by viewModel.identificadores.observeAsState(
        Identificadores(emptyList())
    )

    // Observar estado de registro para mostrar mensajes
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar cuando hay mensaje de éxito o error
    LaunchedEffect(registroExitoso, mensajeError) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = "✅ Nacimiento registrado exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistro()
        } else if (mensajeError.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = mensajeError,
                duration = SnackbarDuration.Long
            )
            viewModel.resetearEstadoRegistro()
        }
    }

    // Obtener identificadores al cargar la pantalla
    LaunchedEffect(Unit) {
        viewModel.getIdentificadores("S0800608B", "L1855m58", "1410AK")
    }

    // DatePickerDialog
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
                            // CORRECCIÓN 1: Configurar tipo de teclado y acciones IME
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
                            // CORRECCIÓN 2: Configurar tipo de teclado y acciones IME
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Fecha de Nacimiento - CORRECCIÓN 3: Usar Box con clickable
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
                                viewModel.listaRazas.forEach { raza ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                raza,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B),
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        onClick = { viewModel.seleccionarRaza(raza) },
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
                                    if (raza != viewModel.listaRazas.last()) {
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

            // Botón Registrar - SIEMPRE HABILITADO
            Button(
                onClick = { viewModel.registrarNacimiento() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A7C59)
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