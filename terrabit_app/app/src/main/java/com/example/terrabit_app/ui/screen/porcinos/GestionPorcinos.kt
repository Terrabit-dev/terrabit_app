package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.example.terrabit_app.viewmodel.porcinos.GestionGuiaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPorcinos(
    navController: NavController,
    viewModel: GestionGuiaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val elementosConCodigos = ElementosConCodigosPorcinos()

    // DatePickerDialog para fecha de salida
    if (uiState.mostrarDatePickerSalida) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerSalida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaSalida(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerSalida() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainOrange,
                    todayDateBorderColor = MainOrange
                )
            )
        }
    }

    // DatePickerDialog para fecha de llegada
    if (uiState.mostrarDatePickerLlegada) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerLlegada() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaLlegada(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerLlegada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainOrange,
                    todayDateBorderColor = MainOrange
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
                            "Crear Guía",
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
                    containerColor = MainOrange,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = WhiteBackground
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
                    // Explotación de Entrada
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Explotación de Entrada *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.explotacion,
                            onValueChange = { viewModel.actualizarExplotacion(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Introducir código de explotación de entrada",
                                    color = BlueGrey
                                )
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainOrange,
                                unfocusedBorderColor = DarkWhiteBackground,
                                focusedTextColor = DarkBlueGrey,
                                unfocusedTextColor = DarkBlueGrey,
                                cursorColor = MainOrange
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Codigo de Categoria
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Código de Categoría *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = uiState.categoriaExpandido,
                            onExpandedChange = { viewModel.toggleCategoriaExpandido() }
                        ) {
                            OutlinedTextField(
                                value = uiState.categoriaSeleccionada,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        text = "Seleccionar categoria",
                                        color = BlueGrey
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = uiState.categoriaExpandido
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainOrange,
                                    unfocusedBorderColor = DarkWhiteBackground,
                                    focusedTextColor = DarkBlueGrey,
                                    unfocusedTextColor = DarkBlueGrey
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.categoriaExpandido,
                                onDismissRequest = { viewModel.cerrarCategoriaMenu() },
                                modifier = Modifier
                                    .background(Color.White)
                            ) {
                                elementosConCodigos.categorias().forEach { (categoria, codigo) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                categoria,
                                                fontSize = 15.sp,
                                                color = DarkBlueGrey,
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        onClick = { viewModel.seleccionarCategoria(categoria, codigo) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 14.dp
                                        ),
                                        colors = MenuDefaults.itemColors(
                                            textColor = DarkBlueGrey,
                                            leadingIconColor = DarkBlueGrey,
                                            trailingIconColor = DarkBlueGrey,
                                            disabledTextColor = BlueGrey
                                        )
                                    )
                                }


                            }
                        }
                    }

                    // Número de Animales
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Número de Animales *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.numAnimales,
                            onValueChange = { viewModel.actualizarNumAnimales(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Introducir número de animales",
                                    color = BlueGrey
                                )
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainOrange,
                                unfocusedBorderColor = DarkWhiteBackground,
                                focusedTextColor = DarkBlueGrey,
                                unfocusedTextColor = DarkBlueGrey,
                                cursorColor = MainOrange
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Fecha de Salida
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Fecha de salida *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.mostrarDatePickerSalida() }
                        ) {
                            OutlinedTextField(
                                value = uiState.fechaSalida,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "Seleccionar fecha de salida",
                                        color = BlueGrey
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Calendario",
                                        tint = MainOrange
                                    )
                                },
                                readOnly = true,
                                enabled = false,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = DarkBlueGrey,
                                    disabledBorderColor = DarkWhiteBackground,
                                    disabledLeadingIconColor = MainOrange,
                                    disabledPlaceholderColor = BlueGrey
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Fecha de Llegada
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Fecha de llegada *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.mostrarDatePickerLlegada() }
                        ) {
                            OutlinedTextField(
                                value = uiState.fechaLlegada,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "Seleccionar fecha de salida",
                                        color = BlueGrey
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Calendario",
                                        tint = MainOrange
                                    )
                                },
                                readOnly = true,
                                enabled = false,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = DarkBlueGrey,
                                    disabledBorderColor = DarkWhiteBackground,
                                    disabledLeadingIconColor = MainOrange,
                                    disabledPlaceholderColor = BlueGrey
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Opcional - Código SIR
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Código SIR",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.codigoSIR,
                            onValueChange = { viewModel.actualizarCodigoSIR(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Introducir código SIR",
                                    color = BlueGrey
                                )
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainOrange,
                                unfocusedBorderColor = DarkWhiteBackground,
                                focusedTextColor = DarkBlueGrey,
                                unfocusedTextColor = DarkBlueGrey,
                                cursorColor = MainOrange
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Opcional - Medio de Transporte
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Medio de Transporte",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = uiState.medioTransporteExpandido,
                            onExpandedChange = { viewModel.toggleMedioTransporteExpandido() }
                        ) {
                            OutlinedTextField(
                                value = uiState.medioTransporteSeleccionado,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        text = "Seleccionar medio de transporte",
                                        color = BlueGrey
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = uiState.medioTransporteExpandido
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainOrange,
                                    unfocusedBorderColor = DarkWhiteBackground,
                                    focusedTextColor = DarkBlueGrey,
                                    unfocusedTextColor = DarkBlueGrey
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = uiState.medioTransporteExpandido,
                                onDismissRequest = { viewModel.cerrarMedioTransporteMenu() },
                                modifier = Modifier
                                    .background(Color.White)
                            ) {
                                elementosConCodigos.medios().forEach { (medioTransporte, codigo) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                medioTransporte,
                                                fontSize = 15.sp,
                                                color = DarkBlueGrey,
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        onClick = { viewModel.seleccionarMedioTransporte(medioTransporte, codigo) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 14.dp
                                        ),
                                        colors = MenuDefaults.itemColors(
                                            textColor = DarkBlueGrey,
                                            leadingIconColor = DarkBlueGrey,
                                            trailingIconColor = DarkBlueGrey,
                                            disabledTextColor = BlueGrey
                                        )
                                    )
                                }


                            }
                        }
                    }

                    // Opcional - Matrícula
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Matrícula",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.matricula,
                            onValueChange = { viewModel.actualizarMatricula(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Introducir Matrícula",
                                    color = BlueGrey
                                )
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainOrange,
                                unfocusedBorderColor = DarkWhiteBackground,
                                focusedTextColor = DarkBlueGrey,
                                unfocusedTextColor = DarkBlueGrey,
                                cursorColor = MainOrange
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Opcional - NIF Conductor
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "NIF del Conductor",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.nifConductor,
                            onValueChange = { viewModel.actualizarNifConductor(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Introducir NIF del conductor",
                                    color = BlueGrey
                                )
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainOrange,
                                unfocusedBorderColor = DarkWhiteBackground,
                                focusedTextColor = DarkBlueGrey,
                                unfocusedTextColor = DarkBlueGrey,
                                cursorColor = MainOrange
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Botón Registrar
                    Button(
                        onClick = { viewModel.crearGuia() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainOrange,
                            disabledContainerColor = DarkWhiteBackground
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Text(
                            text = "Crear Guia",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
            Spacer(Modifier.padding(10.dp))
        }
    }
}