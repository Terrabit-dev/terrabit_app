package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.ui.screen.bovinos.cambiarIdioma
import com.example.terrabit_app.ui.theme.Blue
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.viewmodel.BorradorViewModel
import kotlinx.coroutines.launch

@Composable
fun BorradoresScreen(
    viewModel: BorradorViewModel,
    onMenuClick: () -> Unit
) {
    val borradores by viewModel.borradores.observeAsState(emptyList())
    val borradoresFiltered by viewModel.borradoresFiltrados.observeAsState(emptyList())
    val textoBusqueda by viewModel.textoBusqueda.observeAsState("")

    var mostrarDialogoEliminarTodos by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.cargarBorradores()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        HeaderBorradores(
            totalBorradores = borradores.size,
            onMenuClick = onMenuClick,
            onEliminarTodos = { mostrarDialogoEliminarTodos = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        BarraBusqueda(
            texto = textoBusqueda,
            onTextoChange = { viewModel.actualizarBusqueda(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (borradoresFiltered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = BlueGrey
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay Resultados :O",
                        fontSize = 16.sp,
                        color = BlueGrey
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(borradoresFiltered) { borrador ->
                    TarjetaBorrador(
                        borrador = borrador,
                        onEliminarClick = {
                            viewModel.eliminarBorrador(borrador.id)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    DialogoConfirmacion(
        mostrar = mostrarDialogoEliminarTodos,
        titulo = "Eliminar todos los borradores",
        mensaje = "¿Estás seguro de que deseas eliminar todos los borradores? Esta acción no se puede deshacer.",
        onConfirmar = {
            scope.launch {
                viewModel.eliminarTodosBorradores()
                mostrarDialogoEliminarTodos = false
            }

        },
        onCancelar = { mostrarDialogoEliminarTodos = false }
    )
}

@Composable
fun HeaderBorradores(
    totalBorradores: Int,
    onMenuClick: () -> Unit,
    onEliminarTodos: () -> Unit
) {
    var expandedConfig by remember { mutableStateOf(false) }
    var expandedOpciones by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(color = MainGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { expandedConfig = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = expandedConfig,
                        onDismissRequest = { expandedConfig = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Castellano") },
                            onClick = {
                                expandedConfig = false
                                cambiarIdioma("es")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Català") },
                            onClick = {
                                expandedConfig = false
                                cambiarIdioma("ca")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Borradores",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "$totalBorradores guardados",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { expandedOpciones = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedOpciones,
                        onDismissRequest = { expandedOpciones = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = ErrorRed
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Eliminar todos", color = ErrorRed)
                                }
                            },
                            onClick = {
                                expandedOpciones = false
                                onEliminarTodos()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoConfirmacion(
    mostrar: Boolean,
    titulo: String,
    mensaje: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    if (mostrar) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onCancelar,
            title = {
                Text(
                    titulo,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlueGrey
                )
            },
            text = {
                Text(
                    mensaje,
                    color = BlueGrey
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = onConfirmar
                ) {
                    Text("Confirmar", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = onCancelar
                ) {
                    Text("Cancelar", color = BlueGrey)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ChipFiltro(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (seleccionado) Color.White else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (seleccionado) 2.dp else 0.dp
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
            color = if (seleccionado) DarkBlueGrey else BlueGrey
        )
    }
}

@Composable
fun BarraBusqueda(
    texto: String,
    onTextoChange: (String) -> Unit
) {
    androidx.compose.material3.OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        placeholder = {
            Text(
                "Buscar borradores...",
                color = BlueGrey
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = BlueGrey
            )
        },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onTextoChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = BlueGrey
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainGreen,
            unfocusedBorderColor = BlueGrey.copy(alpha = 0.3f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun TarjetaBorrador(
    borrador: Borrador,
    onEliminarClick: () -> Unit
) {
    var mostrarMenu by remember { mutableStateOf(false) }

    fun obtenerNombreTipo(tipo: String): String {
        return when (tipo) {
            "MUERTE" -> "Muerte"
            "MATERIAL" -> "Material"
            "NACIMIENTO" -> "Nacimiento"
            "CORRECCION_SEXO" -> "Corrección Sexo"
            "IDENTIFICACION_APLAZADA" -> "ID Aplazada"
            else -> tipo
        }
    }

    fun obtenerEstadoLegible(estado: String): String {
        return when (estado) {
            "BORRADOR_AUTO" -> "Guardado"
            "PENDIENTE" -> "Pendiente"
            "ENVIANDO" -> "Enviando"
            "ERROR" -> "Error"
            else -> estado
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (borrador.tipo) {
                    "MUERTE" -> ErrorRed.copy(alpha = 0.15f)
                    "MATERIAL" -> MainGreen.copy(alpha = 0.15f)
                    "NACIMIENTO" -> MainGreen.copy(alpha = 0.15f)
                    "CORRECCION_SEXO" -> Blue.copy(alpha = 0.15f)
                    "IDENTIFICACION_APLAZADA" -> MainGreen.copy(alpha = 0.15f)
                    else -> BlueGrey.copy(alpha = 0.15f)
                },
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = when (borrador.tipo) {
                            "MUERTE" -> Icons.Default.Clear
                            "MATERIAL" -> Icons.Default.ShoppingCart
                            "NACIMIENTO" -> Icons.Default.Add
                            "CORRECCION_SEXO" -> Icons.Default.Edit
                            "IDENTIFICACION_APLAZADA" -> Icons.Default.Badge
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        tint = when (borrador.tipo) {
                            "MUERTE" -> ErrorRed
                            "MATERIAL" -> MainGreen
                            "NACIMIENTO" -> MainGreen
                            "CORRECCION_SEXO" -> Blue
                            "IDENTIFICACION_APLAZADA" -> MainGreen
                            else -> BlueGrey
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        obtenerNombreTipo(borrador.tipo),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkBlueGrey,
                        letterSpacing = 0.2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = when (borrador.estado) {
                            "ENVIANDO" -> MainOrange.copy(alpha = 0.15f)
                            "ERROR" -> ErrorRed.copy(alpha = 0.15f)
                            "BORRADOR_AUTO" -> MainGreen.copy(alpha = 0.15f)
                            "PENDIENTE" -> BlueGrey.copy(alpha = 0.15f)
                            else -> BlueGrey.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            obtenerEstadoLegible(borrador.estado),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            color = when (borrador.estado) {
                                "ENVIANDO" -> MainOrange
                                "ERROR" -> ErrorRed
                                "BORRADOR_AUTO" -> MainGreen
                                "PENDIENTE" -> BlueGrey
                                else -> BlueGrey
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = BlueGrey
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        borrador.fecha,
                        fontSize = 14.sp,
                        color = BlueGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = BlueGrey
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        borrador.hora ?: "Sin hora",
                        fontSize = 14.sp,
                        color = BlueGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { mostrarMenu = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = BlueGrey,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = mostrarMenu,
                    onDismissRequest = { mostrarMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = ErrorRed
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Eliminar", color = ErrorRed)
                            }
                        },
                        onClick = {
                            mostrarMenu = false
                            onEliminarClick()
                        }
                    )
                }
            }
        }
    }
}