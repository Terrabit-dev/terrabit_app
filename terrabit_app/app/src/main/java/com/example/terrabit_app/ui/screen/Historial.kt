package com.example.terrabit_app.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.data.local.database.Historial
import com.example.terrabit_app.ui.theme.Blue
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.viewmodel.HistorialViewModel
import kotlinx.coroutines.launch

@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel = hiltViewModel(),
    onMenuClick: () -> Unit,
    navController: NavController
) {
    val historial by viewModel.historial.observeAsState(emptyList())
    val historialFiltrado by viewModel.historialFiltrado.observeAsState(emptyList())
    val textoBusqueda by viewModel.textoBusqueda.observeAsState("")
    var mostrarDialogoLimpiar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.cargarHistorial()
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item {
                HeaderHistorial(
                    totalRegistros = historial.size,
                    onMenuClick = onMenuClick,
                    onLimpiarHistorial = { mostrarDialogoLimpiar = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                BarraBusquedaHistorial(
                    texto = textoBusqueda,
                    onTextoChange = { viewModel.actualizarBusqueda(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (historialFiltrado.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp).padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay registros en el historial",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(historialFiltrado, key = { it.id }) { registro ->
                    TarjetaHistorial(
                        historial = registro,
                        onEliminarClick = { viewModel.eliminarRegistro(registro.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    if (mostrarDialogoLimpiar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLimpiar = false },
            title = { Text("Limpiar historial", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Se eliminarán todos los registros del historial. Esta acción no se puede deshacer.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.limpiarHistorial()
                        mostrarDialogoLimpiar = false
                    }
                }) {
                    Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLimpiar = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun HeaderHistorial(
    totalRegistros: Int,
    onMenuClick: () -> Unit,
    onLimpiarHistorial: () -> Unit
) {
    var expandedOpciones by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(color = MainGreen)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                }

                Box {
                    IconButton(
                        onClick = { expandedOpciones = true },
                        modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expandedOpciones,
                        onDismissRequest = { expandedOpciones = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp), tint = ErrorRed)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Limpiar historial", color = ErrorRed)
                                }
                            },
                            onClick = { expandedOpciones = false; onLimpiarHistorial() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Historial", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$totalRegistros registros", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BarraBusquedaHistorial(texto: String, onTextoChange: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        placeholder = { Text("Buscar en historial...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onTextoChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun TarjetaHistorial(
    historial: Historial,
    onEliminarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarMenu by remember { mutableStateOf(false) }

    val cardColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "cardColor"
    )

    @Composable
    fun obtenerNombreTipo(tipo: String): String = when (tipo) {
        "MUERTE" -> "Muerte"
        "MATERIAL" -> "Material"
        "NACIMIENTO" -> "Nacimiento"
        "CORRECCION_SEXO" -> "Corrección Sexo"
        "IDENTIFICACION_APLAZADA" -> "ID Aplazada"
        "MATERIAL_DUPLICADO" -> "Material Duplicado"
        "MOVIMIENTO" -> "Movimiento"
        "GUIA" -> "Guía"
        else -> tipo
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono tipo
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (historial.tipo) {
                    "MUERTE" -> ErrorRed.copy(alpha = 0.15f)
                    "MOVIMIENTO" -> MainOrange.copy(alpha = 0.15f)
                    "GUIA" -> MainOrange.copy(alpha = 0.15f)
                    "CORRECCION_SEXO" -> Blue.copy(alpha = 0.15f)
                    else -> MainGreen.copy(alpha = 0.15f)
                },
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = when (historial.tipo) {
                            "MUERTE" -> Icons.Default.Clear
                            "MATERIAL" -> Icons.Default.ShoppingCart
                            "NACIMIENTO" -> Icons.Default.Add
                            "CORRECCION_SEXO" -> Icons.Default.Edit
                            "IDENTIFICACION_APLAZADA" -> Icons.Default.Badge
                            "MATERIAL_DUPLICADO" -> Icons.Default.ContentCopy
                            "MOVIMIENTO" -> Icons.Default.SwapHoriz
                            "GUIA" -> Icons.Default.Receipt
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = when (historial.tipo) {
                            "MUERTE" -> ErrorRed
                            "MOVIMIENTO" -> MainOrange
                            "GUIA" -> MainOrange
                            "CORRECCION_SEXO" -> Blue
                            else -> MainGreen
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        obtenerNombreTipo(historial.tipo),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MainGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MainGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Enviado",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MainGreen
                            )
                        }
                    }
                }

                if (historial.resumen.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        historial.resumen,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(historial.fecha, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(historial.hora, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Box {
                IconButton(onClick = { mostrarMenu = true }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                }
                DropdownMenu(
                    expanded = mostrarMenu,
                    onDismissRequest = { mostrarMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp), tint = ErrorRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Eliminar", color = ErrorRed)
                            }
                        },
                        onClick = { mostrarMenu = false; onEliminarClick() }
                    )
                }
            }
        }
    }
}