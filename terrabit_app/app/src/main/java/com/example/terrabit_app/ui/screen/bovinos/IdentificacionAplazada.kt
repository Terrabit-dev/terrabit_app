package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.alertsErrosScreens
import com.example.terrabit_app.viewmodel.IdentificacionAplazaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentificacionApalzada(navController: NavController, viewModel: IdentificacionAplazaViewModel){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observar variables del ViewModel
    val identificadorAnimal by viewModel.identificadorAnimal.observeAsState("")
    val identifiacionExitosa by viewModel.identificacionExitosa.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorIdentificacion.observeAsState("")
    val codiError by viewModel.codiError.observeAsState()
    val estadoCarga by viewModel.estadoCarga.observeAsState(false)
    val fechaIdentificacion by viewModel.fechaIdentificacion.observeAsState("")
    val mostrarDatePickerIdentificadores by viewModel.mostrarDatePickerIdentificacion.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mostrarDialogoRecuperacion by remember { mutableStateOf(false) }

    val tituloExito = stringResource(id = R.string.successful_message_identification_postpone)
    val titulloError = stringResource(id = R.string.error_message_identification_postpone)

    // ============================================
    // INICIALIZACIÓN Y CARGA DE BORRADOR
    // ============================================
    LaunchedEffect(Unit) {
        viewModel.inicializarSharedPreferences(context)

        // Verificar si hay borrador guardado
        if (viewModel.tieneContenido()) {
            // Ya hay datos cargados, no hacer nada
        } else {
            // Intentar cargar borrador existente
            viewModel.cargarBorradorExistente()

            // Si después de cargar hay contenido, mostrar diálogo
            if (viewModel.tieneContenido()) {
                mostrarDialogoRecuperacion = true
            }
        }
    }

    // ============================================
    // DETECCIÓN DE CICLO DE VIDA (AUTOGUARDADO)
    // ============================================
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // Usuario sale de la pantalla - GUARDAR AUTOMÁTICAMENTE
                    if (viewModel.tieneContenido()) {
                        viewModel.guardarBorradorAutomatico()
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    // Pantalla ya no visible
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // ============================================
    // DIÁLOGO DE RECUPERACIÓN DE BORRADOR
    // ============================================
    if (mostrarDialogoRecuperacion) {
        AlertDialog(
            onDismissRequest = { },
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
                    text = "Borrador encontrado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkBlueGrey
                )
            },
            text = {
                Text(
                    text = "Se encontró un formulario sin completar. ¿Deseas recuperarlo?",
                    fontSize = 16.sp,
                    color = BlueGrey,
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoRecuperacion = false
                        // Los datos ya están cargados
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Recuperar", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoRecuperacion = false
                        viewModel.eliminarBorradorAutomatico()
                        viewModel.limpiarFormulario()
                    }
                ) {
                    Text("Descartar", color = BlueGrey)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Mostrar Snackbar cuando hay mensaje de éxito
    LaunchedEffect(identifiacionExitosa) {
        if (identifiacionExitosa) {
            snackbarHostState.showSnackbar(
                message = tituloExito,
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoIdentificacion()
        }
    }

    // Mostrar diálogo cuando hay error
    LaunchedEffect(mensajeError, codiError) {
        if (mensajeError.isNotEmpty() || codiError != null) {
            mostrarDialogoError = true
        }
    }

    // Diálogo de Error
    if (mostrarDialogoError) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoError = false
                viewModel.resetearEstadoIdentificacion()
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
                    text = titulloError,
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
                        viewModel.resetearEstadoIdentificacion()
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

    // Indicador de carga en pantalla completa
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
                                stringResource(R.string.name_identification_postpone),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                stringResource(R.string.subtitle_identification_postpone),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
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
                        // Identificador del Animal
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.form_id_animal),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = identificadorAnimal,
                                onValueChange = { viewModel.actualizarIdentificadorAnimal(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        stringResource(R.string.form_id_animal_description),
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

                        // Fecha de Identificación
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
                    }
                }

                // Botón Corregir Identificacion
                Button(
                    onClick = { viewModel.corregirIdentificacion() },
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
                        stringResource(R.string.buttom_form_identification_postpone),
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