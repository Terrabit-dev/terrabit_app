package com.example.terrabit_app.ui.screen

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.viewmodel.MovimientosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Movimientos(navController: NavController, viewModel: MovimientosViewModel) {
    // Observar variables del ViewModel
    val codiRemo by viewModel.codiRemo.observeAsState("")
    val dataArribada by viewModel.dataArribada.observeAsState("")
    val horaArribada by viewModel.horaArribada.observeAsState("")
    val codiAtes by viewModel.codiAtes.observeAsState("")
    val nomTransportista by viewModel.nomTransportista.observeAsState("")
    val matricula by viewModel.matricula.observeAsState("")
    val mitjaTransport by viewModel.mitjaTransport.observeAsState("")
    val nifConductor by viewModel.nifConductor.observeAsState("")
    val nomConductor by viewModel.nomConductor.observeAsState("")
    val explotacioDestinacio by viewModel.explotacioDestinacio.observeAsState("")
    val identificadorAnimal by viewModel.identificadorAnimal.observeAsState("")
    val estatArribada by viewModel.estatArribada.observeAsState("")

    // Estados de expansión
    val codiAtesExpandido by viewModel.codiAtesExpandido.observeAsState(false)
    val mitjaTransportExpandido by viewModel.mitjaTransportExpandido.observeAsState(false)
    val estatArribadaExpandido by viewModel.estatArribadaExpandido.observeAsState(false)

    // DatePicker y TimePicker
    val mostrarDatePickerArribada by viewModel.mostrarDatePickerArribada.observeAsState(false)
    val mostrarTimePickerArribada by viewModel.mostrarTimePickerArribada.observeAsState(false)

    // Estados de registro
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val estadoCarga by viewModel.cargandoMovimiento.observeAsState(false)
    val codiError by viewModel.codiError.observeAsState()


    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    // Textos recurrentes
    val successMessage = stringResource(R.string.successful_message_confirm_movs)

    // Elementos con codigos (Transporte, Estado de llegada)
    val elementosConCodigos = ElementosConCodigos()

    // Efectos
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = successMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistro()
        }
    }

    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) {
            mostrarDialogoError = true
        }
    }

    // Diálogo de Error
    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoError = false
                viewModel.resetearEstadoRegistro()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MainOrange,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.error_message_confirm_movs),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkBlueGrey
                )
            },
            text = {
                Text(
                    text = if (codiError != null) {
                        alertsErrosScreens(codiError!!)
                    } else mensajeError,
                    fontSize = 16.sp,
                    color = BlueGrey,
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
                        containerColor = MainOrange
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
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerArribada() }) {
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

    // TimePickerDialog
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
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerArribada() }) {
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
                            color = MainOrange,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.loading_processing),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = BlueGrey
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
                                stringResource(R.string.name_confirm_movs),
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
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MainGreen,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
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
                            stringResource(R.string.form_movs_title_necessary),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueGrey
                        )

                        // Código REMO
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_codi_remo),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = codiRemo,
                                onValueChange = { viewModel.actualizarCodiRemo(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_codi_remo_description),
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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Fecha y Hora de Arribada
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
                                        .clickable { viewModel.mostrarDatePickerArribada() }
                                ) {
                                    OutlinedTextField(
                                        value = dataArribada,
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
                                                contentDescription = stringResource(R.string.form_date_description),
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
                                        .clickable { viewModel.mostrarTimePickerArribada() }
                                ) {
                                    OutlinedTextField(
                                        value = horaArribada,
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

                        // Código ATES
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_codi_ates),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
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
                                            stringResource(R.string.form_codi_ates_description),
                                            color = BlueGrey
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
                                        focusedBorderColor = MainOrange,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey
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
                                                    color = DarkBlueGrey,
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

                        // Explotación Destino
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_exploitation_destination),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = explotacioDestinacio,
                                onValueChange = { viewModel.actualizarExplotacioDestinacio(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_exploitation_destination_description),
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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Identificador del Animal
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_id_animal),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = identificadorAnimal,
                                onValueChange = { viewModel.actualizarIdentificadorAnimal(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_id_animal_description),
                                        color = BlueGrey
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { /* Acción de cámara */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = stringResource(R.string.form_id_animal_description),
                                            tint = MainOrange
                                        )
                                    }
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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Estado de Arribada
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_state_arrival),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = estatArribadaExpandido,
                                onExpandedChange = { viewModel.toggleEstatArribadaExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = estatArribada,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_state_arrival_description),
                                            color = BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = estatArribadaExpandido
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
                                    expanded = estatArribadaExpandido,
                                    onDismissRequest = { viewModel.cerrarEstatArribadaMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                   elementosConCodigos.EstadosLlegada().forEach { (estat, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    estat,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarEstatArribada(estat, codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                        if (estat != viewModel.listaEstatArribada.last()) {
                                            HorizontalDivider(
                                                color = DarkWhiteBackground,
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card de Datos Opcionales
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
                            stringResource(R.string.form_movs_title_optionals),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueGrey
                        )

                        // Medio de Transporte
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_ways_transports),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
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
                                            stringResource(R.string.form_ways_transports_description),
                                            color = BlueGrey
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
                                        focusedBorderColor = MainOrange,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = mitjaTransportExpandido,
                                    onDismissRequest = { viewModel.cerrarMitjaTransportMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    elementosConCodigos.Transporte().forEach { (medio, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    medio,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarMitjaTransport(medio, codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )

                                    }
                                }
                            }
                        }

                        // Matrícula
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_matricule_transport),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = matricula,
                                onValueChange = { viewModel.actualizarMatricula(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_matricule_transports_description),
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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Nombre Transportista
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_name_transportits),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = nomTransportista,
                                onValueChange = { viewModel.actualizarNomTransportista(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_name_transportits_description),
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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // NIF Conductor
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_nif_driver),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = nifConductor,
                                onValueChange = { viewModel.actualizarNifConductor(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_nif_driver_description),
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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        // Nombre Conductor
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_name_driver),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = nomConductor,
                                onValueChange = { viewModel.actualizarNomConductor(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_name_driver_description),
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
                                    imeAction = ImeAction.Done
                                )
                            )
                        }
                    }
                }

                // Botón Confirmar Movimiento
                Button(
                    onClick = { viewModel.confirmarMovimiento() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga,
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
                        stringResource(R.string.buttom_form_confirm_movs),
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