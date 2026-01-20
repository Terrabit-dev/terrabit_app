package com.example.terrabit_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.viewmodel.MainViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material(navController: NavController, viewModel: MainViewmodel) {
    // Observar variables del ViewModel
    val empresaSubministradora by viewModel.empresaSubministradora.observeAsState("")
    val tipoEnviamiento by viewModel.tipoEnviamiento.observeAsState("")
    val destinoLliurament by viewModel.destinoLliurament.observeAsState("")
    val oficinaComarcal by viewModel.oficinaComarcal.observeAsState("")
    val direccion by viewModel.direccion.observeAsState("")
    val poblacion by viewModel.poblacion.observeAsState("")
    val codigoPostal by viewModel.codigoPostal.observeAsState("")
    val municipio by viewModel.municipio.observeAsState("")
    val telefonoContacto by viewModel.telefonoContacto.observeAsState("")
    val identificadorMaterial by viewModel.identificadorMaterial.observeAsState("")
    val tipoMaterial by viewModel.tipoMaterial.observeAsState("")

    val empresaExpandida by viewModel.empresaExpandida.observeAsState(false)
    val tipoEnviamientoExpandido by viewModel.tipoEnviamientoExpandido.observeAsState(false)
    val destinoExpandido by viewModel.destinoExpandido.observeAsState(false)
    val oficinaComarcalExpandida by viewModel.oficinaComarcalExpandida.observeAsState(false)
    val tipoMaterialExpandido by viewModel.tipoMaterialExpandido.observeAsState(false)

    val registroExitoso by viewModel.registroMaterialExitoso.observeAsState(false)
    val mensajeError by viewModel.mensajeErrorMaterial.observeAsState("")

    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar
    LaunchedEffect(registroExitoso, mensajeError) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = "Material solicitado exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistroMaterial()
        } else if (mensajeError.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = mensajeError,
                duration = SnackbarDuration.Long
            )
            viewModel.resetearEstadoRegistroMaterial()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Solicitar Material",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Sección 5.14",
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
                    containerColor = Color(0xFF3F8F6B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (data.visuals.message.contains("exitosamente")) {
                        Color(0xFF3F8F6B)
                    } else {
                        Color(0xFFD32F2F)
                    },
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Color(0xFFF5F7FA)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Empresa Subministradora
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Empresa Subministradora *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = empresaExpandida,
                            onExpandedChange = { viewModel.toggleEmpresaExpandida() }
                        ) {
                            OutlinedTextField(
                                value = empresaSubministradora,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar empresa", color = Color(0xFF94A3B8)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(empresaExpandida) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF3F8F6B),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = empresaExpandida,
                                onDismissRequest = { viewModel.cerrarEmpresaMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.listaEmpresas.forEach { empresa ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                empresa.nombre,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B)
                                            )
                                        },
                                        onClick = { viewModel.seleccionarEmpresa(empresa.nombre, empresa.nif) },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )
                                    if (empresa != viewModel.listaEmpresas.last()) {
                                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }

                    // Tipo de Envío
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Tipo de Envío *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = tipoEnviamientoExpandido,
                            onExpandedChange = { viewModel.toggleTipoEnviamientoExpandido() }
                        ) {
                            OutlinedTextField(
                                value = tipoEnviamiento,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar tipo de envío", color = Color(0xFF94A3B8)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoEnviamientoExpandido) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF3F8F6B),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = tipoEnviamientoExpandido,
                                onDismissRequest = { viewModel.cerrarTipoEnviamientoMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.listaTiposEnviamiento.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo, fontSize = 15.sp, color = Color(0xFF1E293B)) },
                                        onClick = { viewModel.seleccionarTipoEnviamiento(tipo) },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )
                                    if (tipo != viewModel.listaTiposEnviamiento.last()) {
                                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }

                    // Destino de Entrega
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Destino de Entrega *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = destinoExpandido,
                            onExpandedChange = { viewModel.toggleDestinoExpandido() }
                        ) {
                            OutlinedTextField(
                                value = destinoLliurament,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar destino", color = Color(0xFF94A3B8)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(destinoExpandido) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF3F8F6B),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = destinoExpandido,
                                onDismissRequest = { viewModel.cerrarDestinoMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.listaDestinos.forEach { destino ->
                                    DropdownMenuItem(
                                        text = { Text(destino, fontSize = 15.sp, color = Color(0xFF1E293B)) },
                                        onClick = { viewModel.seleccionarDestino(destino) },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )
                                    if (destino != viewModel.listaDestinos.last()) {
                                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }

                    // Oficina Comarcal (solo si destino == "01 - OC")
                    if (destinoLliurament.startsWith("01")) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Oficina Comarcal *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = oficinaComarcalExpandida,
                                onExpandedChange = { viewModel.toggleOficinaComarcalExpandida() }
                            ) {
                                OutlinedTextField(
                                    value = oficinaComarcal,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    readOnly = true,
                                    placeholder = { Text("Seleccionar oficina comarcal", color = Color(0xFF94A3B8)) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(oficinaComarcalExpandida) },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF3F8F6B),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = oficinaComarcalExpandida,
                                    onDismissRequest = { viewModel.cerrarOficinaComarcalMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaOficinasComarcales.forEach { oc ->
                                        DropdownMenuItem(
                                            text = { Text("${oc.codigo} - ${oc.nombre}", fontSize = 15.sp, color = Color(0xFF1E293B)) },
                                            onClick = { viewModel.seleccionarOficinaComarcal(oc.nombre, oc.codigo) },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                        if (oc != viewModel.listaOficinasComarcales.last()) {
                                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Campos de dirección alternativa (solo si destino == "03")
                    if (destinoLliurament.startsWith("03")) {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Dirección *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = direccion,
                                    onValueChange = { viewModel.actualizarDireccion(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Introducir dirección", color = Color(0xFF94A3B8)) },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF3F8F6B),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        cursorColor = Color(0xFF3F8F6B)
                                    )
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Población *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = poblacion,
                                    onValueChange = { viewModel.actualizarPoblacion(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Introducir población", color = Color(0xFF94A3B8)) },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF3F8F6B),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        cursorColor = Color(0xFF3F8F6B)
                                    )
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Código Postal *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = codigoPostal,
                                    onValueChange = { viewModel.actualizarCodigoPostal(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Introducir código postal", color = Color(0xFF94A3B8)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF3F8F6B),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        cursorColor = Color(0xFF3F8F6B)
                                    )
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Municipio *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = municipio,
                                    onValueChange = { viewModel.actualizarMunicipio(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Introducir municipio", color = Color(0xFF94A3B8)) },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF3F8F6B),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        cursorColor = Color(0xFF3F8F6B)
                                    )
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Teléfono de Contacto *",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    letterSpacing = 0.15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = telefonoContacto,
                                    onValueChange = { viewModel.actualizarTelefonoContacto(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Introducir teléfono", color = Color(0xFF94A3B8)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF3F8F6B),
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedTextColor = Color(0xFF1E293B),
                                        unfocusedTextColor = Color(0xFF1E293B),
                                        cursorColor = Color(0xFF3F8F6B)
                                    )
                                )
                            }
                        }
                    }

                    // Identificador
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Identificador *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = identificadorMaterial,
                            onValueChange = { viewModel.actualizarIdentificadorMaterial(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Introducir o escanear identificador", color = Color(0xFF94A3B8)) },
                            trailingIcon = {
                                IconButton(onClick = { /* Acción de cámara */ }) {
                                    Icon(
                                        Icons.Outlined.CameraAlt,
                                        contentDescription = "Escanear",
                                        tint = Color(0xFF3F8F6B)
                                    )
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3F8F6B),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                cursorColor = Color(0xFF3F8F6B)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                autoCorrect = false
                            )
                        )
                    }

                    // Tipo de Material
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Tipo de Material *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = tipoMaterialExpandido,
                            onExpandedChange = { viewModel.toggleTipoMaterialExpandido() }
                        ) {
                            OutlinedTextField(
                                value = tipoMaterial,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar tipo de material", color = Color(0xFF94A3B8)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoMaterialExpandido) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF3F8F6B),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = tipoMaterialExpandido,
                                onDismissRequest = { viewModel.cerrarTipoMaterialMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.listaTiposMaterial.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text("${tipo.codigo} - ${tipo.nombre}", fontSize = 15.sp, color = Color(0xFF1E293B)) },
                                        onClick = { viewModel.seleccionarTipoMaterial(tipo.nombre, tipo.codigo) },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )
                                    if (tipo != viewModel.listaTiposMaterial.last()) {
                                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón Solicitar
            Button(
                onClick = { viewModel.solicitarMaterial() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F8F6B)),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Text(
                    "Solicitar Material",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}