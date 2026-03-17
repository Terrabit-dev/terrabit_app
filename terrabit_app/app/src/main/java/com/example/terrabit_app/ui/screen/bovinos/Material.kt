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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.utils.DropdownField
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.viewmodel.CodiMoManagerViewModel
import com.example.terrabit_app.viewmodel.MaterialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material(
    navController: NavController,
    borradorId: String = "",
    historialId: String = ""
) {
    val viewModel = hiltViewModel<MaterialViewModel>()
    val modoLectura = historialId.isNotEmpty()

    val empresaSubministradora by viewModel.empresaSubministradora.observeAsState("")
    val tipoEnviamiento by viewModel.tipoEnviamiento.observeAsState("")
    val destinoLliurament by viewModel.destinoLliurament.observeAsState("")
    val oficinaComarcal by viewModel.oficinaComarcal.observeAsState("")
    val direccion by viewModel.direccion.observeAsState("")
    val poblacion by viewModel.poblacion.observeAsState("")
    val codigoPostal by viewModel.codigoPostal.observeAsState("")
    val municipio by viewModel.municipio.observeAsState("")
    val telefonoContacto by viewModel.telefonoContacto.observeAsState("")
    val tipoMaterial by viewModel.tipoMaterial.observeAsState("")
    val empresaExpandida by viewModel.empresaExpandida.observeAsState(false)
    val tipoEnviamientoExpandido by viewModel.tipoEnviamientoExpandido.observeAsState(false)
    val destinoExpandido by viewModel.destinoExpandido.observeAsState(false)
    val oficinaComarcalExpandida by viewModel.oficinaComarcalExpandida.observeAsState(false)
    val tipoMaterialExpandido by viewModel.tipoMaterialExpandido.observeAsState(false)
    val registroExitoso by viewModel.registroMaterialExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorMaterial.observeAsState("")
    val estadoCarga by viewModel.cargandoMaterial.observeAsState(false)
    val listaUnidades by viewModel.listaUnidades.observeAsState(emptyList())

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    val elementosConCodigos = ElementosConCodigos()
    val codigoDestino = viewModel.getCodiDestinoEnvio()
    val codiMoObligatorio = viewModel.codiMoEsObligatorio()

    val mensajeExito = stringResource(R.string.successful_message_material)

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(mensajeExito, duration = SnackbarDuration.Short)
            viewModel.resetearEstadoRegistroMaterial()
        }
    }

    LaunchedEffect(mensajeError) {
        if (mensajeError.isNotEmpty()) mostrarDialogoError = true
    }

    LaunchedEffect(Unit) {
        when {
            historialId.isNotEmpty() -> viewModel.cargarDesdeHistorial(historialId)
            borradorId.isNotEmpty() -> viewModel.cargarBorradorPorId(borradorId)
        }
    }

    if (mostrarDialogoError && mensajeError.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetearEstadoRegistroMaterial() },
            icon = { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MainGreen, modifier = Modifier.size(48.dp)) },
            title = { Text(stringResource(R.string.error_message_material), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(mensajeError, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetearEstadoRegistroMaterial() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
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
                            Text(stringResource(R.string.name_request_material), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            if (modoLectura) Text("Solo lectura", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when {
                                historialId.isNotEmpty() -> navController.popBackStack()
                                borradorId.isNotEmpty() -> navController.popBackStack()
                                else -> navController.navigate(Routes.MaterialCategoria.route)
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MainGreen, titleContentColor = Color.White, navigationIconContentColor = Color.White)
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(snackbarData = data, containerColor = MainGreen, contentColor = Color.White, shape = RoundedCornerShape(12.dp))
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        DropdownField(
                            label = stringResource(R.string.form_suply_company) + " *",
                            selectedValue = empresaSubministradora,
                            expanded = empresaExpandida,
                            placeholder = stringResource(R.string.form_suply_company_description),
                            opciones = elementosConCodigos.tipoEmpresaSubministradora(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleEmpresaExpandida() },
                            onDismissRequest = { viewModel.cerrarEmpresaMenu() },
                            onSeleccionar = { codigo, nombre -> viewModel.seleccionarEmpresa(nombre, codigo) },
                            defectColor = true
                        )
                        DropdownField(
                            label = stringResource(R.string.form_send_type) + " *",
                            selectedValue = tipoEnviamiento,
                            expanded = tipoEnviamientoExpandido,
                            placeholder = stringResource(R.string.form_send_type_description),
                            opciones = elementosConCodigos.tiposEnvios(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleTipoEnviamientoExpandido() },
                            onDismissRequest = { viewModel.cerrarTipoEnviamientoMenu() },
                            onSeleccionar = { codigo, nombre -> viewModel.seleccionarTipoEnviamiento(nombre, codigo) },
                            defectColor = true
                        )
                        DropdownField(
                            label = stringResource(R.string.form_send_address) + " *",
                            selectedValue = destinoLliurament,
                            expanded = destinoExpandido,
                            placeholder = stringResource(R.string.form_send_address_description),
                            opciones = elementosConCodigos.tiposDireccionEnvio(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleDestinoExpandido() },
                            onDismissRequest = { viewModel.cerrarDestinoMenu() },
                            onSeleccionar = { codigo, nombre -> viewModel.seleccionarDestino(nombre, codigo) },
                            defectColor = true
                        )

                        if (codigoDestino == "01") {
                            DropdownField(
                                label = stringResource(R.string.form_comarcal_office) + " *",
                                selectedValue = oficinaComarcal,
                                expanded = oficinaComarcalExpandida,
                                placeholder = stringResource(R.string.form_comarcal_office_description),
                                opciones = elementosConCodigos.tiposOficinasComarcales(),
                                enabled = !modoLectura,
                                onExpandedChange = { viewModel.toggleOficinaComarcalExpandida() },
                                onDismissRequest = { viewModel.cerrarOficinaComarcalMenu() },
                                onSeleccionar = { codigo, nombre -> viewModel.seleccionarOficinaComarcal(codigo, nombre) },
                                defectColor = true
                            )
                        }

                        if (codigoDestino == "02" || codigoDestino == "03") {
                            if (codigoDestino == "02") {
                                Text(stringResource(R.string.mesagge_send_dades), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.15.sp)
                            }
                            val sufijo = if (codigoDestino == "03") " *" else ""
                            CampoTexto(label = stringResource(R.string.form_address) + sufijo, valor = direccion, placeholder = stringResource(R.string.form_address_description), onValueChange = { viewModel.actualizarDireccion(it) }, defectColor = true, enabled = !modoLectura)
                            CampoTexto(label = stringResource(R.string.form_poblacion) + sufijo, valor = poblacion, placeholder = stringResource(R.string.form_poblacion_description), onValueChange = { viewModel.actualizarPoblacion(it) }, defectColor = true, enabled = !modoLectura)
                            CampoTexto(label = stringResource(R.string.form_postal_code) + sufijo, valor = codigoPostal, placeholder = stringResource(R.string.form_postal_code_description), keyboardType = KeyboardType.Number, onValueChange = { viewModel.actualizarCodigoPostal(it) }, defectColor = true, enabled = !modoLectura)
                            CampoTexto(label = stringResource(R.string.form_municipality) + sufijo, valor = municipio, placeholder = stringResource(R.string.form_municipality_description), onValueChange = { viewModel.actualizarMunicipio(it) }, defectColor = true, enabled = !modoLectura)
                            CampoTexto(label = stringResource(R.string.form_contact_phone) + sufijo, valor = telefonoContacto, placeholder = stringResource(R.string.form_contact_phone_description), keyboardType = KeyboardType.Phone, onValueChange = { viewModel.actualizarTelefonoContacto(it) }, defectColor = true, enabled = !modoLectura)
                        }

                        DropdownField(
                            label = stringResource(R.string.form_material_type) + " *",
                            selectedValue = tipoMaterial,
                            expanded = tipoMaterialExpandido,
                            placeholder = stringResource(R.string.form_material_type_description),
                            opciones = elementosConCodigos.tiposMaterial(),
                            enabled = !modoLectura,
                            onExpandedChange = { viewModel.toggleTipoMaterialExpandido() },
                            onDismissRequest = { viewModel.cerrarTipoMaterialMenu() },
                            onSeleccionar = { codigo, nombre -> viewModel.seleccionarTipoMaterial(nombre, codigo) },
                            defectColor = true
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
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.title_identifiers), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (!modoLectura) {
                                TextButton(onClick = { viewModel.agregarUnidades() }, colors = ButtonDefaults.textButtonColors(contentColor = MainGreen)) {
                                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.action_add), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        if (codiMoObligatorio) {
                            Text(stringResource(R.string.alert_material_codiMo), fontSize = 13.sp, color = ErrorRed, fontWeight = FontWeight.Medium)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                        listaUnidades.forEachIndexed { indice, item ->
                            UnidadesItem(
                                indice = indice,
                                codiMo = item.codiExplotacio,
                                unidades = item.nombreUnitats,
                                mostrarEliminar = !modoLectura && listaUnidades.size > 1,
                                codiMoObligatorio = codiMoObligatorio,
                                modoLectura = modoLectura,
                                oncodiMoChange = { viewModel.actualizarCodiExplotacio(indice, it) },
                                onEliminar = { viewModel.eliminarUnidades(indice) },
                                onUnidadesChange = { viewModel.actualizarUnidades(indice, it) }
                            )
                            if (indice < listaUnidades.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }

                if (!modoLectura) {
                    Button(
                        onClick = { viewModel.solicitarMaterial() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp).height(56.dp),
                        enabled = !estadoCarga,
                        colors = ButtonDefaults.buttonColors(containerColor = MainGreen, disabledContainerColor = MaterialTheme.colorScheme.outline),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
                    ) {
                        Text(stringResource(R.string.buttom_form_request_material), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!modoLectura) viewModel.guardarBorradorAutomatico()
        }
    }
}

@Composable
private fun UnidadesItem(
    indice: Int,
    codiMo: String?,
    unidades: String,
    mostrarEliminar: Boolean,
    codiMoObligatorio: Boolean,
    modoLectura: Boolean = false,
    oncodiMoChange: (String) -> Unit,
    onUnidadesChange: (String) -> Unit,
    onEliminar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.form_unitats) + " ${indice + 1}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (mostrarEliminar) {
                IconButton(onClick = onEliminar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar unidad", tint = ErrorRed, modifier = Modifier.size(20.dp))
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.label_codimo) + if (codiMoObligatorio) " *" else "",
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = codiMo ?: "",
                onValueChange = { oncodiMoChange(it) },
                enabled = !modoLectura,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.form_codiMo_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (codiMoObligatorio && codiMo.isNullOrEmpty()) ErrorRed else MainGreen,
                    unfocusedBorderColor = if (codiMoObligatorio && codiMo.isNullOrEmpty()) ErrorRed.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MainGreen,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledBorderColor = if (codiMoObligatorio && codiMo.isNullOrEmpty()) ErrorRed.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, autoCorrect = false)
            )
            if (codiMoObligatorio && codiMo.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.alert_necessary_codiMO), fontSize = 12.sp, color = ErrorRed)
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.form_unitats) + " *", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = unidades,
                onValueChange = { onUnidadesChange(it) },
                enabled = !modoLectura,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.form_unitats_description), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MainGreen,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}