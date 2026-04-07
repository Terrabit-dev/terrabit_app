package com.example.terrabit_app.ui.screen.bovinos

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.utils.components.CampoIdentificadorAutoComplete
import com.example.terrabit_app.utils.components.useDebounce
import com.example.terrabit_app.ui.theme.DarkOrange
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.Yellow
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.utils.usb.UsbSerialViewModel
import com.example.terrabit_app.viewmodel.bovinos.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.bovinos.ViewModelMuerteBovi
import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.terrabit_app.utils.AnimalSeleccionadoHolder
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.LocationUtils
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fallecimiento(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<ViewModelMuerteBovi>()
    val modoLectura = historialId.isNotEmpty()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val tipoSeleccionado by viewModel.tipoMuerte.observeAsState(null)
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
    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarBluetooth by remember { mutableStateOf(false) }

    val mensajeRegistroExitoso = stringResource(R.string.successful_message_dead)
    val mensajeRegistroError = stringResource(R.string.error_message_dead)
    val elementosConCodigos = remember { ElementosConCodigos() }
    val datePickerState = rememberDatePickerState()

    val usbViewModel = hiltViewModel<UsbSerialViewModel>()
    val usbState by usbViewModel.state.collectAsState()
    val usbErrorText = usbState.error?.let { stringResource(it) }

    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)
    val codiMoActivo by codiMoViewModel.codiMoActivo.observeAsState(null)

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var procedeDeLista  by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            obtenerUbicacion(fusedLocationClient, viewModel)
        }
    }

    LaunchedEffect(Unit) {
        usbViewModel.mensajes.collect { mensaje ->
            viewModel.actualizarIdentificadorMuerte(mensaje)
        }
    }

    LaunchedEffect(usbErrorText) {
        usbErrorText?.let {
            snackbarHostState.showSnackbar(message = "USB: $it", duration = SnackbarDuration.Short)
        }
    }

    if (mostrarBluetooth) {
        BluetoothScanDialog(
            bluetoothViewModel = bluetoothViewModel,
            onMensajeRecibido = { mensaje ->
                viewModel.actualizarIdentificadorMuerte(mensaje)
                mostrarBluetooth = false
            },
            onDismiss = { mostrarBluetooth = false }
        )
    }

    LaunchedEffect(Unit) {
        val animalId = AnimalSeleccionadoHolder.consume()
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty() -> viewModel.cargarBorradorPorId(borradorId)
            animalId.isNotEmpty() -> {
                viewModel.precargarAnimal(animalId)
                procedeDeLista = true
            }

        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && !modoLectura && viewModel.tieneContenido()) {
                viewModel.guardarBorradorAutomatico()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(mensajeRegistroExitoso, duration = SnackbarDuration.Short)
            viewModel.resetearEstadoRegistroMuerte()
        }
    }

    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) mostrarDialogoError = true
    }

    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstadoRegistroMuerte() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(48.dp)) },
            title = { Text(mensajeRegistroError, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    mensajeError.ifEmpty { alertsErrosScreens(codiError!!) },
                    fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstadoRegistroMuerte() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDatePickerMuerte && !modoLectura) {
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerMuerte() },
            confirmButton = {
                TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaMuerte(it) } }) {
                    Text(stringResource(R.string.accept_buttom), color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerMuerte() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(selectedDayContainerColor = ErrorRed, todayDateBorderColor = ErrorRed)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(stringResource(R.string.name_report_dead), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            if (modoLectura) Text("Solo lectura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when {
                                historialId.isNotEmpty() -> navController.popBackStack()
                                borradorId.isNotEmpty() -> navController.popBackStack()
                                procedeDeLista -> navController.navigate(Routes.ListarBovinos.route)
                                else -> navController.navigate(Routes.GestionBovinos.route)
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
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
                    Snackbar(snackbarData = data, containerColor = MainGreen, contentColor = Color.White, shape = RoundedCornerShape(12.dp))
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CodiMoSelector(
                                codisMos = codiMoViewModel.getCodisMos(),
                                seleccionado = codiMoActivo,
                                expanded = codisMoExpandido,
                                onToggle = { codiMoViewModel.toggleCodisMoExpandido() },
                                onDismiss = { codiMoViewModel.cerrarCodisMo() },
                                onSeleccionar = { codi -> codiMoViewModel.seleccionarCodiMo(codi) },
                                accentColor = ErrorRed
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            DropdownField(
                                label = stringResource(R.string.form_type_dead),
                                selectedValue = tipoSeleccionado,
                                expanded = if (modoLectura) false else tipoExpandido,
                                placeholder = stringResource(R.string.form_type_dead_description),
                                opciones = elementosConCodigos.getMuertes(),
                                enabled = !modoLectura,
                                onExpandedChange = { if (!modoLectura) viewModel.toggleTipoMuerteExpandido() },
                                onDismissRequest = { viewModel.cerrarTipoMuerteMenu() },
                                onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarTipoMuerte(nombre, codigo) },
                                accentColor = ErrorRed
                            )
                        }

                        if (!modoLectura) {
                            useDebounce(identificadorAnimal, delayMillis = 300L) { viewModel.searchBovinos(it) }
                        }
                        CampoIdentificadorAutoComplete(
                            label = if (tipoMuerte.contains("01")) stringResource(R.string.form_id_animal) else stringResource(R.string.form_id_mother),
                            valor = identificadorAnimal,
                            placeholder = if (tipoMuerte.contains("01")) stringResource(R.string.form_id_animal_description) else stringResource(R.string.form_mother_description),
                            enabled = !modoLectura,
                            onValueChange = { viewModel.actualizarIdentificadorMuerte(it) },
                            suggestions = suggestionsBovinos,
                            onAnimalSelected = { viewModel.onBovinoSelected(it) },
                            isLoadingSuggestions = isLoadingBovinos,
                            onClickBluetooth = { bluetoothViewModel.iniciarEscaneo(context); mostrarBluetooth = true },
                            onClickUsb = { usbViewModel.conectar() }
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_dead_date),
                                fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = if (!modoLectura)
                                    Modifier.fillMaxWidth().clickable { viewModel.mostrarDatePickerMuerte() }
                                else
                                    Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = fechaMuerte,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_date_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = ErrorRed) },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLeadingIconColor = ErrorRed,
                                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        if (tipoMuerte.contains("02")) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    stringResource(R.string.form_pregnancy_months),
                                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = mesesGestacion,
                                    onValueChange = { viewModel.actualizarMesesGestacion(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !modoLectura,
                                    placeholder = { Text(stringResource(R.string.form_pregnancy_months_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ErrorRed,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        cursorColor = ErrorRed,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.title_cadaver), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(stringResource(R.string.description_cadaver), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                            }

                            Switch(
                                checked = cadaverInaccesible,
                                onCheckedChange = if (modoLectura) null else { { viewModel.toggleCadaverInaccesible() } },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ErrorRed,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }

                        if (cadaverInaccesible) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MainOrange, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.title_gps), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (!modoLectura) {
                                        Button(
                                            onClick = {
                                                val permisos = arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                                locationPermissionLauncher.launch(permisos)
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                            shape = RoundedCornerShape(10.dp),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                        ) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MainOrange, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(R.string.buttom_gps), color = ErrorRed, fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(stringResource(R.string.gps_laltitud), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ErrorRed)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = coordenadaX,
                                                onValueChange = { viewModel.actualizarCoordenadaX(it) },
                                                enabled = !modoLectura,
                                                placeholder = { Text(stringResource(R.string.gps_laltitud_description), fontSize = 13.sp, color = DarkOrange) },
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = MainOrange,
                                                    unfocusedBorderColor = Yellow,
                                                    focusedTextColor = ErrorRed,
                                                    unfocusedTextColor = ErrorRed,
                                                    cursorColor = MainOrange,
                                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                                    disabledBorderColor = Yellow,
                                                    disabledTextColor = ErrorRed,
                                                    disabledLabelColor = DarkOrange,
                                                    disabledPlaceholderColor = DarkOrange,
                                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(stringResource(R.string.gps_longitud), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ErrorRed)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = coordenadaY,
                                                onValueChange = { viewModel.actualizarCoordenadaY(it) },
                                                enabled = !modoLectura,
                                                placeholder = { Text(stringResource(R.string.gps_longitud_description), fontSize = 13.sp, color = DarkOrange) },
                                                singleLine = true,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = MainOrange,
                                                    unfocusedBorderColor = Yellow,
                                                    focusedTextColor = ErrorRed,
                                                    unfocusedTextColor = ErrorRed,
                                                    cursorColor = MainOrange,
                                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                                    disabledBorderColor = Yellow,
                                                    disabledTextColor = ErrorRed,
                                                    disabledLabelColor = DarkOrange,
                                                    disabledPlaceholderColor = DarkOrange,
                                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                                ),
                                                modifier = Modifier.fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.putMuerteBovino() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                        enabled = !estadoCarga,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            disabledContainerColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.buttom_form_dead), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (estadoCarga) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.size(120.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = ErrorRed, strokeWidth = 4.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(stringResource(R.string.loading_processing), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("MissingPermission")
private fun obtenerUbicacion(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    viewModel: ViewModelMuerteBovi
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val (x, y) = LocationUtils.latLonToUTM(location.latitude, location.longitude)
            viewModel.actualizarUbicacion(x, y)
        }
    }
}