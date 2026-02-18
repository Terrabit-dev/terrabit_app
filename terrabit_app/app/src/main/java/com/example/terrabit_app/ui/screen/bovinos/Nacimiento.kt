package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.CameraAlt
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.viewmodel.NacimientoViewmodel
import kotlin.collections.emptyList
import com.example.terrabit_app.R

import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.utils.alertsErrosScreens
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.viewmodel.BorradorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nacimiento(navController: NavController,
               viewModel: NacimientoViewmodel,
               borradorId: String = "") {
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

    val identificadores: Identificadores by viewModel.identificadores.observeAsState(
        Identificadores(emptyList())
    )

    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeError.observeAsState("")
    val codiError by viewModel.codiError.observeAsState()
    val estadoCarga by viewModel.cargandoNacimiento.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarDialogoAviso by remember { mutableStateOf(false) }
    var cantidadBorradores by remember { mutableStateOf(0) }

    val mensajeRegistroExitoso = stringResource(R.string.successful_message_born)
    val mensajeRegistroError = stringResource(R.string.error_message_born)
    val elementosConCodigos = ElementosConCodigos()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ============================================
    // INICIALIZACIÓN Y DETECCIÓN DE BORRADORES
    // ============================================
    LaunchedEffect(Unit) {
        viewModel.inicializarSharedPreferences(context)
        if (borradorId.isNotEmpty()) {
            viewModel.cargarBorradorPorId(borradorId)
            return@LaunchedEffect
        }

        val borradores = viewModel.obtenerBorradoresNacimiento()
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
                        containerColor = Color(0xFF4A7C59)
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
                message = mensajeRegistroExitoso,
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
                    tint = MainGreen,
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
                    text = if (codiError != null) {
                        alertsErrosScreens(codiError!!)
                    } else mensajeError,
                    fontSize = 16.sp,
                    color = BlueGrey,
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
                        containerColor = MainGreen
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

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePicker() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFecha(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePicker() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainGreen,
                    todayDateBorderColor = MainGreen
                )
            )
        }
    }

    if (mostrarDatePickerIdentificadores) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerIdentificacion() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.seleccionarFechaIdentificacion(millis)
                        }
                    }
                ) {
                    Text(stringResource(R.string.accept_buttom), color = MainGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.ocultarDatePickerIdentificacion() }) {
                    Text(stringResource(R.string.cancel_buttom), color = BlueGrey)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainGreen,
                    todayDateBorderColor = MainGreen
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
                            color = MainGreen,
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
    else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                stringResource(R.string.born_register_name),
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
                        containerColor = MainGreen,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MainGreen, // Verde para éxito
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
                                stringResource(R.string.form_id_mother),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = idMadre,
                                onValueChange = { viewModel.actualizarIdMadre(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_mother_description),
                                        color = BlueGrey
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { /* Acción de cámara */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = "Escanear",
                                            tint = MainGreen
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainGreen,
                                    unfocusedBorderColor = DarkWhiteBackground,
                                    focusedTextColor = DarkBlueGrey,
                                    unfocusedTextColor = DarkBlueGrey,
                                    cursorColor = MainGreen
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false
                                )
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_id_breeding),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = idCria,
                                onValueChange = { viewModel.actualizarIdCria(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_id_breeding_description),
                                        color = BlueGrey
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { /* Acción de cámara */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = "Escanear",
                                            tint = MainGreen
                                        )
                                    }

                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainGreen,
                                    unfocusedBorderColor = DarkWhiteBackground,
                                    focusedTextColor = DarkBlueGrey,
                                    unfocusedTextColor = DarkBlueGrey,
                                    cursorColor = MainGreen
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false
                                )
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_birthdate),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.mostrarDatePicker() }
                            ) {
                                OutlinedTextField(
                                    value = fechaNacimiento,
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
                                            tint = MainGreen
                                        )
                                    },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = DarkBlueGrey,
                                        disabledBorderColor = DarkWhiteBackground,
                                        disabledLeadingIconColor = MainGreen,
                                        disabledPlaceholderColor = BlueGrey
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_date_identification),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.mostrarDatePickerIdentificacion() }
                            ) {
                                OutlinedTextField(
                                    value = fechaIdentificacion,
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
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Calendario",
                                            tint = MainGreen
                                        )
                                    },
                                    readOnly = true,
                                    enabled = false,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = DarkBlueGrey,
                                        disabledBorderColor = DarkWhiteBackground,
                                        disabledLeadingIconColor = MainGreen,
                                        disabledPlaceholderColor = BlueGrey
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        // Sexo
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_sex),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = sexoExpandido,
                                onExpandedChange = { viewModel.toggleSexoExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = sexoSeleccionado,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_sex_description),
                                            color = BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = sexoExpandido
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MainGreen,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = sexoExpandido,
                                    onDismissRequest = { viewModel.cerrarSexoMenu() },
                                    modifier = Modifier
                                        .background(Color.White)
                                ) {
                                    elementosConCodigos.sexos().forEach { (sexo, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    sexo,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarSexo(sexo, codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            ),
                                            colors = MenuDefaults.itemColors(
                                                textColor = DarkBlueGrey,
                                                leadingIconColor = DarkBlueGrey,
                                                trailingIconColor = DarkBlueGrey,
                                                disabledTextColor = BlueGrey
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_raze),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = razaExpandida,
                                onExpandedChange = { viewModel.toggleRazaExpandida() }
                            ) {
                                OutlinedTextField(
                                    value = razaSeleccionada,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_raze_description),
                                            color = BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = razaExpandida
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MainGreen,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = razaExpandida,
                                    onDismissRequest = { viewModel.cerrarRazaMenu() },
                                    modifier = Modifier
                                        .background(Color.White)
                                ) {
                                    viewModel.razasBovinas.forEach { raza ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    raza.nombre,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.seleccionarRaza(raza.nombre, raza.codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            ),
                                            colors = MenuDefaults.itemColors(
                                                textColor = DarkBlueGrey,
                                                leadingIconColor = DarkBlueGrey,
                                                trailingIconColor = DarkBlueGrey,
                                                disabledTextColor = BlueGrey
                                            )
                                        )
                                        if (raza != viewModel.razasBovinas.last()) {
                                            HorizontalDivider(
                                                color = DarkWhiteBackground,
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_aptitude),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = aptitudExpandida,
                                onExpandedChange = { viewModel.toggleAptitudExpandida() }
                            ) {
                                OutlinedTextField(
                                    value = aptitudSeleccionada,
                                    onValueChange = {},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            stringResource(R.string.form_aptitude_description),
                                            color = BlueGrey
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = aptitudExpandida
                                        )
                                    },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MainGreen,
                                        unfocusedBorderColor = DarkWhiteBackground,
                                        focusedTextColor = DarkBlueGrey,
                                        unfocusedTextColor = DarkBlueGrey
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = aptitudExpandida,
                                    onDismissRequest = { viewModel.cerrarAptitudMenu() },
                                    modifier = Modifier
                                        .background(Color.White)
                                ) {
                                    elementosConCodigos.aptitudes().forEach { (aptitud, codigo) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    aptitud,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { viewModel.seleccionarAptitud(aptitud, codigo) },
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 14.dp
                                            ),
                                            colors = MenuDefaults.itemColors(
                                                textColor = DarkBlueGrey,
                                                leadingIconColor = DarkBlueGrey,
                                                trailingIconColor = DarkBlueGrey,
                                                disabledTextColor = BlueGrey
                                            )
                                        )
                                        if (aptitud != viewModel.listaAptitudes.last()) {
                                            HorizontalDivider(
                                                color = DarkWhiteBackground,
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.registrarNacimiento() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen,
                        disabledContainerColor = DarkWhiteBackground
                    ),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text(
                        stringResource(R.string.buttom_form_born),
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