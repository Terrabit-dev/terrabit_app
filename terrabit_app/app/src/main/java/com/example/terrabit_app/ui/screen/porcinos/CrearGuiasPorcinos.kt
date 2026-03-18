package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.example.terrabit_app.viewmodel.bovinos.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.porcinos.CrearGuiaPorcinosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearGuiasPorcinos(
    navController: NavController,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<CrearGuiaPorcinosViewModel>()
    val modoLectura = historialId.isNotEmpty()
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val elementosConCodigos = ElementosConCodigosPorcinos()

    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)
    val codiMoActivo by codiMoViewModel.codiMoActivo.observeAsState(null)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarDialogoAviso by remember { mutableStateOf(false) }
    var cantidadBorradores by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty() -> viewModel.cargarBorradorPorId(borradorId)
            else -> {
                cantidadBorradores = viewModel.obtenerCantidadBorradoresPorcinos()
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

    LaunchedEffect(uiState.mensajeExito) {
        if (uiState.mensajeExito != null) {
            snackbarHostState.showSnackbar(uiState.mensajeExito!!, duration = SnackbarDuration.Short)
            viewModel.resetearEstado()
        }
    }

    LaunchedEffect(uiState.mensajeError) {
        if (uiState.mensajeError != null) mostrarDialogoError = true
    }

    if (mostrarDialogoAviso) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Default.Description, contentDescription = null, tint = MainOrange, modifier = Modifier.size(48.dp)) },
            title = { Text("Borradores pendientes", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = {
                Text(
                    "Tienes $cantidadBorradores borradores guardados de este formulario. Puedes verlos en la página de Borradores.\n\n¿Deseas crear uno nuevo?",
                    fontSize = 16.sp, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoAviso = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Crear nuevo", fontWeight = FontWeight.SemiBold) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstado() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MainOrange, modifier = Modifier.size(48.dp)) },
            title = { Text("Error al crear guía", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = { Text(uiState.mensajeError ?: "", fontSize = 16.sp, lineHeight = 24.sp) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstado() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (uiState.mostrarDatePickerSalida && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerSalida() },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaSalida(it) } }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarDatePickerSalida() }) { Text(stringResource(R.string.cancel_buttom), color = BlueGrey) } }
        ) { DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange)) }
    }

    if (uiState.mostrarTimePickerSalida && !modoLectura) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerSalida() },
            confirmButton = { TextButton(onClick = { viewModel.actualizarHoraSalida(timePickerState.hour.toString(), timePickerState.minute.toString()); viewModel.ocultarTimePickerSalida() }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarTimePickerSalida() }) { Text(stringResource(R.string.cancel_buttom), color = BlueGrey) } },
            text = { TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialSelectedContentColor = Color.White, selectorColor = MainOrange)) }
        )
    }

    if (uiState.mostrarDatePickerLlegada && !modoLectura) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerLlegada() },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.seleccionarFechaLlegada(it) } }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarDatePickerLlegada() }) { Text(stringResource(R.string.cancel_buttom), color = BlueGrey) } }
        ) { DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange)) }
    }

    if (uiState.mostrarTimePickerLlegada && !modoLectura) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerLlegada() },
            confirmButton = { TextButton(onClick = { viewModel.actualizarHoraLlegada(timePickerState.hour.toString(), timePickerState.minute.toString()); viewModel.ocultarTimePickerLlegada() }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) } },
            dismissButton = { TextButton(onClick = { viewModel.ocultarTimePickerLlegada() }) { Text(stringResource(R.string.cancel_buttom), color = BlueGrey) } },
            text = { TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialSelectedContentColor = Color.White, selectorColor = MainOrange)) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(R.string.card_crear_guias), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        if (modoLectura) Text("Solo lectura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CodiMoSelector(
                            codisMos = codiMoViewModel.getCodisMos(),
                            seleccionado = codiMoActivo,
                            expanded = codisMoExpandido,
                            onToggle = { codiMoViewModel.toggleCodisMoExpandido() },
                            onDismiss = { codiMoViewModel.cerrarCodisMo() },
                            onSeleccionar = { codi -> codiMoViewModel.seleccionarCodiMo(codi) },
                            accentColor = MainOrange
                        )
                    }

                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_expl_entrada),
                        valor = uiState.explotacion,
                        placeholder = stringResource(R.string.form_porcinos_cod_expl_entrada),
                        onValueChange = { if (!modoLectura) viewModel.actualizarExplotacion(it) },
                        defectColor = false,
                        enabled = !modoLectura
                    )

                    DropdownField(
                        label = stringResource(R.string.form_porcinos_cod_cat),
                        selectedValue = uiState.categoriaSeleccionada,
                        expanded = if (modoLectura) false else uiState.categoriaExpandido,
                        placeholder = stringResource(R.string.form_porcinos_descr_cat),
                        opciones = elementosConCodigos.categorias(),
                        onExpandedChange = { if (!modoLectura) viewModel.toggleCategoriaExpandido() },
                        onDismissRequest = { viewModel.cerrarCategoriaMenu() },
                        onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarCategoria(nombre, codigo) },
                        defectColor = false
                    )

                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_nAnimales),
                        valor = uiState.numAnimales,
                        placeholder = stringResource(R.string.form_porcinos_descr_nAnimales),
                        keyboardType = KeyboardType.Number,
                        onValueChange = { if (!modoLectura) viewModel.actualizarNumAnimales(it) },
                        defectColor = false,
                        enabled = !modoLectura
                    )

                    // Fecha y hora de salida
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.form_date_departure), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkBlueGrey, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier.fillMaxWidth().clickable { if (!modoLectura) viewModel.mostrarDatePickerSalida() }) {
                                OutlinedTextField(
                                    value = uiState.fechaSalida, onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_date_arrival_description), color = BlueGrey) },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                                    readOnly = true, enabled = false, shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = DarkBlueGrey, disabledBorderColor = DarkWhiteBackground, disabledLeadingIconColor = MainOrange, disabledPlaceholderColor = BlueGrey),
                                    singleLine = true
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.form_hour_arrival), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkBlueGrey, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier.fillMaxWidth().clickable { if (!modoLectura) viewModel.mostrarTimePickerSalida() }) {
                                OutlinedTextField(
                                    value = uiState.horaSalida, onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_hour_arrival_description), color = BlueGrey) },
                                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) },
                                    readOnly = true, enabled = false, shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = DarkBlueGrey, disabledBorderColor = DarkWhiteBackground, disabledLeadingIconColor = MainOrange, disabledPlaceholderColor = BlueGrey),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Fecha y hora de llegada
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.form_date_arrival), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkBlueGrey, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier.fillMaxWidth().clickable { if (!modoLectura) viewModel.mostrarDatePickerLlegada() }) {
                                OutlinedTextField(
                                    value = uiState.fechaLlegada, onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_date_arrival_description), color = BlueGrey) },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                                    readOnly = true, enabled = false, shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = DarkBlueGrey, disabledBorderColor = DarkWhiteBackground, disabledLeadingIconColor = MainOrange, disabledPlaceholderColor = BlueGrey),
                                    singleLine = true
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.form_hour_arrival), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkBlueGrey, letterSpacing = 0.15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier.fillMaxWidth().clickable { if (!modoLectura) viewModel.mostrarTimePickerLlegada() }) {
                                OutlinedTextField(
                                    value = uiState.horaLlegada, onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(stringResource(R.string.form_hour_arrival_description), color = BlueGrey) },
                                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) },
                                    readOnly = true, enabled = false, shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = DarkBlueGrey, disabledBorderColor = DarkWhiteBackground, disabledLeadingIconColor = MainOrange, disabledPlaceholderColor = BlueGrey),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_cod_sir),
                        valor = uiState.codigoSIR,
                        placeholder = stringResource(R.string.form_porcinos_descr_sir),
                        onValueChange = { if (!modoLectura) viewModel.actualizarCodigoSIR(it) },
                        defectColor = false,
                        enabled = !modoLectura
                    )

                    DropdownField(
                        label = stringResource(R.string.form_porcinos_medio_trans),
                        selectedValue = uiState.medioTransporteSeleccionado,
                        expanded = if (modoLectura) false else uiState.medioTransporteExpandido,
                        placeholder = stringResource(R.string.form_porcinos_descr_MTransp),
                        opciones = elementosConCodigos.medios(),
                        onExpandedChange = { if (!modoLectura) viewModel.toggleMedioTransporteExpandido() },
                        onDismissRequest = { viewModel.cerrarMedioTransporteMenu() },
                        onSeleccionar = { codigo, nombre -> if (!modoLectura) viewModel.seleccionarMedioTransporte(nombre, codigo) },
                        defectColor = false
                    )

                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_matricula),
                        valor = uiState.matricula,
                        placeholder = stringResource(R.string.form_porcinos_descr_matricula),
                        onValueChange = { if (!modoLectura) viewModel.actualizarMatricula(it) },
                        defectColor = false,
                        enabled = !modoLectura
                    )

                    CampoTexto(
                        label = stringResource(R.string.form_porcinos_nifCond),
                        valor = uiState.nifConductor,
                        placeholder = stringResource(R.string.form_porcinos_descr_nifCond),
                        onValueChange = { if (!modoLectura) viewModel.actualizarNifConductor(it) },
                        defectColor = false,
                        enabled = !modoLectura
                    )

                    if (!modoLectura) {
                        Button(
                            onClick = { viewModel.crearGuia() },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(56.dp),
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MainOrange, disabledContainerColor = DarkWhiteBackground),
                            shape = MaterialTheme.shapes.medium,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(text = stringResource(R.string.card_crear_guias), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}