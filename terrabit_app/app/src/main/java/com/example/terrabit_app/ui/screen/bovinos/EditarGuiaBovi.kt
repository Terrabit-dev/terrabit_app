package com.example.terrabit_app.ui.screen.bovinos


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.guias.Guia
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.viewmodel.bovinos.EditarGuiaBoviViewModel

// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla de edición / confirmación de guía bovina
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarGuiaBovi(
    navController: NavController,
    guiaSeleccionada: Guia,                                   // La guía que venía de la lista
    viewModel: EditarGuiaBoviViewModel = hiltViewModel()
) {
    // ─── Cargar datos al entrar a la pantalla ────────────────────────────────
    LaunchedEffect(guiaSeleccionada.remo) {
        viewModel.cargarDatosGuia(guiaSeleccionada)
    }

    // ─── Observar LiveData ───────────────────────────────────────────────────
    val dataSortida           by viewModel.dataSortida.observeAsState("")
    val horaSortida           by viewModel.horaSortida.observeAsState("")
    val dataArribada          by viewModel.dataArribada.observeAsState("")
    val horaArribada          by viewModel.horaArribada.observeAsState("")
    val codiAtes              by viewModel.codiAtes.observeAsState("")
    val nomTransportista      by viewModel.nomTransportista.observeAsState("")
    val mitjaTransport        by viewModel.mitjaTransport.observeAsState("")
    val mitjaTransportExp     by viewModel.mitjaTransportExpandido.observeAsState(false)
    val matricula             by viewModel.matricula.observeAsState("")
    val nifConductor          by viewModel.nifConductor.observeAsState("")
    val nomConductor          by viewModel.nomConductor.observeAsState("")
    val identificadors        by viewModel.identificadors.observeAsState(listOf(""))
    val cargando              by viewModel.cargando.observeAsState(false)
    val error                 by viewModel.error.observeAsState(null)

    val mostrarDatePickerS    by viewModel.mostrarDatePickerSortida.observeAsState(false)
    val mostrarTimePickerS    by viewModel.mostrarTimePickerSortida.observeAsState(false)
    val mostrarDatePickerA    by viewModel.mostrarDatePickerArribada.observeAsState(false)
    val mostrarTimePickerA    by viewModel.mostrarTimePickerArribada.observeAsState(false)

    val elementosConCodigos   = ElementosConCodigos()
    val snackbarHostState     = remember { SnackbarHostState() }

    // Mostrar snackbar cuando hay error
    LaunchedEffect(error) {
        if (!error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message  = error!!,
                duration = SnackbarDuration.Long
            )
            viewModel.resetearError()
        }
    }

    // ─── Date / Time Pickers ─────────────────────────────────────────────────

    if (mostrarDatePickerS) {
        val dpState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerSortida() },
            confirmButton    = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { viewModel.seleccionarFechaSortida(it) }
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.ocultarDatePickerSortida() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state  = dpState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainOrange,
                    todayDateBorderColor      = MainOrange
                )
            )
        }
    }

    if (mostrarTimePickerS) {
        val tpState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerSortida() },
            confirmButton    = {
                TextButton(onClick = {
                    viewModel.actualizarHoraSortida(tpState.hour.toString(), tpState.minute.toString())
                    viewModel.ocultarTimePickerSortida()
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.ocultarTimePickerSortida() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor   = MaterialTheme.colorScheme.surface,
            text             = {
                TimePicker(
                    state  = tpState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor                = MainOrange
                    )
                )
            }
        )
    }

    if (mostrarDatePickerA) {
        val dpState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.ocultarDatePickerArribada() },
            confirmButton    = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { viewModel.seleccionarFechaArribada(it) }
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.ocultarDatePickerArribada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state  = dpState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MainOrange,
                    todayDateBorderColor      = MainOrange
                )
            )
        }
    }

    if (mostrarTimePickerA) {
        val tpState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.ocultarTimePickerArribada() },
            confirmButton    = {
                TextButton(onClick = {
                    viewModel.actualizarHoraArribada(tpState.hour.toString(), tpState.minute.toString())
                    viewModel.ocultarTimePickerArribada()
                }) { Text(stringResource(R.string.accept_buttom), color = MainOrange) }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.ocultarTimePickerArribada() }) {
                    Text(stringResource(R.string.cancel_buttom), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor   = MaterialTheme.colorScheme.surface,
            text             = {
                TimePicker(
                    state  = tpState,
                    colors = TimePickerDefaults.colors(
                        clockDialSelectedContentColor = Color.White,
                        selectorColor                = MainOrange
                    )
                )
            }
        )
    }

    // ─── Overlay de carga ────────────────────────────────────────────────────

    if (cargando) {
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier  = Modifier.size(120.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape     = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(48.dp),
                            color       = MainOrange,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Guardando...",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                            Text(
                                text       = "Editar guía",
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text       = guiaSeleccionada.remo,
                                fontSize   = 12.sp,
                                color      = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.content_description_back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor        = MainOrange,
                        titleContentColor     = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData   = data,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor   = MaterialTheme.colorScheme.error,
                        shape          = RoundedCornerShape(12.dp)
                    )
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

                // ── Cabecera informativa (solo lectura) ───────────────────
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors    = CardDefaults.cardColors(containerColor = MainOrange.copy(alpha = 0.08f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape     = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint     = MainOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text       = "${guiaSeleccionada.explotacioOrigen}  →  ${guiaSeleccionada.explotacioDestinacio}",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text     = "${guiaSeleccionada.numeroAnimals} animales · ${guiaSeleccionada.remo}",
                                fontSize = 12.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Card principal: fechas y transporte ───────────────────
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape     = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text       = "Datos del movimiento",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )

                        // Fecha y hora de salida
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DateTimeField(
                                modifier = Modifier.weight(1f),
                                label    = stringResource(R.string.form_date_departure),
                                value    = dataSortida,
                                placeholder = stringResource(R.string.form_date_description),
                                icon    = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                                onClick = { viewModel.mostrarDatePickerSortida() }
                            )
                            DateTimeField(
                                modifier    = Modifier.weight(1f),
                                label       = stringResource(R.string.form_hour_arrival),
                                value       = horaSortida,
                                placeholder = stringResource(R.string.form_hour_arrival_description),
                                icon        = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) },
                                onClick     = { viewModel.mostrarTimePickerSortida() }
                            )
                        }

                        // Fecha y hora de llegada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DateTimeField(
                                modifier    = Modifier.weight(1f),
                                label       = stringResource(R.string.form_date_arrival),
                                value       = dataArribada,
                                placeholder = stringResource(R.string.form_date_description),
                                icon        = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                                onClick     = { viewModel.mostrarDatePickerArribada() }
                            )
                            DateTimeField(
                                modifier    = Modifier.weight(1f),
                                label       = stringResource(R.string.form_hour_arrival),
                                value       = horaArribada,
                                placeholder = stringResource(R.string.form_hour_arrival_description),
                                icon        = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MainOrange) },
                                onClick     = { viewModel.mostrarTimePickerArribada() }
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                        Text(
                            text       = "Datos de transporte",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )

                        CampoTexto(
                            label         = stringResource(R.string.form_codi_ates),
                            valor         = codiAtes,
                            placeholder   = stringResource(R.string.form_codi_ates_description),
                            onValueChange = viewModel::actualizarCodiAtes,
                            defectColor   = false
                        )

                        CampoTexto(
                            label         = stringResource(R.string.form_name_transportits),
                            valor         = nomTransportista,
                            placeholder   = stringResource(R.string.form_name_transportits_description),
                            onValueChange = viewModel::actualizarNomTransportista,
                            defectColor   = false
                        )

                        DropdownField(
                            label           = stringResource(R.string.form_ways_transports),
                            selectedValue   = mitjaTransport,
                            expanded        = mitjaTransportExp,
                            placeholder     = stringResource(R.string.form_ways_transports_description),
                            opciones        = elementosConCodigos.transporte(),
                            onExpandedChange = { viewModel.toggleMitjaTransportExpandido() },
                            onDismissRequest = { viewModel.cerrarMitjaTransportMenu() },
                            onSeleccionar    = { codigo, nombre -> viewModel.seleccionarMitjaTransport(nombre, codigo) },
                            defectColor      = true
                        )

                        CampoTexto(
                            label         = stringResource(R.string.form_matricule_transport),
                            valor         = matricula,
                            placeholder   = stringResource(R.string.form_matricule_transports_description),
                            onValueChange = viewModel::actualizarMatricula,
                            defectColor   = false
                        )

                        CampoTexto(
                            label         = stringResource(R.string.form_nif_driver),
                            valor         = nifConductor,
                            placeholder   = stringResource(R.string.form_nif_driver_description),
                            onValueChange = viewModel::actualizarNifConductor,
                            defectColor   = false
                        )

                        CampoTexto(
                            label         = stringResource(R.string.form_name_driver),
                            valor         = nomConductor,
                            placeholder   = stringResource(R.string.form_name_driver_description),
                            onValueChange = viewModel::actualizarNomConductor,
                            defectColor   = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Card de identificadores ───────────────────────────────
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape     = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cabecera con botón añadir
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = stringResource(R.string.form_animal_identifiers),
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick  = { viewModel.agregarIdentificador() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(color = MainOrange, shape = RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    imageVector    = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.content_desc_add_id),
                                    tint     = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Lista de identificadores
                        identificadors.forEachIndexed { index, identificador ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn(),
                                exit    = fadeOut()
                            ) {
                                Card(
                                    modifier  = Modifier.fillMaxWidth(),
                                    colors    = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape     = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Cabecera de cada identificador
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment     = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text       = "Animal ${index + 1}",
                                                fontSize   = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color      = MainOrange
                                            )
                                            // Solo mostrar eliminar si hay más de uno
                                            if (identificadors.size > 1) {
                                                IconButton(
                                                    onClick  = { viewModel.eliminarIdentificador(index) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector    = Icons.Default.Delete,
                                                        contentDescription = stringResource(R.string.content_desc_remove_id),
                                                        tint     = ErrorRed,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }

                                        CampoTexto(
                                            label         = stringResource(R.string.form_id_animal),
                                            valor         = identificador,
                                            placeholder   = stringResource(R.string.form_animal_id_example),
                                            onValueChange = { viewModel.actualizarIdentificador(index, it) },
                                            defectColor   = false
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Botón de confirmación ─────────────────────────────────
                Button(
                    onClick  = {
                        viewModel.confirmarModificacion {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled  = !cargando,
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = MainOrange,
                        disabledContainerColor = MaterialTheme.colorScheme.outline
                    ),
                    shape     = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation  = 2.dp,
                        pressedElevation  = 6.dp
                    )
                ) {
                    Text(
                        text       = "Confirmar modificación",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}