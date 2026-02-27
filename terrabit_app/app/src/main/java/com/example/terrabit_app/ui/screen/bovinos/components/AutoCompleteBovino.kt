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
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteBovinoField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<Animal>,
    onAnimalSelected: (Animal) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Identificador del Animal",
    placeholder: String = "Escribe para buscar...",
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-expandir cuando hay sugerencias
    LaunchedEffect(suggestions, value) {
        expanded = value.isNotBlank() && suggestions.isNotEmpty()
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            enabled = enabled,
            singleLine = true,
            trailingIcon = {
                Row {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    if (value.isNotBlank()) {
                        IconButton(onClick = {
                            onValueChange("")
                            expanded = false
                        }) {
                            Icon(Icons.Default.Clear, "Limpiar")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // Estilo mejorado
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        AnimatedVisibility(visible = expanded && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                // Elevación para mejor visual
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(suggestions) { animal ->
                        SuggestionItem(
                            animal = animal,
                            onClick = {
                                onAnimalSelected(animal)
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

    HorizontalDivider()
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
    keyboardType: KeyboardType = KeyboardType.Text,
    defectColor: Boolean = true,
    // Parámetros opcionales de autocompletado
    // Si no se pasan, el campo funciona como campo simple con BT
    suggestions: List<Animal> = emptyList(),
    onAnimalSelected: ((Animal) -> Unit)? = null,
    isLoadingSuggestions: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val accentColor = if (defectColor) MainGreen else MainOrange

    // Expandir la lista solo si hay sugerencias y hay texto escrito
    LaunchedEffect(suggestions, valor) {
        expanded = valor.isNotBlank() && suggestions.isNotEmpty() && onAnimalSelected != null
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // ---- Label ----
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkBlueGrey,
            letterSpacing = 0.15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // ---- TextField ----
        OutlinedTextField(
            value = valor,
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = BlueGrey) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = DarkWhiteBackground,
                focusedTextColor = DarkBlueGrey,
                unfocusedTextColor = DarkBlueGrey,
                cursorColor = accentColor
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType,
                autoCorrect = false
            ),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Spinner de carga de sugerencias
                    if (isLoadingSuggestions) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 4.dp),
                            strokeWidth = 2.dp,
                            color = accentColor
                        )
                    }
                    // Botón limpiar — solo si hay texto y hay autocompletado activo
                    if (valor.isNotBlank() && onAnimalSelected != null) {
                        IconButton(onClick = {
                            onValueChange("")
                            expanded = false
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                tint = BlueGrey,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    // Botón Bluetooth — siempre visible
                    IconButton(onClick = onClickBluetooth) {
                        Icon(
                            Icons.Outlined.Bluetooth,
                            contentDescription = "Leer crotal por Bluetooth",
                            tint = accentColor
                        )
                    }
                }
            }
        )

        // ---- Lista de sugerencias (autocompletado) ----
        AnimatedVisibility(visible = expanded && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

// ============================================
// ITEM INTERNO DE SUGERENCIA
// ============================================



@Composable
private fun getSexoTexto(sexe: String): String {
    return when (sexe) {
        "01" -> stringResource(R.string.female)
        "02" -> stringResource(R.string.male)
        else -> sexe
    }
}