package com.example.terrabit_app.ui.screen.bovinos.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun SuggestionItem(
    animal: Animal,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = animal.identificador,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (animal.identificadorMare != null) {
            Text(
                text = "Madre: ${animal.identificadorMare}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "Sexo: ${getSexoTexto(animal.sexe)} | Raza: ${animal.raca}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Composable
fun <T> useDebounce(
    value: T,
    delayMillis: Long = 300L,
    onDebounce: (T) -> Unit
) {
    val scope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(value) {
        job?.cancel()
        job = scope.launch {
            delay(delayMillis)
            onDebounce(value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoIdentificadorAutoComplete(
    label: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onClickBluetooth: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    defectColor: Boolean = true,
    suggestions: List<Animal> = emptyList(),
    onAnimalSelected: ((Animal) -> Unit)? = null,
    isLoadingSuggestions: Boolean = false,
    onClickUsb: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val accentColor = if (defectColor) MainGreen else MainOrange

    LaunchedEffect(suggestions, valor) {
        expanded = enabled && valor.isNotBlank() && suggestions.isNotEmpty() && onAnimalSelected != null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType,
                autoCorrect = false
            ),
            trailingIcon = if (enabled) {
                {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (valor.isNotBlank() && onAnimalSelected != null) {
                            IconButton(onClick = { onValueChange(""); expanded = false }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (onClickUsb != null) {
                            IconButton(onClick = onClickUsb) {
                                Icon(
                                    Icons.Outlined.Usb,
                                    contentDescription = "Leer crotal por USB",
                                    tint = accentColor
                                )
                            }
                        }
                        IconButton(onClick = onClickBluetooth) {
                            Icon(
                                Icons.Outlined.Bluetooth,
                                contentDescription = "Leer crotal por Bluetooth",
                                tint = accentColor
                            )
                        }
                    }
                }
            } else null
        )

        AnimatedVisibility(visible = expanded && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(suggestions) { animal ->
                        SuggestionItem(
                            animal = animal,
                            onClick = {
                                onAnimalSelected?.invoke(animal)
                                onValueChange(animal.identificador)
                                expanded = false
                                keyboardController?.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getSexoTexto(sexe: String): String {
    return when (sexe) {
        "01" -> stringResource(R.string.female)
        "02" -> stringResource(R.string.male)
        else -> sexe
    }
}