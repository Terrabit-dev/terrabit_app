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
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.viewmodel.MaterialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material(navController: NavController, viewModel: MaterialViewModel) {
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
    val estadoCarga by viewModel.cargandoMaterial.observeAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    // Mostrar Snackbar de éxito
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(
                message = "Material solicitado exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoRegistroMaterial()
        }
    }

    // Mostrar diálogo cuando hay error
    LaunchedEffect(mensajeError) {
        if (mensajeError.isNotEmpty()) {
            mostrarDialogoError = true
        }
    }

    // Diálogo de Error
    if (mostrarDialogoError && mensajeError.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoError = false
                viewModel.resetearEstadoRegistroMaterial()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Error al Solicitar Material",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkBlueGrey
                )
            },
            text = {
                Text(
                    text = mensajeError,
                    fontSize = 16.sp,
                    color = BlueGrey,
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoError = false
                        viewModel.resetearEstadoRegistroMaterial()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Indicador de carga en pantalla completa
    if (estadoCarga) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) { }, // Bloquear interacción
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
                                color = DarkBlueGrey,
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
                                    placeholder = { Text("Seleccionar empresa", color = BlueGrey) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(empresaExpandida) },
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
                                                    color = DarkBlueGrey
                                                )
                                            },
                                            onClick = { viewModel.seleccionarEmpresa(empresa.nombre, empresa.nif) },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                        if (empresa != viewModel.listaEmpresas.last()) {
                                            HorizontalDivider(color = DarkWhiteBackground, thickness = 1.dp)
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
                                color = DarkBlueGrey,
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
                                    placeholder = { Text("Seleccionar tipo de envío", color = BlueGrey) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoEnviamientoExpandido) },
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
                                    expanded = tipoEnviamientoExpandido,
                                    onDismissRequest = { viewModel.cerrarTipoEnviamientoMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaTiposEnviamiento.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo, fontSize = 15.sp, color = DarkBlueGrey) },
                                            onClick = { viewModel.seleccionarTipoEnviamiento(tipo) },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                        if (tipo != viewModel.listaTiposEnviamiento.last()) {
                                            HorizontalDivider(color = DarkWhiteBackground, thickness = 1.dp)
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
                                color = DarkBlueGrey,
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
                                    placeholder = { Text("Seleccionar destino", color = BlueGrey) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(destinoExpandido) },
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
                                    expanded = destinoExpandido,
                                    onDismissRequest = { viewModel.cerrarDestinoMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaDestinos.forEach { destino ->
                                        DropdownMenuItem(
                                            text = { Text(destino, fontSize = 15.sp, color = DarkBlueGrey) },
                                            onClick = { viewModel.seleccionarDestino(destino) },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                        if (destino != viewModel.listaDestinos.last()) {
                                            HorizontalDivider(color = DarkWhiteBackground, thickness = 1.dp)
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
                                    color = DarkBlueGrey,
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
                                        placeholder = { Text("Seleccionar oficina comarcal", color = BlueGrey) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(oficinaComarcalExpandida) },
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
                                        expanded = oficinaComarcalExpandida,
                                        onDismissRequest = { viewModel.cerrarOficinaComarcalMenu() },
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        viewModel.listaOficinasComarcales.forEach { oc ->
                                            DropdownMenuItem(
                                                text = { Text("${oc.codigo} - ${oc.nombre}", fontSize = 15.sp, color = DarkBlueGrey) },
                                                onClick = { viewModel.seleccionarOficinaComarcal(oc.nombre, oc.codigo) },
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                            )
                                            if (oc != viewModel.listaOficinasComarcales.last()) {
                                                HorizontalDivider(color = DarkWhiteBackground, thickness = 1.dp)
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
                                        color = DarkBlueGrey,
                                        letterSpacing = 0.15.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = direccion,
                                        onValueChange = { viewModel.actualizarDireccion(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Introducir dirección", color = BlueGrey) },
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MainGreen,
                                            unfocusedBorderColor = DarkWhiteBackground,
                                            focusedTextColor = DarkBlueGrey,
                                            unfocusedTextColor = DarkBlueGrey,
                                            cursorColor = MainGreen
                                        )
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Población *",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkBlueGrey,
                                        letterSpacing = 0.15.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = poblacion,
                                        onValueChange = { viewModel.actualizarPoblacion(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Introducir población", color = BlueGrey) },
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MainGreen,
                                            unfocusedBorderColor = DarkWhiteBackground,
                                            focusedTextColor = DarkBlueGrey,
                                            unfocusedTextColor = DarkBlueGrey,
                                            cursorColor = MainGreen
                                        )
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Código Postal *",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkBlueGrey,
                                        letterSpacing = 0.15.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = codigoPostal,
                                        onValueChange = { viewModel.actualizarCodigoPostal(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Introducir código postal", color = BlueGrey) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MainGreen,
                                            unfocusedBorderColor = DarkWhiteBackground,
                                            focusedTextColor = DarkBlueGrey,
                                            unfocusedTextColor = DarkBlueGrey,
                                            cursorColor = MainGreen
                                        )
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Municipio *",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkBlueGrey,
                                        letterSpacing = 0.15.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = municipio,
                                        onValueChange = { viewModel.actualizarMunicipio(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Introducir municipio", color = BlueGrey) },
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MainGreen,
                                            unfocusedBorderColor = DarkWhiteBackground,
                                            focusedTextColor = DarkBlueGrey,
                                            unfocusedTextColor = DarkBlueGrey,
                                            cursorColor = MainGreen
                                        )
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Teléfono de Contacto *",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkBlueGrey,
                                        letterSpacing = 0.15.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = telefonoContacto,
                                        onValueChange = { viewModel.actualizarTelefonoContacto(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Introducir teléfono", color = BlueGrey) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MainGreen,
                                            unfocusedBorderColor = DarkWhiteBackground,
                                            focusedTextColor = DarkBlueGrey,
                                            unfocusedTextColor = DarkBlueGrey,
                                            cursorColor = MainGreen
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
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = identificadorMaterial,
                                onValueChange = { viewModel.actualizarIdentificadorMaterial(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Introducir o escanear identificador", color = BlueGrey) },
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
                                color = DarkBlueGrey,
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
                                    placeholder = { Text("Seleccionar tipo de material", color = BlueGrey) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoMaterialExpandido) },
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
                                    expanded = tipoMaterialExpandido,
                                    onDismissRequest = { viewModel.cerrarTipoMaterialMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    viewModel.listaTiposMaterial.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text("${tipo.codigo} - ${tipo.nombre}", fontSize = 15.sp, color = DarkBlueGrey) },
                                            onClick = { viewModel.seleccionarTipoMaterial(tipo.nombre, tipo.codigo) },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                        )
                                        if (tipo != viewModel.listaTiposMaterial.last()) {
                                            HorizontalDivider(color = DarkWhiteBackground, thickness = 1.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Botón Solicitar - Deshabilitado mientras carga
                Button(
                    onClick = { viewModel.solicitarMaterial() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .height(56.dp),
                    enabled = !estadoCarga, // Deshabilitar mientras carga
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
}
