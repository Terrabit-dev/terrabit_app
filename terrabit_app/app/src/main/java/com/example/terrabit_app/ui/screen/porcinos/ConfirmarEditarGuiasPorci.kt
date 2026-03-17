package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.example.terrabit_app.viewmodel.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarEditarGuiasPorci(
    navController: NavController,
    viewModelGestionarGuias : GestionarGuiasViewModel,
    viewModelEditarGuias : EditarGuiaPorcinosViewModel
) {
    val uiStateEdita by viewModelEditarGuias.uiState.collectAsState()
    val uiStateLista by viewModelGestionarGuias.uiState.collectAsState()

    Log.d("Guia seleccionada", "Nose: ${uiStateLista.guiaSeleccionada}  ")
    // 1. Cargamos los datos de la lista al formulario al entrar
    LaunchedEffect(Unit) {
        uiStateLista.guiaSeleccionada?.let { guia ->
            viewModelEditarGuias.cargarDatosGuia(guia)
            Log.d("Datos Guia", "guia: '$guia'  - ${guia.remo}")
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
                    DropdownField(
                        label = stringResource(R.string.form_porcinos_cod_cat),
                        selectedValue = uiStateEdita.categoriaSeleccionada,
                        expanded = uiStateEdita.categoriaExpandido,
                        placeholder = stringResource(R.string.form_porcinos_descr_cat),
                        opciones = elementosConCodigos.categorias(),
                        onExpandedChange = { viewModelEditarGuias.toggleCategoriaExpandido() },
                        onDismissRequest = { viewModelEditarGuias.cerrarCategoriaMenu() },
                        onSeleccionar = { codigo, nombre -> viewModelEditarGuias.seleccionarCategoria(nombre, codigo) },
                        defectColor = false
                    )


                    // Número de Animales
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_nAnimales),
                        valor = uiStateEdita.numAnimales,
                        placeholder = stringResource(R.string.form_porcinos_descr_nAnimales),
                        keyboardType = KeyboardType.Number,
                        onValueChange = { viewModelEditarGuias.actualizarNumAnimales(it) },
                        defectColor = false
                    )

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
                                            stringResource(R.string.form_date_arrival_description),
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
                                            stringResource(R.string.form_date_arrival_description),
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
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_cod_sir),
                        valor = uiStateEdita.codigoSIR,
                        placeholder = stringResource(R.string.form_porcinos_descr_sir),
                        onValueChange = { viewModelEditarGuias.actualizarCodigoSIR(it) },
                        defectColor = false
                    )

                    // Opcional - Matrícula
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_matricula),
                        valor = uiStateEdita.matricula,
                        placeholder = stringResource(R.string.form_porcinos_descr_matricula),
                        onValueChange = { viewModelEditarGuias.actualizarMatricula(it) },
                        defectColor = false
                    )


                    // Opcional - NIF Conductor
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_nifCond),
                        valor = uiStateEdita.nifConductor,
                        placeholder = stringResource(R.string.form_porcinos_descr_nifCond),
                        onValueChange = { viewModelEditarGuias.actualizarNifConductor(it) },
                        defectColor = false
                    )

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