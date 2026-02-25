package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
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
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarGuiaPorcinos(
    navController: NavController,
    viewModelEditarGuias: EditarGuiaPorcinosViewModel,
    viewModelGestionarGuias: GestionarGuiasViewModel
) {
    val uiStateEdita by viewModelEditarGuias.uiState.collectAsState()
    val uiStateLista by viewModelGestionarGuias.uiState.collectAsState()

    // 1. Cargamos los datos de la lista al formulario al entrar
    LaunchedEffect(Unit) {
        uiStateLista.guiaSeleccionada?.let { guia ->
            viewModelEditarGuias.cargarDatosGuia(guia)
        }
    }

    val elementosConCodigos = ElementosConCodigosPorcinos()

    // DatePickerDialog para fecha de salida
    if (uiStateEdita.mostrarDatePickerSalida) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModelEditarGuias.ocultarDatePickerSalida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModelEditarGuias.seleccionarFechaSalida(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModelEditarGuias.ocultarDatePickerSalida() }) {
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

    // TimePickerDialog para fecha de salida
    if (uiStateEdita.mostrarTimePickerSalida) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModelEditarGuias.ocultarTimePickerSalida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModelEditarGuias.actualizarHoraSalida(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModelEditarGuias.ocultarTimePickerSalida()
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModelEditarGuias.ocultarTimePickerSalida() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor = MainOrange
                    )
                )
            }
        )
    }

    // DatePickerDialog para fecha de llegada
    if (uiStateEdita.mostrarDatePickerLlegada) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModelEditarGuias.ocultarDatePickerLlegada() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModelEditarGuias.seleccionarFechaLlegada(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModelEditarGuias.ocultarDatePickerLlegada() }) {
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

    // TimePickerDialog para fecha de llegada
    if (uiStateEdita.mostrarTimePickerLlegada) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModelEditarGuias.ocultarTimePickerLlegada() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModelEditarGuias.actualizarHoraLlegada(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModelEditarGuias.ocultarTimePickerLlegada()
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModelEditarGuias.ocultarTimePickerLlegada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor = MainOrange
                    )
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.card_confirm_edit_guias_porcinos),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
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
                    // Codigo de Categoria
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.form_porcinos_cod_cat),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = uiStateEdita.categoriaExpandido,
                            onExpandedChange = { viewModelEditarGuias.toggleCategoriaExpandido() }
                        ) {
                            OutlinedTextField(
                                value = uiStateEdita.categoriaSeleccionada,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.form_porcinos_descr_cat),
                                        color = BlueGrey
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = uiStateEdita.categoriaExpandido
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
                                expanded = uiStateEdita.categoriaExpandido,
                                onDismissRequest = { viewModelEditarGuias.cerrarCategoriaMenu() },
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
                                        onClick = { viewModelEditarGuias.seleccionarCategoria(categoria, codigo) },
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
                            text = stringResource(R.string.form_porcinos_nAnimales),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiStateEdita.numAnimales,
                            onValueChange = { viewModelEditarGuias.actualizarNumAnimales(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.form_porcinos_descr_nAnimales),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.form_date_departure),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModelEditarGuias.mostrarDatePickerSalida() }
                            ) {
                                OutlinedTextField(
                                    value = uiStateEdita.fechaSalida,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_porcinos_descr_fechaS),
                                            color = BlueGrey
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = stringResource(R.string.form_porcinos_descr_fechaS),
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

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.form_hour_arrival),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModelEditarGuias.mostrarTimePickerSalida() }
                            ) {
                                OutlinedTextField(
                                    value = uiStateEdita.horaSalida,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = stringResource(R.string.form_hour_arrival_description),
                                            color = BlueGrey
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = stringResource(R.string.form_hour_arrival_description),
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
                    }

                    // Fecha de Llegada
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.form_date_arrival),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModelEditarGuias.mostrarDatePickerLlegada() }
                            ) {
                                OutlinedTextField(
                                    value = uiStateEdita.fechaLlegada,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_porcinos_descr_fechaLl),
                                            color = BlueGrey
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = stringResource(R.string.form_porcinos_descr_fechaLl),
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

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.form_hour_arrival),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModelEditarGuias.mostrarTimePickerLlegada() }
                            ) {
                                OutlinedTextField(
                                    value = uiStateEdita.horaLlegada,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_hour_arrival_description),
                                            color = BlueGrey
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = stringResource(R.string.form_hour_arrival_description),
                                            tint =MainOrange
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
                    }

                    // Opcional - Código SIR
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.form_porcinos_cod_sir),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiStateEdita.codigoSIR,
                            onValueChange = { viewModelEditarGuias.actualizarCodigoSIR(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.form_porcinos_descr_sir),
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

                    // Opcional - Matrícula
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.form_porcinos_matricula),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiStateEdita.matricula,
                            onValueChange = { viewModelEditarGuias.actualizarMatricula(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.form_porcinos_descr_matricula),
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
                            text = stringResource(R.string.form_porcinos_nifCond),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiStateEdita.nifConductor,
                            onValueChange = { viewModelEditarGuias.actualizarNifConductor(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.form_porcinos_descr_nifCond),
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

                    // Botón Editar
                    Button(
                        onClick = {
                            viewModelEditarGuias.editarYConfirmarGuia {
                                navController.popBackStack()
                            }
                        },
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
                            text = stringResource(R.string.form_porcinos_edit_confirm),
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