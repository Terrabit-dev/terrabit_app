package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.utils.usb.UsbSerialViewModel
import com.example.terrabit_app.viewmodel.bovinos.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.bovinos.GuiasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionGuias(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<GuiasViewModel>()
    val modoLectura = historialId.isNotEmpty()

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

    val successMessage = stringResource(R.string.success_create_guide)
    val datePlaceholder = stringResource(R.string.form_date_description)
    val hourPlaceholder = stringResource(R.string.form_hour_arrival_description)
    val elementosConCodigos = ElementosConCodigos()

    val usbViewModel = hiltViewModel<UsbSerialViewModel>()
    val usbState by usbViewModel.state.collectAsState()
    val usbErrorText = usbState.error?.let { stringResource(it) }
    var indiceUsb by remember { mutableStateOf<Int?>(null) }

    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)
    val codiMoActivo by codiMoViewModel.codiMoActivo.observeAsState(null)
    LaunchedEffect(Unit) {
        usbViewModel.mensajes.collect { mensaje ->
            indiceUsb?.let { viewModel.actualizarIdentificador(it, mensaje) }
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
                indiceBluetooth?.let { viewModel.actualizarIdentificador(it, mensaje) }
                mostrarBluetooth = false; indiceBluetooth = null
            },
            onDismiss = { mostrarBluetooth = false; indiceBluetooth = null }
        )
    }

    LaunchedEffect(Unit) {
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty() -> viewModel.cargarBorradorPorId(borradorId)
            else -> {
                cantidadBorradores = viewModel.obtenerCantidadBorradoresGuia()
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
            title = { Text(stringResource(R.string.error_create_guide), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
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

    if (mostrarDatePickerSortida && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerSortida() },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaSortida(it) } }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarDatePickerSortida() }) { Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        ) { DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange)) }
    }

    if (mostrarTimePickerSortida && !modoLectura) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerSortida() },
            confirmButton = { TextButton(onClick = { viewModel.actualizarHoraSortida(timePickerState.hour.toString(), timePickerState.minute.toString()); viewModel.ocultarTimePickerSortida() }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarTimePickerSortida() }) { Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant) } },
            containerColor = MaterialTheme.colorScheme.surface,
            text = { TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialSelectedContentColor = Color.White, selectorColor = MainOrange)) }
        )
    }

    if (mostrarDatePickerArribada && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerArribada() },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaArribada(it) } }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarDatePickerArribada() }) { Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        ) { DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange)) }
    }

    if (mostrarTimePickerArribada && !modoLectura) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerArribada() },
            confirmButton = { TextButton(onClick = { viewModel.actualizarHoraArribada(timePickerState.hour.toString(), timePickerState.minute.toString()); viewModel.ocultarTimePickerArribada() }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarTimePickerArribada() }) { Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant) } },
            containerColor = MaterialTheme.colorScheme.surface,
            text = { TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialSelectedContentColor = Color.White, selectorColor = MainOrange)) }
        )
    }

    if (estadoCarga) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(enabled = false) {},
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
                            Text(stringResource(R.string.name_manage_guides), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            if (modoLectura) Text("Solo lectura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MainOrange, titleContentColor = Color.White, navigationIconContentColor = Color.White)
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
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CodiMoSelector(
                                codisMos = codiMoViewModel.getCodisMos(),
                                seleccionado = codiMoActivo, // cuando tenga estado: codiMoViewModel.seleccionado
                                expanded = codisMoExpandido,
                                onToggle = { codiMoViewModel.toggleCodisMoExpandido() },
                                onDismiss = { codiMoViewModel.cerrarCodisMo() },
                                onSeleccionar = {  codi -> codiMoViewModel.seleccionarCodiMo(codi) },
                                accentColor = MainOrange
                            )
                        }
                        Text(stringResource(R.string.form_movs_title_necessary), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                        CampoTexto(label = stringResource(R.string.form_origin_exploitation), valor = explotacioOrigen, placeholder = stringResource(R.string.form_format_mo_rega), onValueChange = { if (!modoLectura) viewModel.actualizarExplotacioOrigen(it) }, defectColor = false, enabled = !modoLectura)
                        CampoTexto(label = stringResource(R.string.form_exploitation_destination), valor = explotacioDestinacio, placeholder = stringResource(R.string.form_format_mo_rega), onValueChange = { if (!modoLectura) viewModel.actualizarExplotacioDestinacio(it) }, defectColor = false, enabled = !modoLectura)

                        DropdownField(
                            label = stringResource(R.string.form_temporal), selectedValue = temporal,
                            expanded = if (modoLectura) false else temporalExpandido,
                            placeholder = stringResource(R.string.form_yes_no), opciones = elementosConCodigos.opcionesSiNo(),
                            onExpandedChange = { if (!modoLectura) viewModel.toggleTemporalExpandido() },
                            onDismissRequest = { viewModel.cerrarTemporalMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarTemporal(nombre, codigo) },
                            defectColor = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DateTimeField(modifier = Modifier.weight(1f), label = stringResource(R.string.form_date_departure), value = dataSortida, placeholder = datePlaceholder, icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) }, onClick = { if (!modoLectura) viewModel.mostrarDatePickerSortida() })
                            DateTimeField(modifier = Modifier.weight(1f), label = stringResource(R.string.form_hour_arrival), value = horaSortida, placeholder = hourPlaceholder, icon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) }, onClick = { if (!modoLectura) viewModel.mostrarTimePickerSortida() })
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DateTimeField(modifier = Modifier.weight(1f), label = stringResource(R.string.form_date_arrival), value = dataArribada, placeholder = datePlaceholder, icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) }, onClick = { if (!modoLectura) viewModel.mostrarDatePickerArribada() })
                            DateTimeField(modifier = Modifier.weight(1f), label = stringResource(R.string.form_hour_arrival), value = horaArribada, placeholder = hourPlaceholder, icon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) }, onClick = { if (!modoLectura) viewModel.mostrarTimePickerArribada() })
                        }

                        DropdownField(
                            label = stringResource(R.string.form_mobility_guide), selectedValue = mobilitat,
                            expanded = if (modoLectura) false else mobilitatExpandido,
                            placeholder = stringResource(R.string.form_yes_no), opciones = elementosConCodigos.opcionesSiNo(),
                            onExpandedChange = { if (!modoLectura) viewModel.toggleMobilitatExpandido() },
                            onDismissRequest = { viewModel.cerrarMobilitatMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarMobilitat(nombre, codigo) },
                            defectColor = true
                        )

                        ParametrosCentroInspeccion(viewModel, modoLectura)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(stringResource(R.string.form_movs_title_optionals), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                        CampoTexto(label = stringResource(R.string.form_codi_ates), valor = codiAtes, placeholder = stringResource(R.string.form_codi_ates_description), onValueChange = { if (!modoLectura) viewModel.campoCodiAtes(it) }, defectColor = false, enabled = !modoLectura)
                        CampoTexto(label = stringResource(R.string.form_name_transportits), valor = nomTransportista, placeholder = stringResource(R.string.form_name_transportits_description), onValueChange = { if (!modoLectura) viewModel.actualizarNomTransportista(it) }, defectColor = false, enabled = !modoLectura)

                        DropdownField(
                            label = stringResource(R.string.form_ways_transports), selectedValue = mitjaTransport,
                            expanded = if (modoLectura) false else mitjaTransportExpandido,
                            placeholder = stringResource(R.string.form_ways_transports_description), opciones = elementosConCodigos.transporte(),
                            onExpandedChange = { if (!modoLectura) viewModel.toggleMitjaTransportExpandido() },
                            onDismissRequest = { viewModel.cerrarMitjaTransportMenu() },
                            onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarMitjaTransport(nombre, codigo) },
                            defectColor = true
                        )

                        CampoTexto(label = stringResource(R.string.form_matricule_transport), valor = matricula, placeholder = stringResource(R.string.form_matricule_transports_description), onValueChange = { if (!modoLectura) viewModel.actualizarMatricula(it) }, defectColor = false, enabled = !modoLectura)
                        CampoTexto(label = stringResource(R.string.form_nif_driver), valor = nifConductor, placeholder = stringResource(R.string.form_nif_driver_description), onValueChange = { if (!modoLectura) viewModel.actualizarNifConductor(it) }, defectColor = false, enabled = !modoLectura)
                        CampoTexto(label = stringResource(R.string.form_name_driver), valor = nomConductor, placeholder = stringResource(R.string.form_name_driver_description), onValueChange = { if (!modoLectura) viewModel.actualizarNomConductor(it) }, defectColor = false, enabled = !modoLectura)

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.form_animal_identifiers), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
                                if (!modoLectura) {
                                    IconButton(
                                        onClick = { viewModel.agregarIdentificador() },
                                        modifier = Modifier.size(36.dp).background(color = MainOrange, shape = RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.content_desc_add_id), tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            identificadors.forEachIndexed { index, identificador ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Animal ${index + 1}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MainOrange)
                                            if (!modoLectura && identificadors.size > 1) {
                                                IconButton(onClick = { viewModel.eliminarIdentificador(index) }, modifier = Modifier.size(32.dp)) {
                                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.content_desc_remove_id), tint = ErrorRed, modifier = Modifier.size(20.dp))
                                                }
                                            }
                                        }
                                        if (!modoLectura) {
                                            useDebounce(identificador, delayMillis = 300L) { viewModel.searchBovinos(index, it) }
                                        }
                                        CampoIdentificadorAutoComplete(
                                            label = stringResource(R.string.form_id_animal),
                                            valor = identificador,
                                            placeholder = stringResource(R.string.form_animal_id_example),
                                            onValueChange = { if (!modoLectura) viewModel.actualizarIdentificador(index, it) },
                                            suggestions = if (modoLectura) emptyList() else if (activeIndex == index) suggestionsBovinos else emptyList(),
                                            onAnimalSelected = { if (!modoLectura) viewModel.onBovinoSelected(index, it) },
                                            isLoadingSuggestions = if (modoLectura) false else isLoadingBovinos,
                                            defectColor = false,
                                            onClickBluetooth = { if (!modoLectura) {
                                                    indiceBluetooth = index
                                                    bluetoothViewModel.iniciarEscaneo(context)
                                                    mostrarBluetooth = true
                                                }
                                            },
                                            onClickUsb = {
                                                indiceUsb = index
                                                usbViewModel.conectar()
                                            }

                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.confirmarAltaGuia() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                        enabled = !estadoCarga,
                        colors = ButtonDefaults.buttonColors(containerColor = MainOrange, disabledContainerColor = MaterialTheme.colorScheme.outline),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.btn_create_guide), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DateTimeField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
            OutlinedTextField(
                value = value, onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = icon,
                readOnly = true, enabled = false,
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
fun ParametrosCentroInspeccion(viewModel: GuiasViewModel, modoLectura: Boolean = false) {
    val pais by viewModel.pais.observeAsState("")
    val codiExplotacio by viewModel.codiExplotacio.observeAsState("")
    val (isChecked, setChecked) = remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { if (!modoLectura) setChecked(it) }
            )
            Text("El destino es centro de inspección?", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
        }
        if (isChecked) {
            CampoTexto(label = stringResource(R.string.form_pif_country), valor = pais, placeholder = stringResource(R.string.form_pif_country_desc), onValueChange = { if (!modoLectura) viewModel.actualizarPais(it) }, defectColor = false, enabled = !modoLectura)
            CampoTexto(label = stringResource(R.string.form_pif_exploitation), valor = codiExplotacio, placeholder = stringResource(R.string.form_pif_exploitation_desc), onValueChange = { if (!modoLectura) viewModel.actualizarCodiExplotacio(it) }, defectColor = false, enabled = !modoLectura)
        }
    }
}