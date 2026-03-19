package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.utils.components.CampoIdentificadorAutoComplete
import com.example.terrabit_app.utils.components.useDebounce
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.utils.usb.UsbSerialViewModel
import com.example.terrabit_app.viewmodel.bovinos.NacimientoViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nacimiento(
    navController: NavController,
    bluetooth: BluetoothViewModel,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<NacimientoViewmodel>()
    val modoLectura = historialId.isNotEmpty()

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
    val fechaIdentificacion by viewModel.fechaIdentificacion.observeAsState("")
    val mostrarDatePickerIdentificadores by viewModel.mostrarDatePickerIdentificacion.observeAsState(false)
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val codiError by viewModel.codiError.observeAsState()
    val estadoCarga by viewModel.cargandoNacimiento.observeAsState(false)
    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)
    val activeFieldIndex by viewModel.activeFieldIndex.observeAsState(-1)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarBluetooth by remember { mutableStateOf(false) }
    var madreBluetooth by remember { mutableStateOf(false) }
    var criaBluetooth by remember { mutableStateOf(false) }

    val mensajeRegistroExitoso = stringResource(R.string.successful_message_born)
    val mensajeRegistroError = stringResource(R.string.error_message_born)
    val elementosConCodigos = ElementosConCodigos()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val usbViewModel = hiltViewModel<UsbSerialViewModel>()
    val usbState by usbViewModel.state.collectAsState()
    val usbErrorText = usbState.error?.let { stringResource(it) }

    var madreUsb by remember { mutableStateOf(false) }
    var criaUsb by remember { mutableStateOf(false) }

    val razas = elementosConCodigos.razasBovinas()

    LaunchedEffect(Unit) {
        usbViewModel.mensajes.collect { mensaje ->
            when {
                madreUsb -> {
                    viewModel.actualizarIdMadre(mensaje)
                    madreUsb = false
                }
                criaUsb -> {
                    viewModel.actualizarIdCria(mensaje)
                    criaUsb = false
                }
            }
        }
    }

    LaunchedEffect(usbErrorText) {
        usbErrorText?.let {
            snackbarHostState.showSnackbar(
                message = "USB: $it",
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(Unit) {
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty() -> viewModel.cargarBorradorPorId(borradorId)
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

    if (mostrarBluetooth) {
        BluetoothScanDialog(
            bluetoothViewModel = bluetooth,
            onMensajeRecibido = { mensaje ->
                if (criaBluetooth) viewModel.actualizarIdCria(mensaje)
                else viewModel.actualizarIdMadre(mensaje)
                mostrarBluetooth = false
                madreBluetooth = false
                criaBluetooth = false
            },
            onDismiss = { mostrarBluetooth = false }
        )
    }

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(mensajeRegistroExitoso, duration = SnackbarDuration.Short)
            viewModel.resetearEstadoRegistro()
        }
    }

    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) mostrarDialogoError = true
    }

    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstadoRegistro() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MainGreen, modifier = Modifier.size(48.dp)) },
            title = { Text(mensajeRegistroError, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    if (codiError != null) alertsErrosScreens(codiError!!) else mensajeError,
                    fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstadoRegistro() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDatePicker && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePicker() },
            confirmButton = {
                TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFecha(it) } }) {
                    Text(stringResource(R.string.accept_buttom), color = MainGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(selectedDayContainerColor = MainGreen, todayDateBorderColor = MainGreen)
            )
        }
    }

    if (mostrarDatePickerIdentificadores && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerIdentificacion() },
            confirmButton = {
                TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaIdentificacion(it) } }) {
                    Text(stringResource(R.string.accept_buttom), color = MainGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerIdentificacion() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(selectedDayContainerColor = MainGreen, todayDateBorderColor = MainGreen)
            )
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
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = MainGreen, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Procesando...", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            Text(stringResource(R.string.born_register_name), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            if (modoLectura) Text("Solo lectura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when {
                                historialId.isNotEmpty() -> navController.popBackStack()
                                borradorId.isNotEmpty() -> navController.popBackStack()
                                else -> navController.navigate(Routes.GestionBovinos.route)
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MainGreen,
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

                        if (!modoLectura) {
                            useDebounce(idMadre, delayMillis = 300L) { viewModel.searchBovinos(0, it) }
                        }
                        CampoIdentificadorAutoComplete(
                            label = stringResource(R.string.form_id_mother),
                            valor = idMadre,
                            placeholder = stringResource(R.string.form_mother_description),
                            enabled = !modoLectura,
                            onValueChange = { if (!modoLectura) viewModel.actualizarIdMadre(it) },
                            suggestions = if (modoLectura) emptyList() else if (activeFieldIndex == 0) suggestionsBovinos else emptyList(),
                            onAnimalSelected = { if (!modoLectura) viewModel.onMotherselected(it) },
                            isLoadingSuggestions = if (modoLectura) false else (isLoadingBovinos && activeFieldIndex == 0),
                            onClickBluetooth = {
                                if (!modoLectura) {
                                    madreBluetooth = true; criaBluetooth = false
                                    bluetooth.iniciarEscaneo(context); mostrarBluetooth = true
                                }
                            },
                            onClickUsb = {
                                if (!modoLectura) {
                                    madreUsb = true; criaUsb = false
                                    usbViewModel.conectar()
                                }
                            }
                        )

                        if (!modoLectura) {
                            useDebounce(idCria, delayMillis = 300L) { viewModel.searchBovinos(1, it) }
                        }
                        CampoIdentificadorAutoComplete(
                            label = stringResource(R.string.form_id_breeding),
                            valor = idCria,
                            placeholder = stringResource(R.string.form_id_breeding_description),
                            enabled = !modoLectura,
                            onValueChange = { if (!modoLectura) viewModel.actualizarIdCria(it) },
                            suggestions = if (modoLectura) emptyList() else if (activeFieldIndex == 1) suggestionsBovinos else emptyList(),
                            onAnimalSelected = { if (!modoLectura) viewModel.onBreedingSelected(it) },
                            isLoadingSuggestions = if (modoLectura) false else (isLoadingBovinos && activeFieldIndex == 1),
                            onClickBluetooth = {
                                if (!modoLectura) {
                                    madreBluetooth = false; criaBluetooth = true
                                    bluetooth.iniciarEscaneo(context); mostrarBluetooth = true
                                }
                            },
                            onClickUsb = {
                                if (!modoLectura) {
                                    madreUsb = false; criaUsb = true
                                    usbViewModel.conectar()
                                }
                            }
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.form_birthdate), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = if (!modoLectura)
                                    Modifier.fillMaxWidth().clickable { viewModel.mostrarDatePicker() }
                                else
                                    Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = fechaNacimiento, onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_date_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainGreen) },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLeadingIconColor = MainGreen,
                                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.form_date_identification), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = if (!modoLectura)
                                    Modifier.fillMaxWidth().clickable { viewModel.mostrarDatePickerIdentificacion() }
                                else
                                    Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = fechaIdentificacion, onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_date_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainGreen) },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLeadingIconColor = MainGreen,
                                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        DropdownField(
                            label = stringResource(R.string.form_sex),
                            selectedValue = sexoSeleccionado,
                            expanded = if (modoLectura) false else sexoExpandido,
                            placeholder = stringResource(R.string.form_sex_description),
                            opciones = elementosConCodigos.sexos(),
                            enabled = !modoLectura,
                            onExpandedChange = { if (!modoLectura) viewModel.toggleSexoExpandido() },
                            onDismissRequest = { viewModel.cerrarSexoMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarSexo(nombre, codigo) },
                            defectColor = true
                        )

                        DropdownField(
                            label = stringResource(R.string.form_raze),
                            selectedValue = razaSeleccionada,
                            expanded = if (modoLectura) false else razaExpandida,
                            placeholder = stringResource(R.string.form_raze_description),
                            opciones = razas,
                            enabled = !modoLectura,
                            onExpandedChange = { if (!modoLectura) viewModel.toggleRazaExpandida() },
                            onDismissRequest = { viewModel.cerrarRazaMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarRaza(nombre, codigo) },
                            defectColor = true
                        )

                        DropdownField(
                            label = stringResource(R.string.form_aptitude),
                            selectedValue = aptitudSeleccionada,
                            expanded = if (modoLectura) false else aptitudExpandida,
                            placeholder = stringResource(R.string.form_aptitude_description),
                            opciones = elementosConCodigos.aptitudes(),
                            enabled = !modoLectura,
                            onExpandedChange = { if (!modoLectura) viewModel.toggleAptitudExpandida() },
                            onDismissRequest = { viewModel.cerrarAptitudMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarAptitud(nombre, codigo) },
                            defectColor = true
                        )
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.registrarNacimiento() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                        enabled = !estadoCarga,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainGreen,
                            disabledContainerColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.buttom_form_born), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}