package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.utils.components.CampoIdentificadorAutoComplete
import com.example.terrabit_app.utils.components.useDebounce
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.AnimalSeleccionadoHolder
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.bluetooth.BluetoothScanDialog
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.utils.usb.UsbSerialViewModel
import com.example.terrabit_app.viewmodel.bovinos.MaterialDuplicadoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDuplicadosScreen(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<MaterialDuplicadoViewModel>()
    val modoLectura = historialId.isNotEmpty()

    val elementosConCodigos = ElementosConCodigos()

    val empresaSubministradora      by viewModel.empresaSubministradora.observeAsState("")
    val tipoEnviamiento             by viewModel.tipoEnviamiento.observeAsState(null)
    val tipoDireccionEnvio          by viewModel.tipoEnviamiento.observeAsState(null)
    val oficinaComarcal             by viewModel.oficinaComarcal.observeAsState("")
    val direccionEnvio              by viewModel.direccion.observeAsState("")
    val poblacion                   by viewModel.poblacion.observeAsState("")
    val municipio                   by viewModel.municipio.observeAsState("")
    val codigoPostal                by viewModel.codigoPostal.observeAsState("")
    val telefono                    by viewModel.telefonoContacto.observeAsState("")
    val empresaExpandida            by viewModel.empresaExpandida.observeAsState(false)
    val tipoEnviamientoExpandido    by viewModel.tipoEnviamientoExpandido.observeAsState(false)
    val direccionEnvioExpandido     by viewModel.destinoExpandido.observeAsState(false)
    val oficinaComarcalExpandido    by viewModel.oficinaComarcalExpandida.observeAsState(false)
    val registroExitoso             by viewModel.operacionExitosa.observeAsState(false)
    val mensajeError                by viewModel.mensajeError.observeAsState("")
    val cargando                    by viewModel.estadoCarga.observeAsState(false)
    val suggestionsBovinos          by viewModel.suggestionsBovinos.observeAsState(emptyList())
    val isLoadingBovinos            by viewModel.isLoadingBovinos.observeAsState(false)
    val activeIndex                 by viewModel.activeFieldIndex.observeAsState(-1)

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError  by remember { mutableStateOf(false) }
    var indiceBluetooth      by remember { mutableStateOf<Int?>(null) }
    var mostrarBluetooth     by remember { mutableStateOf(false) }
    var indiceUsb            by remember { mutableStateOf<Int?>(null) }
    var procedeDeLista       by remember { mutableStateOf(false) }

    val codisMoExpandido by viewModel.codisMoExpandido.observeAsState(false)
    val codiMoActivo by viewModel.codiMoActivo.observeAsState(null)

    val successMessage       = stringResource(R.string.success_duplicate_request)

    val direccionAlternativa     = "03"
    val direccionExplotacion     = "02"
    val direccionOficinaComarcal = "01"

    val usbViewModel = hiltViewModel<UsbSerialViewModel>()
    val usbState     by usbViewModel.state.collectAsState()
    val usbErrorText = usbState.error?.let { stringResource(it) }

    // ── USB: traducción automática de prefijo de país ──────────────────────
    LaunchedEffect(Unit) {
        usbViewModel.mensajes.collect { mensaje ->
            indiceUsb?.let { viewModel.actualizarIdentificadorDesdeHardware(it, mensaje) }
            indiceUsb = null
        }
    }

    LaunchedEffect(usbErrorText) {
        usbErrorText?.let {
            snackbarHostState.showSnackbar(message = "USB: $it", duration = SnackbarDuration.Short)
        }
    }

    // ── Bluetooth: traducción automática de prefijo de país ───────────────
    if (mostrarBluetooth) {
        BluetoothScanDialog(
            bluetoothViewModel = bluetoothViewModel,
            onMensajeRecibido = { mensaje ->
                indiceBluetooth?.let { viewModel.actualizarIdentificadorDesdeHardware(it, mensaje) }
                mostrarBluetooth = false
                indiceBluetooth  = null
            },
            onDismiss = {
                mostrarBluetooth = false
                indiceBluetooth  = null
            }
        )
    }

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(successMessage, duration = SnackbarDuration.Short)
            viewModel.resetearEstado()
        }
    }

    LaunchedEffect(mensajeError) {
        if (mensajeError.isNotEmpty()) mostrarDialogoError = true
    }

    LaunchedEffect(Unit) {
        val animalId = AnimalSeleccionadoHolder.consume()
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty()  -> viewModel.cargarBorradorPorId(borradorId)
            animalId.isNotEmpty()    -> {
                viewModel.precargarAnimal(animalId)
                procedeDeLista = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!modoLectura) viewModel.guardarBorradorAutomatico()
        }
    }

    if (mostrarDialogoError && mensajeError.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstado() },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null,
                    tint = ErrorRed, modifier = Modifier.size(48.dp))
            },
            title = {
                Text(stringResource(R.string.title_duplicate_error), fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Text(mensajeError, fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp)
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstado() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (cargando) {
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp),
                            color = MainGreen, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.loading_processing),
                            fontSize = 14.sp, fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            Text(stringResource(R.string.duplicate_request_name),
                                fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            if (modoLectura) Text("Solo lectura",
                                fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when {
                                historialId.isNotEmpty() -> navController.popBackStack()
                                borradorId.isNotEmpty()  -> navController.popBackStack()
                                procedeDeLista -> navController.navigate(Routes.ListarBovinos.route)
                                else -> navController.navigate(Routes.MaterialCategoria.route)
                            }
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_description_back))
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
                    Snackbar(snackbarData = data, containerColor = MainGreen,
                        contentColor = Color.White, shape = RoundedCornerShape(12.dp))
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

                // ── Card datos envío ───────────────────────────────────────
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
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CodiMoSelector(
                                codisMos = viewModel.getCodisMos(),
                                seleccionado = codiMoActivo,
                                expanded = codisMoExpandido,
                                onToggle = { viewModel.toggleCodisMoExpandido() },
                                onDismiss = { viewModel.cerrarCodisMo() },
                                onSeleccionar = { codi -> viewModel.seleccionarCodiMo(codi) },
                                accentColor = MainGreen
                            )
                        }
                        DropdownField(
                            label = stringResource(R.string.form_suply_company) + " *",
                            selectedValue = empresaSubministradora,
                            expanded = empresaExpandida,
                            placeholder = stringResource(R.string.form_suply_company_description),
                            opciones = elementosConCodigos.getEmpresaSubministradora(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleEmpresaExpandida() },
                            onDismissRequest = { viewModel.cerrarEmpresaMenu() },
                            onSeleccionar = { codigo, nombre ->
                                viewModel.seleccionarEmpresa(codigo, nombre)
                            },
                            accentColor = MainGreen
                        )
                        DropdownField(
                            label = stringResource(R.string.form_send_type) + " *",
                            selectedValue = tipoEnviamiento,
                            expanded = tipoEnviamientoExpandido,
                            placeholder = stringResource(R.string.form_send_type_description),
                            opciones = elementosConCodigos.getTiposEnvios(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleTipoEnviamientoExpandido() },
                            onDismissRequest = { viewModel.cerrarTipoEnviamientoMenu() },
                            onSeleccionar = { codigo, nombre ->
                                viewModel.seleccionarTipoEnviamiento(nombre, codigo)
                            },
                            accentColor = MainGreen
                        )
                        DropdownField(
                            label = stringResource(R.string.form_send_address) + " *",
                            selectedValue = tipoDireccionEnvio,
                            expanded = direccionEnvioExpandido,
                            placeholder = stringResource(R.string.form_send_address_description),
                            opciones = elementosConCodigos.getTiposDireccionEnvio(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleDestinoExpandido() },
                            onDismissRequest = { viewModel.cerrarDestinoMenu() },
                            onSeleccionar = { codigo, nombre ->
                                viewModel.seleccionarDestino(nombre, codigo)
                            },
                            accentColor = MainGreen
                        )

                        if (viewModel.getCodiDestinoEnvio() == direccionOficinaComarcal) {
                            DropdownField(
                                label = stringResource(R.string.form_comarcal_office) + " *",
                                selectedValue = oficinaComarcal,
                                expanded = oficinaComarcalExpandido,
                                placeholder = stringResource(R.string.form_comarcal_office_description),
                                opciones = elementosConCodigos.getOficinasComarcales(),
                                enabled = !modoLectura,
                                onExpandedChange = { viewModel.toggleOficinaComarcalExpandida() },
                                onDismissRequest = { viewModel.cerrarOficinaComarcalMenu() },
                                onSeleccionar = { codigo, nombre ->
                                    viewModel.seleccionarOficinaComarcal(codigo, nombre)
                                },
                                accentColor = MainGreen
                            )
                        }

                        if (viewModel.getCodiDestinoEnvio() == direccionExplotacion ||
                            viewModel.getCodiDestinoEnvio() == direccionAlternativa) {
                            if (viewModel.getCodiDestinoEnvio() == direccionExplotacion) {
                                Text(stringResource(R.string.mesagge_send_dades),
                                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 0.15.sp)
                            }
                            CampoTexto(
                                label = stringResource(R.string.form_address) + " *",
                                valor = direccionEnvio,
                                placeholder = stringResource(R.string.form_address_description),
                                onValueChange = { viewModel.actualizarDireccion(it) },
                                defectColor = false, enabled = !modoLectura
                            )
                            CampoTexto(
                                label = stringResource(R.string.form_poblacion) + " *",
                                valor = poblacion,
                                placeholder = stringResource(R.string.form_poblacion_description),
                                onValueChange = { viewModel.actualizarPoblacion(it) },
                                defectColor = false, enabled = !modoLectura
                            )
                            CampoTexto(
                                label = stringResource(R.string.form_postal_code) + " *",
                                valor = codigoPostal,
                                placeholder = stringResource(R.string.form_postal_code_description),
                                keyboardType = KeyboardType.Number,
                                onValueChange = { viewModel.actualizarCodigoPostal(it) },
                                defectColor = false, enabled = !modoLectura
                            )
                            CampoTexto(
                                label = stringResource(R.string.form_municipality) + " *",
                                valor = municipio,
                                placeholder = stringResource(R.string.form_municipality_description),
                                onValueChange = { viewModel.actualizarMunicipio(it) },
                                defectColor = false, enabled = !modoLectura
                            )
                            CampoTexto(
                                label = stringResource(R.string.form_contact_phone) + " *",
                                valor = telefono,
                                placeholder = stringResource(R.string.form_contact_phone_description),
                                keyboardType = KeyboardType.Phone,
                                onValueChange = { viewModel.actualizarTelefonoContacto(it) },
                                defectColor = false, enabled = !modoLectura
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Card identificadores ───────────────────────────────────
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
                            Text(stringResource(R.string.title_identifiers),
                                fontSize = 17.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface)
                            if (!modoLectura) {
                                TextButton(
                                    onClick = { viewModel.agregarAnimal() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MainGreen)
                                ) {
                                    Icon(Icons.Default.Add,
                                        contentDescription = stringResource(R.string.action_add),
                                        modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.action_add),
                                        fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                        val animales by viewModel.listaAnimales.observeAsState(emptyList())
                        val materialesExpandidoPorIndice by viewModel.tipoMaterialExpandidoPorIndice
                            .observeAsState(emptyMap())

                        animales.forEachIndexed { indice, animal ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        stringResource(R.string.label_identifier_count) + " ${indice + 1}",
                                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!modoLectura && animales.size > 1) {
                                        IconButton(
                                            onClick = { viewModel.eliminarAnimal(indice) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete,
                                                contentDescription = "Eliminar identificador",
                                                tint = ErrorRed, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }

                                if (!modoLectura) {
                                    useDebounce(animal.identificador, delayMillis = 300L) {
                                        viewModel.searchBovinosConCampo(indice, it)
                                    }
                                }

                                CampoIdentificadorAutoComplete(
                                    label = stringResource(R.string.form_id_animal),
                                    valor = animal.identificador,
                                    placeholder = stringResource(R.string.form_animal_id_example),
                                    enabled = !modoLectura,
                                    onValueChange = {
                                        if (!modoLectura)
                                            viewModel.actualizarIdentificador(indice, it)
                                    },
                                    suggestions = if (modoLectura || activeIndex != indice)
                                        emptyList() else suggestionsBovinos,
                                    onAnimalSelected = {
                                        if (!modoLectura) viewModel.onBovinoSelected(indice, it)
                                    },
                                    isLoadingSuggestions = !modoLectura && isLoadingBovinos,
                                    defectColor = true,
                                    onClickBluetooth = {
                                        if (!modoLectura) {
                                            indiceBluetooth = indice
                                            bluetoothViewModel.iniciarEscaneo(context)
                                            mostrarBluetooth = true
                                        }
                                    },
                                    onClickUsb = {
                                        if (!modoLectura) {
                                            indiceUsb = indice
                                            usbViewModel.conectar()
                                        }
                                    }
                                )

                                DropdownField(
                                    label = stringResource(R.string.form_material_type) + " *",
                                    selectedValue = elementosConCodigos
                                        .getTiposMaterialDuplicadosId(animal.tipusMaterial),
                                    expanded = materialesExpandidoPorIndice[indice] ?: false,
                                    placeholder = stringResource(R.string.form_state_arrival_description),
                                    opciones = elementosConCodigos.getTiposMaterialDuplicados(),
                                    enabled = !modoLectura,
                                    onExpandedChange = {
                                        viewModel.toggleTipoMaterialExpandido(indice)
                                    },
                                    onDismissRequest = { viewModel.cerrarTipoMaterialMenu(indice) },
                                    onSeleccionar = { codigo, _ ->
                                        viewModel.seleccionarTipoMaterialIdentificador(indice, codigo)
                                    },
                                    accentColor = MainGreen
                                )

                                if (indice < animales.size - 1) Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.solicitarDuplicado() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                            .height(56.dp),
                        enabled = !cargando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainGreen,
                            disabledContainerColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.btn_duplicate_request),
                            fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}