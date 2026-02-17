package com.example.terrabit_app.ui.screen.bovinos

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.viewmodel.GuiasViewModel
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionGuias(navController: NavController, viewModel: GuiasViewModel, borradorId: String = "") {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val explotacioOrigen by viewModel.explotacioOrigen.observeAsState("")
    val explotacioDestinacio by viewModel.explotacioDestinacio.observeAsState("")
    val temporal by viewModel.temporal.observeAsState("")
    val dataSortida by viewModel.dataSortida.observeAsState("")
    val horaSortida by viewModel.horaSortida.observeAsState("")
    val dataArribada by viewModel.dataArribada.observeAsState("")
    val horaArribada by viewModel.horaArribada.observeAsState("")
    val mobilitat by viewModel.mobilitat.observeAsState("")

    val codiAtes by viewModel.codiAtes.observeAsState("")
    val nomTransportista by viewModel.nomTransportista.observeAsState("")
    val mitjaTransport by viewModel.mitjaTransport.observeAsState("")
    val matricula by viewModel.matricula.observeAsState("")
    val nifConductor by viewModel.nifConductor.observeAsState("")
    val nomConductor by viewModel.nomConductor.observeAsState("")
    val identificadors by viewModel.identificadors.observeAsState(listOf(""))

    val temporalExpandido by viewModel.temporalExpandido.observeAsState(false)
    val mobilitatExpandido by viewModel.mobilitatExpandido.observeAsState(false)
    val mitjaTransportExpandido by viewModel.mitjaTransportExpandido.observeAsState(false)

    val mostrarDatePickerSortida by viewModel.mostrarDatePickerSortida.observeAsState(false)
    val mostrarTimePickerSortida by viewModel.mostrarTimePickerSortida.observeAsState(false)
    val mostrarDatePickerArribada by viewModel.mostrarDatePickerArribada.observeAsState(false)
    val mostrarTimePickerArribada by viewModel.mostrarTimePickerArribada.observeAsState(false)

    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val estadoCarga by viewModel.cargandoGuia.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarDialogoAviso by remember { mutableStateOf(false) }
    var cantidadBorradores by remember { mutableStateOf(0) }

    val codiError by viewModel.codiError.observeAsState()

    val successMessage = stringResource(R.string.success_create_guide)
    val datePlaceholder = stringResource(R.string.form_date_description)
    val hourPlaceholder = stringResource(R.string.form_hour_arrival_description)

    val elementosConCodigos = ElementosConCodigos()

    // ============================================
    // INICIALIZACIÓN Y DETECCIÓN DE BORRADORES
    // ============================================
    LaunchedEffect(Unit) {
        viewModel.inicializarSharedPreferences(context)

        if (borradorId.isNotEmpty()) {
            viewModel.cargarBorradorPorId(borradorId)
            return@LaunchedEffect
        }

        val borradores = viewModel.obtenerBorradoresGuia()
        cantidadBorradores = borradores.size
        if (cantidadBorradores >= 2) {
            mostrarDialogoAviso = true
        }
    }

    // ============================================
    // DIÁLOGO DE AVISO DE BORRADORES
    // ============================================
    if (mostrarDialogoAviso) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MainOrange,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Borradores pendientes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkBlueGrey
                )
            },
            text = {
                Text(
                    text = "Tienes $cantidadBorradores borradores guardados de este formulario. Puedes verlos en la página de Borradores.\n\n¿Deseas crear uno nuevo?",
                    fontSize = 16.sp,
                    color = BlueGrey,
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoAviso = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE28F41)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Crear nuevo", fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ============================================
    // DETECCIÓN DE CICLO DE VIDA (AUTOGUARDADO)
    // ============================================
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (viewModel.tieneContenido()) {
                        viewModel.guardarBorradorAutomatico()
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                    text = stringResource(R.string.error_create_guide),
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

    if (mostrarDatePickerSortida) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerSortida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaSortida(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerSortida() }) {
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

    if (mostrarTimePickerSortida) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerSortida() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.actualizarHoraSortida(
                            timePickerState.hour.toString(),
                            timePickerState.minute.toString()
                        )
                        viewModel.ocultarTimePickerSortida()
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerSortida() }) {
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
                            "Procesando...",
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
                                stringResource(R.string.name_manage_guides),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Routes.GuiasMovimientos.route) }) {
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

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_origin_exploitation),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = explotacioOrigen,
                                onValueChange = { viewModel.actualizarExplotacioOrigen(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_format_mo_rega),
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
                                        stringResource(R.string.form_format_mo_rega),
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

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_temporal),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = temporalExpandido,
                                onExpandedChange = { viewModel.toggleTemporalExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = temporal,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_yes_no),
                                            color = BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = temporalExpandido
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
                                    expanded = temporalExpandido,
                                    onDismissRequest = { viewModel.cerrarTemporalMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    elementosConCodigos.opcionesSiNo().forEach { (opcion, codi) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    opcion,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarTemporal(opcion, codi) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

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
                                        .clickable { viewModel.mostrarDatePickerSortida() }
                                ) {
                                    OutlinedTextField(
                                        value = dataSortida,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                datePlaceholder,
                                                color = BlueGrey
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = datePlaceholder,
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
                                        .clickable { viewModel.mostrarTimePickerSortida() }
                                ) {
                                    OutlinedTextField(
                                        value = horaSortida,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                hourPlaceholder,
                                                color = BlueGrey
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = hourPlaceholder,
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
                                                datePlaceholder,
                                                color = BlueGrey
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = datePlaceholder,
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
                                                hourPlaceholder,
                                                color = BlueGrey
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = hourPlaceholder,
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

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_mobility_guide),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = mobilitatExpandido,
                                onExpandedChange = { viewModel.toggleMobilitatExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = mobilitat,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_yes_no),
                                            color =BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = mobilitatExpandido
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
                                    expanded = mobilitatExpandido,
                                    onDismissRequest = { viewModel.cerrarMobilitatMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    elementosConCodigos.opcionesSiNo().forEach { (opcion, codi) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    opcion,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarMobilitat(opcion, codi) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        ParametrosCentroInspeccion(viewModel)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_codi_ates),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = codiAtes,
                                onValueChange = { if (it.length <= 15) viewModel.campoCodiAtes(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_ates_max_chars),
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
                                        stringResource(R.string.form_transport_name_warning),
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
                                    elementosConCodigos.transporte().forEach { (medio, codi) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    medio,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarMitjaTransport(medio, codi) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

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
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stringResource(R.string.form_animal_identifiers),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlueGrey,
                                    letterSpacing = 0.15.sp
                                )
                                IconButton(
                                    onClick = { viewModel.agregarIdentificador() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.content_desc_add_id),
                                        tint = MainOrange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            identificadors.forEachIndexed { index, identificador ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = identificador,
                                        onValueChange = { viewModel.actualizarIdentificador(index, it) },
                                        modifier = Modifier.weight(1f),
                                        placeholder = {
                                            Text(
                                                stringResource(R.string.form_animal_id_example),
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
                                            imeAction = if (index == identificadors.lastIndex) ImeAction.Done else ImeAction.Next
                                        )
                                    )

                                    if (identificadors.size > 1) {
                                        IconButton(
                                            onClick = { viewModel.eliminarIdentificador(index) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = stringResource(R.string.content_desc_remove_id),
                                                tint = ErrorRed
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(36.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.confirmarAltaGuia() },
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
                        stringResource(R.string.btn_create_guide),
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

@Composable
fun ParametrosCentroInspeccion(viewModel: GuiasViewModel) {

    val pais by viewModel.pais.observeAsState("")
    val codiExplotacio by viewModel.codiExplotacio.observeAsState("")
    val (isChecked, setChecked) = remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { setChecked(it) }
            )
            Text("El destino es centro de inspección?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
                letterSpacing = 0.15.sp
            )
        }

        if (isChecked) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.form_pif_country),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    letterSpacing = 0.15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = pais,
                    onValueChange = { viewModel.actualizarPais(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.form_pif_country_desc),
                            color = Color(0xFF94A3B8)
                        )
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE28F41),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color(0xFF1E293B),
                        unfocusedTextColor = Color(0xFF1E293B),
                        cursorColor = Color(0xFFE28F41)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.form_pif_exploitation),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    letterSpacing = 0.15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = codiExplotacio,
                    onValueChange = { viewModel.actualizarCodiExplotacio(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.form_pif_exploitation_desc),
                            color = Color(0xFF94A3B8)
                        )
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE28F41),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color(0xFF1E293B),
                        unfocusedTextColor = Color(0xFF1E293B),
                        cursorColor = Color(0xFFE28F41)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }
    }
}