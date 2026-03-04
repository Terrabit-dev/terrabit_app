package com.example.terrabit_app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.bovinos.cambiarIdioma
import com.example.terrabit_app.ui.theme.Blue
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.viewmodel.BorradorViewModel
import kotlinx.coroutines.launch

@Composable
fun BorradoresScreen(
    viewModel: BorradorViewModel,
    onMenuClick: () -> Unit,
    navController: NavController
) {
    val borradores by viewModel.borradores.observeAsState(emptyList())
    val borradoresFiltered by viewModel.borradoresFiltrados.observeAsState(emptyList())
    val textoBusqueda by viewModel.textoBusqueda.observeAsState("")

    var mostrarDialogoEliminarTodos by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.inicializarSharedPreferences(context)
        viewModel.cargarBorradores()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                HeaderBorradores(
                    totalBorradores = borradores.size,
                    onMenuClick = onMenuClick,
                    onEliminarTodos = { mostrarDialogoEliminarTodos = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                BarraBusqueda(texto = textoBusqueda, onTextoChange = { viewModel.actualizarBusqueda(it) })
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (borradoresFiltered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp).padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.no_results_description_draft), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(borradoresFiltered) { borrador ->
                    TarjetaBorrador(
                        borrador = borrador,
                        onEliminarClick = { viewModel.eliminarBorrador(borrador.id) },
                        onEditarClick = {
                            navController.navigate(
                                when (borrador.tipo) {
                                    "CORRECCION_SEXO" -> Routes.CorregirBovino.conBorrador(borrador.id)
                                    "MUERTE" -> Routes.Fallecimiento.conBorrador(borrador.id)
                                    "NACIMIENTO" -> Routes.Nacimiento.conBorrador(borrador.id)
                                    "IDENTIFICACION_APLAZADA" -> Routes.IdentificacionAplazada.conBorrador(borrador.id)
                                    "GUIA" -> Routes.GestionGuias.conBorrador(borrador.id)
                                    "MOVIMIENTO" -> Routes.Movimientos.conBorrador(borrador.id)
                                    else -> return@TarjetaBorrador
                                }
                            )
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    DialogoConfirmacion(
        mostrar = mostrarDialogoEliminarTodos,
        titulo = stringResource(R.string.title_delete_all_option),
        mensaje = stringResource(R.string.description_delete_all_option),
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
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {
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

                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = { expandedConfig = true },
                        modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expandedConfig,
                        onDismissRequest = { expandedConfig = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Castellano", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { expandedConfig = false; cambiarIdioma("es") }
                        )
                        DropdownMenuItem(
                            text = { Text("Català", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { expandedConfig = false; cambiarIdioma("ca") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.title_draft), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)

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
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$totalBorradores ${stringResource(R.string.counter_draft)}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }

                Box {
                    IconButton(
                        onClick = { expandedOpciones = true },
                        modifier = Modifier.size(32.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.White, modifier = Modifier.size(20.dp))
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
                                    Text(stringResource(R.string.delete_all_option), color = ErrorRed)
                                }
                            },
                            onClick = { expandedOpciones = false; onEliminarTodos() }
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
        AlertDialog(
            onDismissRequest = onCancelar,
            title = { Text(titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(mensaje, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = onConfirmar) {
                    Text(stringResource(R.string.confirm_delete_option_general), color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelar) {
                    Text(stringResource(R.string.cancel_delete_option_general), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BarraBusqueda(
    texto: String,
    onTextoChange: (String) -> Unit
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        placeholder = { Text(stringResource(R.string.search_bar_text), color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
fun TarjetaBorrador(
    borrador: Borrador,
    onEliminarClick: () -> Unit,
    onEditarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarMenu by remember { mutableStateOf(false) }

    @Composable
    fun obtenerNombreTipo(tipo: String): String = when (tipo) {
        "MUERTE" -> stringResource(R.string.type_draft_title_death)
        "MATERIAL" -> stringResource(R.string.type_draft_title_material)
        "NACIMIENTO" -> stringResource(R.string.type_draft_title_born)
        "CORRECCION_SEXO" -> stringResource(R.string.type_draft_title_sex)
        "IDENTIFICACION_APLAZADA" -> stringResource(R.string.type_draft_title_id)
        else -> tipo
    }

    @Composable
    fun obtenerEstadoLegible(estado: String): String = when (estado) {
        "BORRADOR_AUTO" -> stringResource(R.string.status_draft_saved)
        "PENDIENTE" -> stringResource(R.string.status_draft_pending)
        "ENVIANDO" -> stringResource(R.string.status_draft_sending)
        "ERROR" -> stringResource(R.string.status_draft_error)
        else -> estado
    }

    Card(
        modifier = modifier.fillMaxWidth().height(96.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                },
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        obtenerNombreTipo(borrador.tipo),
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.2.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = when (borrador.estado) {
                            "ENVIANDO" -> MainOrange.copy(alpha = 0.15f)
                            "ERROR" -> ErrorRed.copy(alpha = 0.15f)
                            "BORRADOR_AUTO" -> MainGreen.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            obtenerEstadoLegible(borrador.estado),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1,
                            color = when (borrador.estado) {
                                "ENVIANDO" -> MainOrange
                                "ERROR" -> ErrorRed
                                "BORRADOR_AUTO" -> MainGreen
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(borrador.fecha, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(borrador.hora ?: stringResource(R.string.no_hour_value), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp), tint = Blue)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(stringResource(R.string.edit_button_individual_card), color = Blue)
                            }
                        },
                        onClick = { mostrarMenu = false; onEditarClick() }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp), tint = ErrorRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(stringResource(R.string.delete_button_individual_card), color = ErrorRed)
                            }
                        },
                        onClick = { mostrarMenu = false; onEliminarClick() }
                    )
                }
            }
        }
    }
}