package com.example.terrabit_app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.data.network.modelos.Identificadores
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
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F7FA) // Fondo más suave
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Espaciado superior
            Spacer(modifier = Modifier.height(20.dp))

            // Card contenedor del formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large // Bordes más redondeados
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp), // Más padding interno
                    verticalArrangement = Arrangement.spacedBy(24.dp) // Más espacio entre elementos
                ) {
                    // ID Madre
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "ID Madre *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B), // Color más oscuro y legible
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
                                    color = Color(0xFF94A3B8) // Placeholder más suave
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { /* Acción de cámara */ }) {
                                    Icon(
                                        Icons.Outlined.CameraAlt,
                                        contentDescription = "Escanear",
                                        tint = Color(0xFF1565C0) // Color azul principal
                                    )
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1565C0),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B)
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
                                        tint = Color(0xFF1565C0)
                                    )
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1565C0),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B)
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
                        OutlinedTextField(
                            value = fechaNacimiento,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.mostrarDatePicker() },
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
                                    tint = Color(0xFF1565C0)
                                )
                            },
                            enabled = false,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color(0xFF1E293B),
                                disabledBorderColor = Color(0xFFCBD5E1),
                                disabledLeadingIconColor = Color(0xFF1565C0),
                                disabledPlaceholderColor = Color(0xFF94A3B8)
                            ),
                            singleLine = true
                        )
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
                                    focusedBorderColor = Color(0xFF1565C0),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = sexoExpandido,
                                onDismissRequest = { viewModel.cerrarSexoMenu() }
                            ) {
                                viewModel.listaSexos.forEach { sexo ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                sexo,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B)
                                            )
                                        },
                                        onClick = { viewModel.seleccionarSexo(sexo) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        )
                                    )
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
                                    focusedBorderColor = Color(0xFF1565C0),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = razaExpandida,
                                onDismissRequest = { viewModel.cerrarRazaMenu() }
                            ) {
                                viewModel.listaRazas.forEach { raza ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                raza,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B)
                                            )
                                        },
                                        onClick = { viewModel.seleccionarRaza(raza) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        )
                                    )
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
                                    focusedBorderColor = Color(0xFF1565C0),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = aptitudExpandida,
                                onDismissRequest = { viewModel.cerrarAptitudMenu() }
                            ) {
                                viewModel.listaAptitudes.forEach { aptitud ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                aptitud,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B)
                                            )
                                        },
                                        onClick = { viewModel.seleccionarAptitud(aptitud) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Botón Registrar
            Button(
                onClick = { viewModel.registrarNacimiento() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .height(56.dp), // Altura estándar Material Design
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    disabledContainerColor = Color(0xFFE2E8F0)
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                ),
                enabled = viewModel.esFormularioValido()
            ) {
                Text(
                    "Registrar Nacimiento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            // Espaciado inferior
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}