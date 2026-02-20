package com.example.terrabit_app.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.MainGreen

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
    onSeleccionar: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkBlueGrey,
            letterSpacing = 0.15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange() }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                placeholder = { Text(placeholder, color = BlueGrey) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainGreen,
                    unfocusedBorderColor = DarkWhiteBackground,
                    focusedTextColor = DarkBlueGrey,
                    unfocusedTextColor = DarkBlueGrey
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onDismissRequest() },
                modifier = Modifier.background(Color.White)
            ) {
                opciones.forEach { (codigo, nombre) ->
                    DropdownMenuItem(
                        text = { Text(nombre, fontSize = 15.sp, color = DarkBlueGrey) },
                        onClick = { onSeleccionar(codigo, nombre) },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}