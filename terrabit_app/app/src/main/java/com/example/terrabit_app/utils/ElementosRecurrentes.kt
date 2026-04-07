package com.example.terrabit_app.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
    label: String,
    selectedValue: T?,
    expanded: Boolean,
    placeholder: String,
    opciones: Map<String, T>,
    onExpandedChange: () -> Unit,
    onDismissRequest: () -> Unit,
    onSeleccionar: (String, T) -> Unit,
    defectColor: Boolean,
    enabled: Boolean = true
) {
    val accentColor = if (defectColor) MainGreen else MainOrange

    // Resolvemos el texto que se mostrará en el campo principal dependiendo del tipo T
    val textoMostrar = when (selectedValue) {
        is Int -> if (selectedValue != 0) stringResource(id = selectedValue) else "" // Evita error si mandas 0 por defecto
        is String -> selectedValue
        else -> selectedValue?.toString() ?: ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = if (enabled) expanded else false,
            onExpandedChange = { if (enabled) onExpandedChange() }
        ) {
            OutlinedTextField(
                value = textoMostrar, // Usamos el valor ya resuelto
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = enabled,
                readOnly = true,
                placeholder = {
                    Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(if (enabled) expanded else false) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
            if (enabled) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onDismissRequest() },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    opciones.forEach { (codigo, valorRaw) ->

                        // Resolvemos el texto para cada elemento de la lista desplegable
                        val nombreItem = when (valorRaw) {
                            is Int -> stringResource(id = valorRaw)
                            is String -> valorRaw
                            else -> valorRaw.toString()
                        }

                        DropdownMenuItem(
                            text = {
                                Text(
                                    nombreItem,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                // Devolvemos el código (String) y el valor original en su tipo correspondiente (T)
                                onSeleccionar(codigo, valorRaw)
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LargeDropdownField(
    label: String,
    selectedValue: T?,
    expanded: Boolean,
    placeholder: String,
    opciones: Map<String, T>,
    onExpandedChange: () -> Unit,
    onDismissRequest: () -> Unit,
    onSeleccionar: (String, T) -> Unit,
    defectColor: Boolean,
    enabled: Boolean = true
) {
    // Definimos el color según tu lógica (asegúrate de importar MainGreen/MainOrange si los tienes en otro archivo)
    val accentColor = if (defectColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    // Texto del campo principal
    val textoMostrar = when (selectedValue) {
        is Int -> if (selectedValue != 0) stringResource(id = selectedValue) else ""
        is String -> selectedValue
        else -> selectedValue?.toString() ?: ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = textoMostrar,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (enabled) onExpandedChange() },
            enabled = false,
            readOnly = true,
            placeholder = {
                Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    }

    // Diálogo con buscador y LazyColumn
    if (expanded && enabled) {
        // Estado de la búsqueda (se reinicia al cerrar)
        var searchQuery by remember { mutableStateOf("") }

        Dialog(onDismissRequest = {
            searchQuery = "" // Limpiamos la búsqueda al cancelar
            onDismissRequest()
        }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp) // Limita la altura para que no ocupe toda la pantalla
            ) {
                Column {
                    // 1. Barra de Búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Buscar...", fontSize = 15.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // 2. Traducir y Filtrar (¡El truco está aquí!)
                    // Usamos .map para convertir los Int a String y luego .filter para buscar
                    val opcionesProcesadas = opciones.entries.map { entry ->
                        val textoItem = when (val valorRaw = entry.value) {
                            is Int -> if (valorRaw != 0) stringResource(id = valorRaw) else ""
                            is String -> valorRaw
                            else -> valorRaw.toString()
                        }
                        // Guardamos el Código Original, el Valor Original, y el Texto Traducido
                        Triple(entry.key, entry.value, textoItem)
                    }.filter {
                        // Filtramos ignorando mayúsculas/minúsculas. Buscamos tanto por el texto como por el código (ej. "9999")
                        it.third.contains(searchQuery, ignoreCase = true) ||
                                it.first.contains(searchQuery, ignoreCase = true)
                    }

                    // 3. Renderizar la lista filtrada
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                    ) {
                        items(opcionesProcesadas) { (codigo, valorRaw, nombreItem) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        nombreItem,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    searchQuery = "" // Limpiamos la búsqueda tras elegir
                                    onSeleccionar(codigo, valorRaw)
                                    onDismissRequest()
                                },
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                            )
                        }

                        // Mensaje si no hay resultados
                        if (opcionesProcesadas.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_found_result),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoTexto(
    label: String,
    valor: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    defectColor: Boolean,
    enabled: Boolean = true
) {
    val accentColor = if (defectColor) MainGreen else MainOrange

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = accentColor,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
fun CodiMoSelector(
    modifier: Modifier = Modifier,
    codisMos: List<String>,
    seleccionado: String? = null,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onSeleccionar: (String) -> Unit = {},
    accentColor: Color = MainGreen
) {
    Box(modifier = modifier) {
        // ── Botón trigger ──
        Surface(
            onClick = onToggle,
            shape = RoundedCornerShape(10.dp),
            color = if (seleccionado != null) accentColor else Color.White,
            border = BorderStroke(1.5.dp, accentColor),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.House,
                    contentDescription = null,
                    tint = if (seleccionado != null) Color.White else accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = seleccionado ?: "Codi MO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (seleccionado != null) Color.White else accentColor
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (seleccionado != null) Color.White else accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ── Dropdown ──
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .widthIn(min = 180.dp)
        ) {
            codisMos.forEachIndexed { index, codi ->
                val esSeleccionado = codi == seleccionado

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (esSeleccionado) accentColor else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .border(1.dp, accentColor, CircleShape)
                            )
                            Column {
                                Text(
                                    text = codi,
                                    fontSize = 14.sp,
                                    fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (esSeleccionado) accentColor else DarkBlueGrey
                                )
                            }
                        }
                    },
                    onClick = { onSeleccionar(codi) },
                    modifier = Modifier
                        .background(
                            if (esSeleccionado) accentColor.copy(alpha = 0.08f)
                            else Color.Transparent
                        )
                )

                if (index < codisMos.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = DarkWhiteBackground
                    )
                }
            }
        }


    }
}



// Pantalla de Carga al cambiar de Idioma

@Composable
fun PantallaCargaIdioma(visible: Boolean) {
    if (!visible) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(120.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                        stringResource(R.string.loading_processing),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}