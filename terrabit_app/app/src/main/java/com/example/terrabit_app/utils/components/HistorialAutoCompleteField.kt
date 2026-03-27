package com.example.terrabit_app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.ui.theme.MainOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialAutoCompleteField(
    valor: String,
    onValorChange: (String) -> Unit,
    label: String,
    clave: String,
    historialManager: HistorialCamposManager,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var historial by remember { mutableStateOf<List<String>>(emptyList()) }
    var sugerencias by remember { mutableStateOf<List<String>>(emptyList()) }
    var mostrarSugerencias by remember { mutableStateOf(false) }

    LaunchedEffect(clave) {
        historial = historialManager.obtenerHistorial(clave)
    }

    LaunchedEffect(valor, historial) {
        sugerencias = if (valor.isBlank()) {
            historial
        } else {
            historial.filter { it.contains(valor, ignoreCase = true) && it != valor }
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = {
                onValorChange(it)
                mostrarSugerencias = true
            },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        mostrarSugerencias = true
                        scope.launch {
                            historial = historialManager.obtenerHistorial(clave)
                        }
                    } else {
                        mostrarSugerencias = false
                    }
                },
            enabled = enabled,
            isError = isError,
            supportingText = supportingText,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MainOrange,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor    = MainOrange,
                cursorColor          = MainOrange,
                disabledBorderColor  = MaterialTheme.colorScheme.outline,
                disabledTextColor    = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor   = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        AnimatedVisibility(visible = mostrarSugerencias && sugerencias.isNotEmpty() && enabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(sugerencias) { entrada ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValorChange(entrada)
                                    mostrarSugerencias = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = entrada,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        historialManager.eliminarEntrada(clave, entrada)
                                        historial = historialManager.obtenerHistorial(clave)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        if (sugerencias.last() != entrada) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }
}