package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.utils.ElementosConCodigos
import com.example.terrabit_app.viewmodel.MaterialDuplicadoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDupplicadosScreen(navController: NavController){

    val viewModel = viewModel<MaterialDuplicadoViewModel>()
    val empresaSubministradora by viewModel.empresaSubministradora.observeAsState("")
    val tipoEnviamiento by viewModel.tipoEnviamiento.observeAsState("")
    val tipoDireccionEnvio by viewModel.direccionEnvio.observeAsState("")
    val oficinaComarcal by viewModel.oficinaComarcal.observeAsState("")
    val direccionEnvio by viewModel.dirrecionEnvio.observeAsState("")
    val poblacion by viewModel.poblacion.observeAsState("")
    val municipio by viewModel.municipio.observeAsState("")
    val codigoPostal by viewModel.codigoPostal.observeAsState("")
    val telefono by viewModel.telefonoContacto.observeAsState("")

    val empresaSubministradoraExpandido by viewModel.empresaExpandida.observeAsState(false)
    val tipoEviamientoExpandido by viewModel.tipoEnviamientoExpandido.observeAsState(false)
    val direccionEnvioExpandido by viewModel.direccionEnvioExpandido.observeAsState(false)
    val oficinaComarcalExpandido by viewModel.oficinaComarcalExpandido.observeAsState(false)

    val elementosConCodigos = ElementosConCodigos()



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
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.MaterialCategoria.route) }) {
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
        containerColor = WhiteBackground
    ){padding ->
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
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ){
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
                            expanded = empresaSubministradoraExpandido,
                            onExpandedChange = { viewModel.toggleEmpresaExpandida() }
                        ) {
                            OutlinedTextField(
                                value = empresaSubministradora,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar empresa", color = BlueGrey) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(empresaSubministradoraExpandido) },
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
                                expanded = empresaSubministradoraExpandido,
                                onDismissRequest = { viewModel.cerrarEmpresaMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                elementosConCodigos.tiposOficinasComarcales().forEach { (codigo, empresa) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                empresa,
                                                fontSize = 15.sp,
                                                color = DarkBlueGrey
                                            )
                                        },
                                        onClick = {  viewModel.seleccionarEmpresa(codigo, empresa)},
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )

                                }
                            }
                        }
                    }
                    // Tipos de envio
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Tiipos de envio *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = tipoEviamientoExpandido,
                            onExpandedChange = { viewModel.toggleTipoEnviamientoExpandido() }
                        ) {
                            OutlinedTextField(
                                value = tipoEnviamiento,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar empresa", color = BlueGrey) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoEviamientoExpandido) },
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
                                expanded = tipoEviamientoExpandido,
                                onDismissRequest = { viewModel.cerrarTipoEnviamientoMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                elementosConCodigos.tiposEnvios().forEach { (codigo, nombre) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                nombre,
                                                fontSize = 15.sp,
                                                color = DarkBlueGrey
                                            )
                                        },
                                        onClick = {  viewModel.seleccionarTipoEnviamiento(codigo, nombre)},
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )

                                }
                            }
                        }
                    }
                    // Direccion de envio
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Dirección de envio *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = direccionEnvioExpandido,
                            onExpandedChange = { viewModel.toggleDireccionEnvioExpandido() }
                        ) {
                            OutlinedTextField(
                                value = tipoDireccionEnvio,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                readOnly = true,
                                placeholder = { Text("Seleccionar dirección de envio", color = BlueGrey) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(direccionEnvioExpandido) },
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
                                expanded = direccionEnvioExpandido,
                                onDismissRequest = { viewModel.cerrarDireccionEnvioMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                elementosConCodigos.tiposDireccionEnvio().forEach { (codigo, nombre) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                nombre,
                                                fontSize = 15.sp,
                                                color = DarkBlueGrey
                                            )
                                        },
                                        onClick = {  viewModel.seleccionarDireccionEnvio(codigo, nombre)},
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                    )

                                }
                            }
                        }
                    }
                    if (tipoDireccionEnvio == "OC"){
                        // Tipos de envio
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Codigo comarcal *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBlueGrey,
                                letterSpacing = 0.15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ExposedDropdownMenuBox(
                                expanded = oficinaComarcalExpandido,
                                onExpandedChange = { viewModel.toggleOficinaComarcalExpandido() }
                            ) {
                                OutlinedTextField(
                                    value = oficinaComarcal,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    readOnly = true,
                                    placeholder = { Text("Seleccionar la oficina comarcal", color = BlueGrey) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(oficinaComarcalExpandido) },
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
                                    expanded = oficinaComarcalExpandido,
                                    onDismissRequest = { viewModel.cerrarOficinaComarcalMenu() },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    elementosConCodigos.tiposEnvios().forEach { (codigo, nombre) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    nombre,
                                                    fontSize = 15.sp,
                                                    color = DarkBlueGrey
                                                )
                                            },
                                            onClick = {  viewModel.seleccionarOficinaComarcal(codigo, nombre)},
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                                        )

                                    }
                                }
                            }
                        }
                    }

                    //datos de envios

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
                            value = direccionEnvio,
                            onValueChange = { viewModel.actualizarDireccionEnvio(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("direccion", color = BlueGrey) },
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

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Poablacion *",
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
                            placeholder = { Text("poblacion", color = BlueGrey) },
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
                            placeholder = { Text("municipio", color = BlueGrey) },
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Codigo postal *",
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
                            placeholder = { Text("Codigo psotal", color = BlueGrey) },
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Telefono contacto *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBlueGrey,
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { viewModel.actualizarTelefonoContacto(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("telefeono contacto", color = BlueGrey) },
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

                }

            }

        }
    }
}

