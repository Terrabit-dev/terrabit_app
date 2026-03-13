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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.bovinos.components.CampoIdentificadorAutoComplete
import com.example.terrabit_app.ui.screen.bovinos.components.useDebounce
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.viewmodel.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.IdentificacionAplazaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentificacionApalzada(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<IdentificacionAplazaViewModel>()
    val modoLectura = historialId.isNotEmpty()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val identificadorAnimal by viewModel.identificadorAnimal.observeAsState("")
    val identifiacionExitosa by viewModel.identificacionExitosa.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorIdentificacion.observeAsState("")
    val codiError by viewModel.codiError.observeAsState()
    val estadoCarga by viewModel.estadoCarga.observeAsState(false)
    val fechaIdentificacion by viewModel.fechaIdentificacion.observeAsState("")
    val mostrarDatePickerIdentificadores by viewModel.mostrarDatePickerIdentificacion.observeAsState(false)
    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarBluetooth by remember { mutableStateOf(false) }

    val tituloExito = stringResource(R.string.successful_message_identification_postpone)
    val titulloError = stringResource(R.string.error_message_identification_postpone)

    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)

    if (mostrarBluetooth) {
        BluetoothScanDialog(
            bluetoothViewModel = bluetoothViewModel,
            onMensajeRecibido = { mensaje ->
                viewModel.actualizarIdentificadorAnimal(mensaje)
                mostrarBluetooth = false
            },
            onDismiss = { mostrarBluetooth = false }
        )
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

    LaunchedEffect(identifiacionExitosa) {
        if (identifiacionExitosa) {
            snackbarHostState.showSnackbar(tituloExito, duration = SnackbarDuration.Short)
            viewModel.resetearEstadoIdentificacion()
        }
    }

    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) mostrarDialogoError = true
    }

    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstadoIdentificacion() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MainGreen, modifier = Modifier.size(48.dp)) },
            title = { Text(titulloError, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    if (codiError != null) alertsErrosScreens(codiError!!) else mensajeError,
                    fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstadoIdentificacion() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
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
                            Text(stringResource(R.string.name_identification_postpone), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (modoLectura) "Solo lectura" else stringResource(R.string.subtitle_identification_postpone),
                                fontSize = 13.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.9f)
                            )
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
                                accentColor = MainGreen
                            )
                        }
                        if (!modoLectura) {
                            useDebounce(identificadorAnimal, delayMillis = 300L) { viewModel.searchBovinos(it) }
                        }
                        CampoIdentificadorAutoComplete(
                            label = stringResource(R.string.form_id_animal),
                            valor = identificadorAnimal,
                            placeholder = stringResource(R.string.form_id_animal_description),
                            enabled = !modoLectura,
                            onValueChange = { viewModel.actualizarIdentificadorAnimal(it) },
                            suggestions = suggestionsBovinos,
                            onAnimalSelected = { viewModel.onBovinoSelected(it) },
                            isLoadingSuggestions = isLoadingBovinos,
                            onClickBluetooth = { bluetoothViewModel.iniciarEscaneo(context); mostrarBluetooth = true }
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_date_identification),
                                fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = if (!modoLectura)
                                    Modifier.fillMaxWidth().clickable { viewModel.mostrarDatePickerIdentificacion() }
                                else
                                    Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = fechaIdentificacion,
                                    onValueChange = {},
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
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.corregirIdentificacion() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                        enabled = !estadoCarga,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainGreen,
                            disabledContainerColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.buttom_form_identification_postpone), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}