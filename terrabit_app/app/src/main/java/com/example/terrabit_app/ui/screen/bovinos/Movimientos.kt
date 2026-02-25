package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.viewmodel.MovimientosViewModel
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.ui.screen.bovinos.components.AutoCompleteBovinoField
import com.example.terrabit_app.ui.screen.bovinos.components.useDebounce
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Movimientos(navController: NavController, bluetoothViewModel: BluetoothViewModel, borradorId: String = "") {
    val viewModel = viewModel<MovimientosViewModel>()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    val codiAtesExpandido by viewModel.codiAtesExpandido.observeAsState(false)
    val mitjaTransportExpandido by viewModel.mitjaTransportExpandido.observeAsState(false)
    val estatArribadaExpandido by viewModel.estatArribadaExpandido.observeAsState(false)

    val mostrarDatePickerArribada by viewModel.mostrarDatePickerArribada.observeAsState(false)
    val mostrarTimePickerArribada by viewModel.mostrarTimePickerArribada.observeAsState(false)

    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val estadoCarga by viewModel.cargandoMovimiento.observeAsState(false)
    val codiError by viewModel.codiError.observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarDialogoAviso by remember { mutableStateOf(false) }
    var cantidadBorradores by remember { mutableStateOf(0) }

    val successMessage = stringResource(R.string.successful_message_confirm_movs)

    val elementosConCodigos = ElementosConCodigos()

    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)
    val activeIndex by viewModel.activeFieldIndex.observeAsState(-1)

    // ============================================
    // INICIALIZACIÓN Y DETECCIÓN DE BORRADORES
    // ============================================
    LaunchedEffect(Unit) {
        viewModel.inicializarSharedPreferences(context)

        if (borradorId.isNotEmpty()) {
            viewModel.cargarBorradorPorId(borradorId)
            return@LaunchedEffect
        }

        val borradores = viewModel.obtenerBorradoresMovimiento()
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
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Borradores pendientes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text = "Tienes $cantidadBorradores borradores guardados de este formulario. Puedes verlos en la página de Borradores.\n\n¿Deseas crear uno nuevo?",
                    fontSize = 16.sp,
                    color = Color(0xFF475569),
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
                    tint = Color(0xFFE28F41),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.error_message_confirm_movs),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text = if (codiError != null) {
                        alertsErrosScreens(codiError!!)
                    } else mensajeError,
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
                        containerColor = Color(0xFFE28F41)
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
                    Text(stringResource(R.string.accept_buttom), color = Color(0xFFE28F41))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerArribada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFFE28F41),
                    todayDateBorderColor = Color(0xFFE28F41)
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
                    Text(stringResource(R.string.accept_buttom), color = Color(0xFFE28F41))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerArribada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = Color(0xFF64748B))
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor = Color(0xFFE28F41)
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
                            color = Color(0xFFE28F41),
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.loading_processing),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
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
                        IconButton(onClick = { navController.navigate(Routes.GuiasMovimientos.route)}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE28F41),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF4A7C59),
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
                            color = Color(0xFF1E293B)
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_codi_remo),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.form_date_arrival),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
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
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = stringResource(R.string.form_date_description),
                                                tint = Color(0xFFE28F41)
                                            )
                                        },
                                        readOnly = true,
                                        enabled = false,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color(0xFF1E293B),
                                            disabledBorderColor = Color(0xFFCBD5E1),
                                            disabledLeadingIconColor = Color(0xFFE28F41),
                                            disabledPlaceholderColor = Color(0xFF94A3B8)
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
                                    color = Color(0xFF1E293B),
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
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = stringResource(R.string.form_hour_arrival_description),
                                                tint = Color(0xFFE28F41)
                                            )
                                        },
                                        readOnly = true,
                                        enabled = false,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color(0xFF1E293B),
                                            disabledBorderColor = Color(0xFFCBD5E1),
                                            disabledLeadingIconColor = Color(0xFFE28F41),
                                            disabledPlaceholderColor = Color(0xFF94A3B8)
                                        ),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_codi_ates),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                                            color = Color(0xFF94A3B8)
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
                                        focusedBorderColor = Color(0xFFE28F41),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
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
                                                    color = Color(0xFF1E293B),
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

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_exploitation_destination),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                            stringResource(R.string.form_movs_title_animals),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_ways_transports),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                                            color = Color(0xFF94A3B8)
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
                                        focusedBorderColor = Color(0xFFE28F41),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = mitjaTransportExpandido,
                                    onDismissRequest = { viewModel.cerrarMitjaTransportMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    elementosConCodigos.transporte().forEach { (medio, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    medio,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E293B),
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

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_matricule_transport),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                                stringResource(R.string.form_name_transportits),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                                stringResource(R.string.form_nif_driver),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                                stringResource(R.string.form_name_driver),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
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
                                    imeAction = ImeAction.Done
                                )
                            )
                        }
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.form_movs_title_animals),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )

                            IconButton(
                                onClick = { viewModel.agregarAnimal() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFFE28F41),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar animal",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        val animales by viewModel.listaAnimales.observeAsState(emptyList())
                        val estatExpandidoPorIndice by viewModel.estatArribadaExpandidoPorIndice.observeAsState(emptyMap())
                        val classCanalExpandidoPorIndice by viewModel.classCanalExpandidoPorIndice.observeAsState(emptyMap())
                        val tipusExpandidoPorIndice by viewModel.tipusPresentacioExpandidoPorIndice.observeAsState(emptyMap())
                        val datePickerPorIndice by viewModel.mostrarDatePickerPorIndice.observeAsState(emptyMap())

                        animales.forEachIndexed { index, animal ->
                            if (datePickerPorIndice[index] == true) {
                                val datePickerState = rememberDatePickerState()
                                DatePickerDialog(
                                    onDismissRequest = { viewModel.ocultarDatePickerSacrMort(index) },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                datePickerState.selectedDateMillis?.let { millis ->
                                                    viewModel.seleccionarFechaSacrMort(index, millis)
                                                }
                                            }
                                        ) {
                                            Text(stringResource(R.string.accept_buttom), color = Color(0xFFE28F41))
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { viewModel.ocultarDatePickerSacrMort(index) }) {
                                            Text(stringResource(R.string.cancel_buttom), color = Color(0xFF64748B))
                                        }
                                    }
                                ) {
                                    DatePicker(
                                        state = datePickerState,
                                        colors = DatePickerDefaults.colors(
                                            selectedDayContainerColor = Color(0xFFE28F41),
                                            todayDateBorderColor = Color(0xFFE28F41)
                                        )
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8FAFC)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Animal ${index + 1}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE28F41)
                                        )

                                        if (animales.size > 1) {
                                            IconButton(
                                                onClick = { viewModel.eliminarAnimal(index) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar animal",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            stringResource(R.string.form_id_animal),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B),
                                            letterSpacing = 0.15.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        useDebounce(animal.identificador, delayMillis = 300L) { query ->
                                            viewModel.searchBovinos(index, query)  // Pasar el índice
                                        }

                                        AutoCompleteBovinoField(
                                            value = animal.identificador,
                                            onValueChange = {
                                                viewModel.actualizarIdentificadorAnimal(index, it)
                                            },
                                            suggestions = if (activeIndex == index) suggestionsBovinos else emptyList(),  // Solo mostrar si es el campo activo
                                            onAnimalSelected = { viewModel.onBovinoSelected(index, it) },
                                            isLoading = isLoadingBovinos,
                                            label = stringResource(R.string.form_id_animal),
                                            placeholder = stringResource(R.string.form_id_animal_description),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            stringResource(R.string.form_state_arrival),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B),
                                            letterSpacing = 0.15.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        ExposedDropdownMenuBox(
                                            expanded = estatExpandidoPorIndice[index] ?: false,
                                            onExpandedChange = {
                                                viewModel.toggleEstatArribadaExpandido(index)
                                            }
                                        ) {
                                            OutlinedTextField(
                                                value = elementosConCodigos.estadosLlegada()[animal.estatArribada] ?: "",
                                                onValueChange = {},
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor(),
                                                readOnly = true,
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.form_state_arrival_description),
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = estatExpandidoPorIndice[index] ?: false
                                                    )
                                                },
                                                singleLine = true,
                                                shape = MaterialTheme.shapes.medium,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFE28F41),
                                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                                    focusedTextColor = Color(0xFF1E293B),
                                                    unfocusedTextColor = Color(0xFF1E293B),
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                )
                                            )
                                            ExposedDropdownMenu(
                                                expanded = estatExpandidoPorIndice[index] ?: false,
                                                onDismissRequest = {
                                                    viewModel.cerrarEstatArribadaMenu(index)
                                                },
                                                modifier = Modifier.background(Color.White)
                                            ) {
                                                elementosConCodigos.estadosLlegada().forEach { (codigo, estat) ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                estat,
                                                                fontSize = 14.sp,
                                                                color = Color(0xFF1E293B),
                                                                fontWeight = FontWeight.Normal
                                                            )
                                                        },
                                                        onClick = {
                                                            viewModel.seleccionarEstatArribadaAnimal(
                                                                index, estat, codigo
                                                            )
                                                        },
                                                        contentPadding = PaddingValues(
                                                            horizontal = 16.dp,
                                                            vertical = 12.dp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (animal.estatArribada == "80") {
                                        HorizontalDivider(
                                            color = Color(0xFFE2E8F0),
                                            thickness = 1.dp
                                        )

                                        Text(
                                            stringResource(R.string.form_movs_title_sacrifate_dade),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE28F41)
                                        )

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.form_date_sacrifice),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                letterSpacing = 0.15.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.mostrarDatePickerSacrMort(index)
                                                    }
                                            ) {
                                                OutlinedTextField(
                                                    value = animal.dataSacrMort ?: "",
                                                    onValueChange = {},
                                                    modifier = Modifier.fillMaxWidth(),
                                                    placeholder = {
                                                        Text(
                                                            stringResource(R.string.form_date_description),
                                                            color = Color(0xFF94A3B8),
                                                            fontSize = 14.sp
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.DateRange,
                                                            contentDescription = "Fecha",
                                                            tint = Color(0xFFE28F41)
                                                        )
                                                    },
                                                    readOnly = true,
                                                    enabled = false,
                                                    shape = MaterialTheme.shapes.medium,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF1E293B),
                                                        disabledBorderColor = Color(0xFFCBD5E1),
                                                        disabledLeadingIconColor = Color(0xFFE28F41),
                                                        disabledPlaceholderColor = Color(0xFF94A3B8),
                                                        disabledContainerColor = Color.White
                                                    ),
                                                    singleLine = true
                                                )
                                            }
                                        }

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.form_weight_canal),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                letterSpacing = 0.15.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value = animal.pesCanal?:"",
                                                onValueChange = {
                                                    viewModel.actualizarPesCanal(index, it)
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.form_weight_canal_description),
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                singleLine = true,
                                                shape = MaterialTheme.shapes.medium,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFE28F41),
                                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                                    focusedTextColor = Color(0xFF1E293B),
                                                    unfocusedTextColor = Color(0xFF1E293B),
                                                    cursorColor = Color(0xFFE28F41),
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                ),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Decimal,
                                                    imeAction = ImeAction.Next
                                                )
                                            )
                                        }

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.form_class_canal),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                letterSpacing = 0.15.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value = animal.classCanal?:"",
                                                onValueChange = {
                                                    viewModel.actualizarClassCanal(index, it)
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.form_class_canal_description),
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                singleLine = true,
                                                shape = MaterialTheme.shapes.medium,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFE28F41),
                                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                                    focusedTextColor = Color(0xFF1E293B),
                                                    unfocusedTextColor = Color(0xFF1E293B),
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                )
                                            )
                                        }

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.form_type_presentation),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                letterSpacing = 0.15.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            ExposedDropdownMenuBox(
                                                expanded = tipusExpandidoPorIndice[index] ?: false,
                                                onExpandedChange = {
                                                    viewModel.toggleTipusPresentacioExpandido(index)
                                                }
                                            ) {
                                                OutlinedTextField(
                                                    value = elementosConCodigos.tiposPresentacion()[animal.tipusPresentacio] ?: "",
                                                    onValueChange = {},
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .menuAnchor(),
                                                    readOnly = true,
                                                    placeholder = {
                                                        Text(
                                                            stringResource(R.string.form_type_presentation_description),
                                                            color = Color(0xFF94A3B8),
                                                            fontSize = 14.sp
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = tipusExpandidoPorIndice[index] ?: false
                                                        )
                                                    },
                                                    singleLine = true,
                                                    shape = MaterialTheme.shapes.medium,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFFE28F41),
                                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                                        focusedTextColor = Color(0xFF1E293B),
                                                        unfocusedTextColor = Color(0xFF1E293B),
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White
                                                    )
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = tipusExpandidoPorIndice[index] ?: false,
                                                    onDismissRequest = {
                                                        viewModel.cerrarTipusPresentacioMenu(index)
                                                    },
                                                    modifier = Modifier.background(Color.White)
                                                ) {
                                                    elementosConCodigos.tiposPresentacion().forEach { (codigo, tipo) ->
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(
                                                                    tipo,
                                                                    fontSize = 14.sp,
                                                                    color = Color(0xFF1E293B),
                                                                    fontWeight = FontWeight.Normal
                                                                )
                                                            },
                                                            onClick = {
                                                                viewModel.seleccionarTipusPresentacio(index, codigo)
                                                            },
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
                            }

                            if (index < animales.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.confirmarMovimiento() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE28F41),
                        disabledContainerColor = Color(0xFFCBD5E1)
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