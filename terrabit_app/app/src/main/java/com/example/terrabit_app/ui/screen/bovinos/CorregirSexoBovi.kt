package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.viewmodel.CorrecionSexoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorregirSexoBovi(navController: NavController, bluetoothViewModel: BluetoothViewModel, borradorId: String = "") {
    val viewModel = hiltViewModel<CorrecionSexoViewModel>()
    val identificadorCorreccionSexo by viewModel.identificadorCorreccionSexo.observeAsState("")
    val sexoCorreccionSeleccionado by viewModel.sexoCorreccionSeleccionado.observeAsState("")
    val sexoCorreccionExpandido by viewModel.sexoCorreccionExpandido.observeAsState(false)
    val correccionSexoExitosa by viewModel.correccionSexoExitosa.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorCorreccionSexo.observeAsState("")
    val codiError by viewModel.codiError.observeAsState()
    val estadoCarga by viewModel.estadoCarga.observeAsState(false)
    val suggestionsBovinos by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos by viewModel.isLoadingBovinos.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarBluetooth by remember { mutableStateOf(false) }

    val mensajeCorreccionSexoExitosa = stringResource(R.string.successful_message_correct_sex)
    val mensajeErrorCorreccionSexo = stringResource(R.string.error_message_correct_sex)
    val elementosConCodigos = remember { ElementosConCodigos() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    if (mostrarBluetooth) {
        BluetoothScanDialog(
            bluetoothViewModel = bluetoothViewModel,
            onMensajeRecibido = { mensaje ->
                viewModel.actualizarIdentificadorCorreccionSexo(mensaje)
                mostrarBluetooth = false
            },
            onDismiss = { mostrarBluetooth = false }
        )
    }

    LaunchedEffect(Unit) {
        if (borradorId.isNotEmpty()) {
            viewModel.cargarBorradorPorId(borradorId)
        } else {
            viewModel.cargarBorradorExistente()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && viewModel.tieneContenido()) {
                viewModel.guardarBorradorAutomatico()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(correccionSexoExitosa) {
        if (correccionSexoExitosa) {
            snackbarHostState.showSnackbar(mensajeCorreccionSexoExitosa, duration = SnackbarDuration.Short)
            viewModel.resetearEstadoCorreccionSexo()
        }
    }

    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) mostrarDialogoError = true
    }

    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstadoCorreccionSexo() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MainGreen, modifier = Modifier.size(48.dp)) },
            title = { Text(mensajeErrorCorreccionSexo, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    if (codiError != null) alertsErrosScreens(codiError!!) else mensajeError,
                    fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstadoCorreccionSexo() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.name_sex_correct), fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Routes.GestionBovinos.route) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                        useDebounce(identificadorCorreccionSexo, delayMillis = 300L) { viewModel.searchBovinos(it) }
                        CampoIdentificadorAutoComplete(
                            label = stringResource(R.string.form_id_animal),
                            valor = identificadorCorreccionSexo,
                            placeholder = stringResource(R.string.form_id_animal_description),
                            onValueChange = { viewModel.actualizarIdentificadorCorreccionSexo(it) },
                            suggestions = suggestionsBovinos,
                            onAnimalSelected = { viewModel.onBovinoSelected(it) },
                            isLoadingSuggestions = isLoadingBovinos,
                            onClickBluetooth = {
                                bluetoothViewModel.iniciarEscaneo(context)
                                mostrarBluetooth = true
                            }
                        )

                        DropdownField(
                            label = stringResource(R.string.form_sex),
                            selectedValue = sexoCorreccionSeleccionado,
                            expanded = sexoCorreccionExpandido,
                            placeholder = stringResource(R.string.form_send_address_description),
                            opciones = elementosConCodigos.sexos(),
                            onExpandedChange = { viewModel.toggleSexoCorreccionExpandido() },
                            onDismissRequest = { viewModel.cerrarSexoCorreccionMenu() },
                            onSeleccionar = { codigo, nombre -> viewModel.seleccionarSexoCorreccion(nombre, codigo) },
                            defectColor = true
                        )
                    }
                }

                Button(
                    onClick = { viewModel.corregirSexoAnimal() },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                ) {
                    Text(stringResource(R.string.buttom_form_correct_sex), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
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
        }
    }
}