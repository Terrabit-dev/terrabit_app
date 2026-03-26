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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.moviminetos.modelos.Moviment
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.utils.components.CampoIdentificadorAutoComplete
import com.example.terrabit_app.utils.components.useDebounce
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.utils.usb.UsbSerialViewModel
import com.example.terrabit_app.viewmodel.bovinos.MovimientosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Movimientos(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel,
    borradorId: String = "",
    historialId: String = "",
    movimientoSeleccionado: Moviment
) {
    val viewModel = hiltViewModel<MovimientosViewModel>()
    // Cargar datos al entrar a la pantalla
    val elementosConCodigos = ElementosConCodigos()
    val transportMap = elementosConCodigos.transporte()
    LaunchedEffect(movimientoSeleccionado.codiRemo) {
        if (movimientoSeleccionado.codiRemo.isNotEmpty()) {
            val transportNombre = transportMap[movimientoSeleccionado.mitjaTransport] ?: ""
            viewModel.cargarDatosMovimiento(movimientoSeleccionado, transportNombre)
        }
    }

    val modoLectura = historialId.isNotEmpty()

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
    val codiAtesExpandido by viewModel.codiAtesExpandido.observeAsState(false)
    val mitjaTransportExpandido by viewModel.mitjaTransportExpandido.observeAsState(false)
    val mostrarDatePickerArribada by viewModel.mostrarDatePickerArribada.observeAsState(false)
    val mostrarTimePickerArribada by viewModel.mostrarTimePickerArribada.observeAsState(false)
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val estadoCarga by viewModel.cargandoMovimiento.observeAsState(false)
    val codiError by viewModel.codiError.observeAsState()
    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)
    val activeIndex by viewModel.activeFieldIndex.observeAsState(-1)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarDialogoAviso by remember { mutableStateOf(false) }
    var cantidadBorradores by remember { mutableStateOf(0) }
    var indiceBluetooth by remember { mutableStateOf<Int?>(null) }
    var mostrarBluetooth by remember { mutableStateOf(false) }

    val successMessage = stringResource(R.string.successful_message_confirm_movs)

    val usbViewModel = hiltViewModel<UsbSerialViewModel>()
    val usbState by usbViewModel.state.collectAsState()
    val usbErrorText = usbState.error?.let { stringResource(it) }
    var indiceUsb by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        usbViewModel.mensajes.collect { mensaje ->
            indiceUsb?.let { viewModel.actualizarIdentificadorAnimal(it, mensaje) }
            indiceUsb = null
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
                indiceBluetooth?.let { viewModel.actualizarIdentificadorAnimal(it, mensaje) }
                mostrarBluetooth = false
                indiceBluetooth = null
            },
            onDismiss = { mostrarBluetooth = false; indiceBluetooth = null }
        )
    }

    LaunchedEffect(Unit) {
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty() -> viewModel.cargarBorradorPorId(borradorId)
            else -> {
                cantidadBorradores = viewModel.obtenerCantidadBorradoresMovimiento()
                if (cantidadBorradores >= 2) mostrarDialogoAviso = true
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
            snackbarHostState.showSnackbar(successMessage, duration = SnackbarDuration.Short)
            viewModel.resetearEstadoRegistro()
        }
    }

    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) mostrarDialogoError = true
    }

    if (mostrarDialogoAviso) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Default.Description, contentDescription = null, tint = MainOrange, modifier = Modifier.size(48.dp)) },
            title = { Text("Borradores pendientes", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Tienes $cantidadBorradores borradores guardados de este formulario. Puedes verlos en la página de Borradores.\n\n¿Deseas crear uno nuevo?",
                    fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoAviso = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Crear nuevo", fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstadoRegistro() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MainOrange, modifier = Modifier.size(48.dp)) },
            title = { Text(stringResource(R.string.error_message_confirm_movs), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    if (codiError != null) alertsErrosScreens(codiError!!) else mensajeError,
                    fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstadoRegistro() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDatePickerArribada && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerArribada() },
            confirmButton = {
                TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaArribada(it) } }) {
                    Text(stringResource(R.string.accept_buttom), color = MainOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerArribada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange))
        }
    }

    if (mostrarTimePickerArribada && !modoLectura) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerArribada() },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarHoraArribada(timePickerState.hour.toString(), timePickerState.minute.toString())
                    viewModel.ocultarTimePickerArribada()
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarTimePickerArribada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialSelectedContentColor = Color.White, selectorColor = MainOrange))
            }
        )
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
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = MainOrange, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.loading_processing), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            Text(stringResource(R.string.name_confirm_movs), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            if (modoLectura) Text("Solo lectura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when {
                                historialId.isNotEmpty() -> navController.popBackStack()
                                borradorId.isNotEmpty() -> navController.popBackStack()
                                else -> navController.navigate(Routes.GuiasMovimientos.route)
                            }
                        }) {
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
                        Text(stringResource(R.string.form_movs_title_necessary), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                        SimpleTextField(
                            label = stringResource(R.string.form_codi_remo),
                            value = codiRemo,
                            placeholder = stringResource(R.string.form_codi_remo_description),
                            onValueChange = { viewModel.actualizarCodiRemo(it) },
                            enabled = !modoLectura,
                            imeAction = ImeAction.Next
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DateTimeFieldMovs(
                                modifier = Modifier.weight(1f),
                                label = stringResource(R.string.form_date_arrival),
                                value = dataArribada,
                                placeholder = stringResource(R.string.form_date_arrival_description),
                                icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                                enabled = !modoLectura,
                                onClick = { viewModel.mostrarDatePickerArribada() }
                            )
                            DateTimeFieldMovs(
                                modifier = Modifier.weight(1f),
                                label = stringResource(R.string.form_hour_arrival),
                                value = horaArribada,
                                placeholder = stringResource(R.string.form_hour_arrival_description),
                                icon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) },
                                enabled = !modoLectura,
                                onClick = { viewModel.mostrarTimePickerArribada() }
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.form_codi_ates), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = if (modoLectura) false else codiAtesExpandido,
                                onExpandedChange = { if (!modoLectura) viewModel.toggleCodiAtesExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = codiAtes,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    enabled = !modoLectura,
                                    readOnly = true,
                                    placeholder = { Text(stringResource(R.string.form_codi_ates_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = if (modoLectura) false else codiAtesExpandido) },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MainOrange,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    )
                                )
                                if (!modoLectura) {
                                    ExposedDropdownMenu(
                                        expanded = codiAtesExpandido,
                                        onDismissRequest = { viewModel.cerrarCodiAtesMenu() },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        viewModel.listaCodigosAtes.forEach { ates ->
                                            DropdownMenuItem(
                                                text = { Text("${ates.codigo} - ${ates.nombre}", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Normal) },
                                                onClick = { viewModel.seleccionarCodiAtes(ates.codigo) },
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        SimpleTextField(
                            label = stringResource(R.string.form_exploitation_destination),
                            value = explotacioDestinacio,
                            placeholder = stringResource(R.string.form_exploitation_destination_description),
                            onValueChange = { viewModel.actualizarExplotacioDestinacio(it) },
                            enabled = !modoLectura,
                            imeAction = ImeAction.Next
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        Text(stringResource(R.string.form_movs_title_animals), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                        DropdownField(
                            label = stringResource(R.string.form_ways_transports),
                            selectedValue = mitjaTransport,
                            expanded = if (modoLectura) false else mitjaTransportExpandido,
                            placeholder = stringResource(R.string.form_ways_transports_description),
                            opciones = elementosConCodigos.transporte(),
                            enabled = !modoLectura,
                            onExpandedChange = { if (!modoLectura) viewModel.toggleMitjaTransportExpandido() },
                            onDismissRequest = { viewModel.cerrarMitjaTransportMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarMitjaTransport(nombre, codigo) },
                            defectColor = false
                        )


                        SimpleTextField(label = stringResource(R.string.form_matricule_transport), value = matricula, placeholder = stringResource(R.string.form_matricule_transports_description), onValueChange = { viewModel.actualizarMatricula(it) }, enabled = !modoLectura, imeAction = ImeAction.Next)
                        SimpleTextField(label = stringResource(R.string.form_name_transportits), value = nomTransportista, placeholder = stringResource(R.string.form_name_transportits_description), onValueChange = { viewModel.actualizarNomTransportista(it) }, enabled = !modoLectura, imeAction = ImeAction.Next)
                        SimpleTextField(label = stringResource(R.string.form_nif_driver), value = nifConductor, placeholder = stringResource(R.string.form_nif_driver_description), onValueChange = { viewModel.actualizarNifConductor(it) }, enabled = !modoLectura, imeAction = ImeAction.Next)
                        SimpleTextField(label = stringResource(R.string.form_name_driver), value = nomConductor, placeholder = stringResource(R.string.form_name_driver_description), onValueChange = { viewModel.actualizarNomConductor(it) }, enabled = !modoLectura, imeAction = ImeAction.Done)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.form_movs_title_animals), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (!modoLectura) {
                                IconButton(
                                    onClick = { viewModel.agregarAnimal() },
                                    modifier = Modifier.size(40.dp).background(color = MainOrange, shape = RoundedCornerShape(8.dp))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Agregar animal", tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        val animales by viewModel.listaAnimales.observeAsState(emptyList())
                        val estatExpandidoPorIndice by viewModel.estatArribadaExpandidoPorIndice.observeAsState(emptyMap())
                        val tipusExpandidoPorIndice by viewModel.tipusPresentacioExpandidoPorIndice.observeAsState(emptyMap())
                        val datePickerPorIndice by viewModel.mostrarDatePickerPorIndice.observeAsState(emptyMap())

                        animales.forEachIndexed { index, animal ->
                            if (datePickerPorIndice[index] == true && !modoLectura) {
                                val datePickerState = rememberDatePickerState()
                                DatePickerDialog(
                                    onDismissRequest = { viewModel.ocultarDatePickerSacrMort(index) },
                                    confirmButton = {
                                        TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaSacrMort(index, it) } }) {
                                            Text(stringResource(R.string.accept_buttom), color = MainOrange)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { viewModel.ocultarDatePickerSacrMort(index) }) {
                                            Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                ) {
                                    DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange))
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Animal ${index + 1}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MainOrange)
                                        if (!modoLectura && animales.size > 1) {
                                            IconButton(onClick = { viewModel.eliminarAnimal(index) }, modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar animal", tint = ErrorRed, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }

                                    if (!modoLectura) {
                                        useDebounce(animal.identificador, delayMillis = 300L) { viewModel.searchBovinos(index, it) }
                                    }
                                    CampoIdentificadorAutoComplete(
                                        label = stringResource(R.string.form_id_animal),
                                        valor = animal.identificador,
                                        placeholder = stringResource(R.string.form_animal_id_example),
                                        enabled = !modoLectura,
                                        onValueChange = { viewModel.actualizarIdentificadorAnimal(index, it) },
                                        suggestions = if (activeIndex == index) suggestionsBovinos else emptyList(),
                                        onAnimalSelected = { viewModel.onBovinoSelected(index, it) },
                                        isLoadingSuggestions = isLoadingBovinos,
                                        defectColor = false,
                                        onClickBluetooth = {
                                            indiceBluetooth = index
                                            bluetoothViewModel.iniciarEscaneo(context)
                                            mostrarBluetooth = true
                                        },
                                        onClickUsb = {
                                            indiceUsb = index
                                            usbViewModel.conectar()
                                        }
                                    )

                                    DropdownField(
                                        label = stringResource(R.string.form_state_arrival),
                                        selectedValue = elementosConCodigos.estadosLlegada()[animal.estatArribada] ?: "",
                                        expanded = estatExpandidoPorIndice[index] ?: false,
                                        placeholder = stringResource(R.string.form_state_arrival_description),
                                        opciones = elementosConCodigos.estadosLlegada(),
                                        enabled = !modoLectura,
                                        onExpandedChange = { viewModel.toggleEstatArribadaExpandido(index) },
                                        onDismissRequest = { viewModel.cerrarEstatArribadaMenu(index) },
                                        onSeleccionar = { codigo, nombre -> viewModel.seleccionarEstatArribadaAnimal(index, nombre, codigo) },
                                        defectColor = true
                                    )

                                    if (animal.estatArribada == "80") {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                                        Text(stringResource(R.string.form_movs_title_sacrifate_dade), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MainOrange)

                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(stringResource(R.string.form_date_sacrifice), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Box(
                                                modifier = if (!modoLectura)
                                                    Modifier.fillMaxWidth().clickable { viewModel.mostrarDatePickerSacrMort(index) }
                                                else
                                                    Modifier.fillMaxWidth()
                                            ) {
                                                OutlinedTextField(
                                                    value = animal.dataSacrMort ?: "",
                                                    onValueChange = {},
                                                    modifier = Modifier.fillMaxWidth(),
                                                    placeholder = { Text(stringResource(R.string.form_date_description), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                                                    readOnly = true,
                                                    enabled = false,
                                                    shape = MaterialTheme.shapes.medium,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                                        disabledLeadingIconColor = MainOrange,
                                                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        disabledContainerColor = MaterialTheme.colorScheme.surface
                                                    ),
                                                    singleLine = true
                                                )
                                            }
                                        }

                                        CampoTexto(label = stringResource(R.string.form_weight_canal), valor = animal.pesCanal ?: "0", placeholder = stringResource(R.string.form_weight_canal_description), onValueChange = { viewModel.actualizarPesCanal(index, it) }, defectColor = false, keyboardType = KeyboardType.Decimal, enabled = !modoLectura)
                                        CampoTexto(label = stringResource(R.string.form_class_canal), valor = animal.classCanal ?: "", placeholder = stringResource(R.string.form_class_canal_description), onValueChange = { viewModel.actualizarClassCanal(index, it) }, defectColor = false, enabled = !modoLectura)

                                        DropdownField(
                                            label = stringResource(R.string.form_type_presentation),
                                            selectedValue = elementosConCodigos.tiposPresentacion()[animal.tipusPresentacio] ?: "",
                                            expanded = tipusExpandidoPorIndice[index] ?: false,
                                            placeholder = stringResource(R.string.form_type_presentation_description),
                                            opciones = elementosConCodigos.tiposPresentacion(),
                                            enabled = !modoLectura,
                                            onExpandedChange = { viewModel.toggleTipusPresentacioExpandido(index) },
                                            onDismissRequest = { viewModel.cerrarTipusPresentacioMenu(index) },
                                            onSeleccionar = { codigo, _ -> viewModel.seleccionarTipusPresentacio(index, codigo) },
                                            defectColor = false
                                        )
                                    }
                                }
                            }

                            if (index < animales.size - 1) Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.confirmarMovimiento() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                        enabled = !estadoCarga,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainOrange,
                            disabledContainerColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.buttom_form_confirm_movs), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DateTimeFieldMovs(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String,
    icon: @Composable () -> Unit,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = if (enabled)
                Modifier.fillMaxWidth().clickable { onClick() }
            else
                Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = icon,
                readOnly = true,
                enabled = false,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MainOrange,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun SimpleTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainOrange,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MainOrange,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction)
        )
    }
}