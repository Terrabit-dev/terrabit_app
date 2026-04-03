package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.porcinos.ElementosConCodigosPorcinos
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaGuiasPorcinas(
    navController: NavController,
    viewModelGestionarGuias: GestionarGuiasViewModel,
    viewModelEditarGuias: EditarGuiaPorcinosViewModel
) {
    val uiState by viewModelGestionarGuias.uiState.collectAsState()

    if (uiState.mostrarDatePicker) {
        val dpState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModelGestionarGuias.ocultarDatePicker() },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { viewModelGestionarGuias.seleccionarFecha(it) }
                }) { Text("Aceptar", color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModelGestionarGuias.ocultarDatePicker() }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = dpState, colors = DatePickerDefaults.colors(selectedDayContainerColor = MainOrange, todayDateBorderColor = MainOrange))
        }
    }

    if (uiState.mostrarTimePicker) {
        val tpState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModelGestionarGuias.ocultarTimePicker() },
            confirmButton = {
                TextButton(onClick = {
                    viewModelGestionarGuias.seleccionarHora(tpState.hour, tpState.minute)
                }) { Text("Aceptar", color = MainOrange) }
            },
            dismissButton = {
                TextButton(onClick = { viewModelGestionarGuias.ocultarTimePicker() }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                TimePicker(state = tpState, colors = TimePickerDefaults.colors(clockDialSelectedContentColor = Color.White, selectorColor = MainOrange))
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.gest_porcinos_edit_confirm), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainOrange, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                !uiState.consultaIniciada -> {
                    FormularioConsulta(
                        rega         = uiState.rega,
                        fechaDisplay = uiState.fechaCorteDisplay,
                        mensajeError = uiState.mensajeError,
                        onRegaChange = { viewModelGestionarGuias.actualizarRega(it) },
                        onFechaClick = { viewModelGestionarGuias.mostrarDatePicker() },
                        onConsultar  = { viewModelGestionarGuias.consultarLista() }
                    )
                }
                uiState.isLoading -> {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(color = MainOrange, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.gest_porcinos_cargando_mov), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (uiState.listaGuiasPorcinos.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(R.string.gest_porcinos_no_guias), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(uiState.listaGuiasPorcinos) { guia ->
                                GuiaCard(navController = navController, guia = guia, viewModelGestionarGuias = viewModelGestionarGuias, viewModelEditarGuias = viewModelEditarGuias)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormularioConsulta(
    rega: String,
    fechaDisplay: String,
    mensajeError: String?,
    onRegaChange: (String) -> Unit,
    onFechaClick: () -> Unit,
    onConsultar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(text = stringResource(R.string.gest_porcinos_edit_confirm), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

                CampoTexto(label = "Código REGA", valor = rega, placeholder = "Ej: ES080470001881", onValueChange = onRegaChange, defectColor = false)

                Column {
                    Text(text = "Fecha de corte", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().clickable { onFechaClick() }) {
                        OutlinedTextField(
                            value = fechaDisplay, onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Selecciona fecha y hora", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MainOrange) },
                            readOnly = true, enabled = false, shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MainOrange,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true
                        )
                    }
                }

                if (!mensajeError.isNullOrBlank()) {
                    Text(text = mensajeError, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Button(onClick = onConsultar, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = MainOrange), shape = MaterialTheme.shapes.medium) {
                    Text(text = "Consultar lista", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GuiaCard(
    navController: NavController,
    guia: GuiaGTRLista,
    viewModelGestionarGuias: GestionarGuiasViewModel,
    viewModelEditarGuias: EditarGuiaPorcinosViewModel
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = guia.moOrigen, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = MainOrange, modifier = Modifier.size(18.dp))
                    Text(text = guia.moDesti, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                FilledIconButton(
                    onClick = {
                        viewModelEditarGuias.cargarDatosGuia(guia)
                        navController.navigate(Routes.EditarGuiaPorcinos.route)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MainOrange),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.content_description_edit), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Text(text = guia.remo, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp, fontFamily = FontFamily.Monospace)
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Column {
                        Text(text = stringResource(R.string.form_porcino_entradas_fecha_salida), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = formatearFecha(guia.dataSortida), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Column {
                        Text(text = stringResource(R.string.form_porcinos_entradas_fecha_llegada), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = formatearFecha(guia.dataArribada), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(icon = Icons.Default.Pets, label = "${guia.nombreAnimals}")
                InfoChip(icon = Icons.Default.Category, label = "Cat. ${ElementosConCodigosPorcinos().categorias()[guia.categoria]}")
                Log.d("Guia info", "Informacion: $guia - ${guia.categoria}")
                guia.vehicle?.let { InfoChip(icon = Icons.Default.LocalShipping, label = it) }
            }
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, label: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MainOrange, modifier = Modifier.size(12.dp))
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatearFecha(dateLong: Long): String {
    if (dateLong == 0L) return "--/--/----"
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val dateTime = LocalDateTime.parse(dateLong.toString(), inputFormatter)
        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (e: Exception) { "--/--/----" }
}