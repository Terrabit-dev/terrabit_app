package com.example.terrabit_app.ui.pantallas

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.terrabit_app.viewmodel.porcinos.CrearGuiaPorcinosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearGuiasPorcinos(
    navController: NavController
) {
    val viewModel = viewModel<CrearGuiaPorcinosViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val elementosConCodigos = ElementosConCodigosPorcinos()
    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)

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

    // TimePickerDialog para fecha de salida
    if (uiState.mostrarTimePickerSalida) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerSalida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.actualizarHoraSalida(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModel.ocultarTimePickerSalida()
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerSalida() }) {
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

    // TimePickerDialog para fecha de llegada
    if (uiState.mostrarTimePickerLlegada) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerLlegada() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.actualizarHoraLlegada(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModel.ocultarTimePickerLlegada()
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerLlegada() }) {
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
                            text = stringResource(R.string.card_crear_guias),
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CodiMoSelector(
                            codisMos = codiMoViewModel.getCodisMos(),
                            seleccionado = null, // cuando tenga estado: codiMoViewModel.seleccionado
                            expanded = codisMoExpandido,
                            onToggle = { codiMoViewModel.toggleCodisMoExpandido() },
                            onDismiss = { codiMoViewModel.cerrarCodisMo() },
                            onSeleccionar = { codi -> /* acción  futura*/ },
                            accentColor = MainOrange
                        )
                    }
                    // Explotación de Entrada
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_expl_entrada),
                        valor = uiState.explotacion,
                        placeholder = stringResource(R.string.form_porcinos_cod_expl_entrada),
                        onValueChange = { viewModel.actualizarExplotacion(it) },
                        defectColor = false
                    )


                    // Codigo de Categoria

                    DropdownField(
                        label = stringResource(R.string.form_porcinos_cod_cat),
                        selectedValue = uiState.categoriaSeleccionada,
                        expanded = uiState.categoriaExpandido,
                        placeholder = stringResource(R.string.form_porcinos_descr_cat),
                        opciones = elementosConCodigos.categorias(),
                        onExpandedChange = { viewModel.toggleCategoriaExpandido() },
                        onDismissRequest = { viewModel.cerrarCategoriaMenu() },
                        onSeleccionar = { codigo, nombre -> viewModel.seleccionarCategoria(nombre, codigo) },
                        defectColor = false
                    )


                    // Número de Animales
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_nAnimales),
                        valor = uiState.numAnimales,
                        placeholder = stringResource(R.string.form_porcinos_descr_nAnimales),
                        keyboardType = KeyboardType.Number,
                        onValueChange = { viewModel.actualizarNumAnimales(it) },
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
                                    .clickable { viewModel.mostrarDatePickerSalida() }
                            ) {
                                OutlinedTextField(
                                    value = uiState.fechaSalida,
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
                                    .clickable { viewModel.mostrarTimePickerSalida() }
                            ) {
                                OutlinedTextField(
                                    value = uiState.horaSalida,
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
                                    .clickable { viewModel.mostrarDatePickerLlegada() }
                            ) {
                                OutlinedTextField(
                                    value = uiState.fechaLlegada,
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
                                    .clickable { viewModel.mostrarTimePickerLlegada() }
                            ) {
                                OutlinedTextField(
                                    value = uiState.horaLlegada,
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
                        valor = uiState.codigoSIR,
                        placeholder = stringResource(R.string.form_porcinos_descr_sir),
                        onValueChange = { viewModel.actualizarCodigoSIR(it) },
                        defectColor = false
                    )


                    // Opcional - Medio de Transporte
                    DropdownField(
                        label = stringResource(R.string.form_porcinos_medio_trans),
                        selectedValue = uiState.medioTransporteSeleccionado,
                        expanded = uiState.medioTransporteExpandido,
                        placeholder = stringResource(R.string.form_porcinos_descr_MTransp),
                        opciones = elementosConCodigos.medios(),
                        onExpandedChange = { viewModel.toggleMedioTransporteExpandido() },
                        onDismissRequest = { viewModel.cerrarMedioTransporteMenu() },
                        onSeleccionar = { codigo, nombre -> viewModel.seleccionarMedioTransporte(nombre, codigo) },
                        defectColor = false
                    )

                    // Opcional - Matrícula
                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_matricula),
                        valor = uiState.matricula,
                        placeholder = stringResource(R.string.form_porcinos_descr_matricula),
                        onValueChange = { viewModel.actualizarMatricula(it) },
                        defectColor = false
                    )


                    // Opcional - NIF Conductor

                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_nifCond),
                        valor = uiState.nifConductor,
                        placeholder = stringResource(R.string.form_porcinos_descr_nifCond),
                        onValueChange = { viewModel.actualizarNifConductor(it) },
                        defectColor = false
                    )


                    // Botón Registrar
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.crearGuia()
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
                            text = stringResource(R.string.card_crear_guias),
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