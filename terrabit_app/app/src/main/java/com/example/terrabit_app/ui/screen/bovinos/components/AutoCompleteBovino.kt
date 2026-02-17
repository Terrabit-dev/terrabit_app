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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // ✅ AÑADIDO: Auto-expandir cuando hay sugerencias
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
            // ✅ AÑADIDO: Estilo mejorado
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
                // ✅ AÑADIDO: Elevación para mejor visual
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
            text = "Sexo: ${getSexoText(animal.sexe)} | Raza: ${animal.raca}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    HorizontalDivider()
}

private fun getSexoText(sexe: String): String {
    return when (sexe) {
        "01" -> "Macho"
        "02" -> "Hembra"
        else -> sexe
    }
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