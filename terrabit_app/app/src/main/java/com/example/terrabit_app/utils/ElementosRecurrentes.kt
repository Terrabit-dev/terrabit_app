package com.example.terrabit_app.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import androidx.compose.ui.graphics.Color
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedValue: String,
    expanded: Boolean,
    placeholder: String,
    opciones: Map<String, String>,
    onExpandedChange: () -> Unit,
    onDismissRequest: () -> Unit,
    onSeleccionar: (String, String) -> Unit,
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
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = if (enabled) expanded else false,
            onExpandedChange = { if (enabled) onExpandedChange() }
        ) {
            OutlinedTextField(
                value = selectedValue,
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
                    opciones.forEach { (codigo, nombre) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    nombre,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = { onSeleccionar(codigo, nombre) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                        )
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
    codisMos: List<String>,
    seleccionado: String? = null,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onSeleccionar: (String) -> Unit = {},
    modifier: Modifier = Modifier,
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