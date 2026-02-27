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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.bovinos.components.AutoCompleteBovinoField
import com.example.terrabit_app.ui.screen.bovinos.components.CampoIdentificadorAutoComplete
import com.example.terrabit_app.ui.screen.bovinos.components.useDebounce
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkOrange
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.ui.theme.Yellow
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.viewmodel.ViewModelMuerteBovi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fallecimiento(navController: NavController, bluetoothViewModel: BluetoothViewModel, borradorId: String = "") {
    val viewModel = viewModel<ViewModelMuerteBovi>()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val tipoSeleccionado by viewModel.tipoMuerte.observeAsState("")
    val identificadorAnimal by viewModel.identificadorMuerte.observeAsState("")
    val fechaMuerte by viewModel.fechaMuerte.observeAsState("")
    val mesesGestacion by viewModel.mesesGestacion.observeAsState("")
    val cadaverInaccesible by viewModel.cadaverInaccesible.observeAsState(false)
    val coordenadaX by viewModel.coordenadaX.observeAsState("")
    val coordenadaY by viewModel.coordenadaY.observeAsState("")
    val tipoExpandido by viewModel.tipoMuerteExpandido.observeAsState(false)
    val mostrarDatePickerMuerte by viewModel.mostrarDatePickerMuerte.observeAsState(false)
    val tipoMuerte by viewModel.codigoTipoMuerte.observeAsState("")

    val registroExitoso by viewModel.registroMuerteExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorMuerte.observeAsState("")
    val codiError by viewModel.codiError.observeAsState()
    val estadoCarga by viewModel.cargandoMuerte.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    val mensajeRegistroExitoso = stringResource(R.string.successful_message_dead)
    val mensajeRegistroError = stringResource(R.string.error_message_dead)

    val elementosConCodigos = ElementosConCodigos()
    // control de bluethooth
    var mostrarBluetooth by remember { mutableStateOf(false) }

    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)

    if (mostrarBluetooth) {
        BluetoothScanDialog(
            bluetoothViewModel = bluetoothViewModel,
            onMensajeRecibido = { mensaje ->
                viewModel.actualizarIdentificadorMuerte(mensaje)
                mostrarBluetooth = false

            },
            onDismiss = {
                mostrarBluetooth = false
            }
        )
    }

    // ============================================
    // INICIALIZACIÓN Y DETECCIÓN DE BORRADORES
    // ============================================
    LaunchedEffect(Unit) {
        viewModel.inicializarSharedPreferences(context)

        if (borradorId.isNotEmpty()) {
            viewModel.cargarBorradorPorId(borradorId)
            return@LaunchedEffect
        }
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
                message = mensajeRegistroExitoso,
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistroMuerte()
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
                viewModel.resetearEstadoRegistroMuerte()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = mensajeRegistroError,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkBlueGrey
                )
            },
            text = {
                Text(
                    text = mensajeError.ifEmpty { alertsErrosScreens(codiError!!) },
                    fontSize = 16.sp,
                    color = BlueGrey,
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoError = false
                        viewModel.resetearEstadoRegistroMuerte()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
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

    if (mostrarDatePickerMuerte) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerMuerte() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaMuerte(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerMuerte() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = ErrorRed,
                    todayDateBorderColor = ErrorRed
                )
            )
        }
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
                            color = ErrorRed,
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
    }
    else{
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                stringResource(R.string.name_report_dead),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Routes.GestionBovinos.route) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ErrorRed,
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
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_type_dead),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = tipoExpandido,
                                onExpandedChange = { viewModel.toggleTipoMuerteExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = tipoSeleccionado,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_type_dead_description),
                                            color = BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = tipoExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ErrorRed,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = tipoExpandido,
                                    onDismissRequest = { viewModel.cerrarTipoMuerteMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    elementosConCodigos.muertes().forEach { (tipo, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    tipo,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarTipoMuerte(tipo,codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        useDebounce(identificadorAnimal, delayMillis = 300L) { query ->
                            viewModel.searchBovinos(query)
                        }
                        CampoIdentificadorAutoComplete(
                            label = if (tipoMuerte.contains("01")) stringResource(R.string.form_id_animal) else stringResource(R.string.form_id_mother),
                            valor = identificadorAnimal,
                            placeholder = if (tipoMuerte.contains("01")) stringResource(R.string.form_id_animal_description) else stringResource(R.string.form_mother_description),
                            onValueChange = { viewModel.actualizarIdentificadorMuerte(it) },
                            suggestions = suggestionsBovinos,
                            onAnimalSelected = { viewModel.onBovinoSelected(it) },
                            isLoadingSuggestions = isLoadingBovinos,
                            onClickBluetooth = {
                                bluetoothViewModel.iniciarEscaneo(context)
                                mostrarBluetooth = true
                            }
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_dead_date),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.mostrarDatePickerMuerte() }
                            ) {
                                OutlinedTextField(
                                    value = fechaMuerte,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_date_description),
                                            color = BlueGrey
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "Calendario",
                                            tint = ErrorRed
                                        )
                                    },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = DarkBlueGrey,
                                        disabledBorderColor = DarkWhiteBackground,
                                        disabledLeadingIconColor = ErrorRed,
                                        disabledPlaceholderColor = BlueGrey
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        if (tipoMuerte.contains("02")) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    stringResource(R.string.form_pregnancy_months),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlueGrey,
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = mesesGestacion,
                                    onValueChange = { viewModel.actualizarMesesGestacion(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_pregnancy_months_description),
                                            color = BlueGrey
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ErrorRed,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey,
                                        cursorColor = ErrorRed
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.title_cadaver),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlueGrey
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    stringResource(R.string.description_cadaver),
                                    fontSize = 13.sp,
                                    color = BlueGrey,
                                    lineHeight = 18.sp
                                )
                            }
                            Switch(
                                checked = cadaverInaccesible,
                                onCheckedChange = { viewModel.toggleCadaverInaccesible() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ErrorRed,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = DarkWhiteBackground
                                )
                            )
                        }

                        if (cadaverInaccesible) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = WhiteBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MainOrange,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            stringResource(R.string.title_gps),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ErrorRed
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { viewModel.obtenerUbicacionActual() },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 2.dp
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MainOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            stringResource(R.string.buttom_gps),
                                            color = ErrorRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                stringResource(R.string.gps_laltitud),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ErrorRed
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = coordenadaX,
                                                onValueChange = { viewModel.actualizarCoordenadaX(it) },
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.gps_laltitud_description),
                                                        fontSize = 13.sp,
                                                        color = DarkOrange
                                                    )
                                                },
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = MainOrange,
                                                    unfocusedBorderColor = Yellow,
                                                    focusedTextColor = ErrorRed,
                                                    unfocusedTextColor = ErrorRed,
                                                    cursorColor = MainOrange,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Decimal,
                                                    imeAction = ImeAction.Next
                                                )
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                stringResource(R.string.gps_longitud),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ErrorRed
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = coordenadaY,
                                                onValueChange = { viewModel.actualizarCoordenadaY(it) },
                                                placeholder = {
                                                    Text(
                                                        stringResource(R.string.gps_longitud_description),
                                                        fontSize = 13.sp,
                                                        color = DarkOrange
                                                    )
                                                },
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = MainOrange,
                                                    unfocusedBorderColor = Yellow,
                                                    focusedTextColor = ErrorRed,
                                                    unfocusedTextColor = ErrorRed,
                                                    cursorColor = MainOrange,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Decimal,
                                                    imeAction = ImeAction.Done
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.putMuerteBovino() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed,
                        disabledContainerColor = DarkWhiteBackground
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text(
                        stringResource(R.string.buttom_form_dead),
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