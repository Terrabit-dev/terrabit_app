package com.example.terrabit_app.ui.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.viewmodel.MainViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorregirSexoBovi(navController: NavController, viewModel: MainViewmodel) {
    // Observar variables del ViewModel
    val identificadorCorreccionSexo by viewModel.identificadorCorreccionSexo.observeAsState("")
    val sexoCorreccionSeleccionado by viewModel.sexoCorreccionSeleccionado.observeAsState("")
    val sexoCorreccionExpandido by viewModel.sexoCorreccionExpandido.observeAsState(false)

    // Observar estado de registro para mostrar mensajes
    val correccionSexoExitosa by viewModel.correccionSexoExitosa.observeAsState(false)
    val mensajeErrorCorreccionSexo by viewModel.mensajeErrorCorreccionSexo.observeAsState("")

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar cuando hay mensaje de éxito o error
    LaunchedEffect(correccionSexoExitosa, mensajeErrorCorreccionSexo) {
        if (correccionSexoExitosa) {
            snackbarHostState.showSnackbar(
                message = "Sexo corregido exitosamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearEstadoCorreccionSexo()
        } else if (mensajeErrorCorreccionSexo.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = mensajeErrorCorreccionSexo,
                duration = SnackbarDuration.Long
            )
            viewModel.resetearEstadoCorreccionSexo()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Corregir Sexo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Modificación de Animal",
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
                    containerColor = Color(0xFF4A7C59),
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
                        Color(0xFF4A7C59) // Verde para éxito
                    } else {
                        Color(0xFFD32F2F) // Rojo para error
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
                            "Identificador del Animal *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = identificadorCorreccionSexo,
                            onValueChange = { viewModel.actualizarIdentificadorCorreccionSexo(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Introducir o escanear identificador",
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { /* Acción de cámara */ }) {
                                    Icon(
                                        Icons.Outlined.CameraAlt,
                                        contentDescription = "Escanear",
                                        tint = Color(0xFF4A7C59)
                                    )
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4A7C59),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                cursorColor = Color(0xFF4A7C59)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            )
                        )
                    }

                    // Sexo Correcto
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Sexo Correcto *",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            letterSpacing = 0.15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ExposedDropdownMenuBox(
                            expanded = sexoCorreccionExpandido,
                            onExpandedChange = { viewModel.toggleSexoCorreccionExpandido() }
                        ) {
                            OutlinedTextField(
                                value = sexoCorreccionSeleccionado,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        "Seleccionar sexo",
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = sexoCorreccionExpandido
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4A7C59),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = sexoCorreccionExpandido,
                                onDismissRequest = { viewModel.cerrarSexoCorreccionMenu() },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.listaSexos.forEach { sexo ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                sexo,
                                                fontSize = 15.sp,
                                                color = Color(0xFF1E293B),
                                                fontWeight = FontWeight.Normal
                                            )
                                        },
                                        onClick = { viewModel.seleccionarSexoCorreccion(sexo) },
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 14.dp
                                        ),
                                        colors = MenuDefaults.itemColors(
                                            textColor = Color(0xFF1E293B),
                                            leadingIconColor = Color(0xFF1E293B),
                                            trailingIconColor = Color(0xFF1E293B),
                                            disabledTextColor = Color(0xFF94A3B8)
                                        )
                                    )
                                    if (sexo != viewModel.listaSexos.last()) {
                                        HorizontalDivider(
                                            color = Color(0xFFF1F5F9),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón Corregir Sexo
            Button(
                onClick = { viewModel.corregirSexoAnimal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A7C59)
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Text(
                    "Corregir Sexo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}