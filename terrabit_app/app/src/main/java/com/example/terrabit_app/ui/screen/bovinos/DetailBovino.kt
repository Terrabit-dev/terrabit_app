package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.ElementosConCodigos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBovino(navController: NavController, animal: Animal) {

    //Elementos con codigos
    val elements = ElementosConCodigos()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detalles del animal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = animal.identificador,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(
                title = "Indentificador del animal",
                accentColor = MainGreen
            ) {
                DetailRow(
                    label = "Identificador",
                    value = animal.identificador,
                    mono = true
                )
                if (!animal.identificadorElectronic.isNullOrEmpty()) {
                    DetailDivider()
                    DetailRow(
                        label = "Identificador electronico",
                        value = animal.identificadorElectronic,
                        mono = true
                    )
                }
                if (!animal.tipusIdentificadorElectronic.isNullOrEmpty()) {
                    DetailDivider()
                    DetailRow(
                        label = "Tipo de indentificador electronico",
                        value = animal.tipusIdentificadorElectronic
                    )
                }
            }

            DetailCard(
                title ="Información basica",
                accentColor = MainGreen
            ) {
                DetailRow(
                    label ="Fecha de nacimiento",
                    value = formatearFecha(animal.dataNaixement)
                )
                DetailDivider()
                DetailRowSexe(
                    label = "Sexo",
                    sexe = animal.sexe
                )
                DetailDivider()
                DetailRow(
                    label = "Raza",
                    value = stringResource(elements.getRazaBovinasId(animal.raca))
                )
            }

            if (!animal.explotacioNaixement.isNullOrEmpty() ||
                !animal.paisNaixement.isNullOrEmpty() ||
                !animal.identificadorMare.isNullOrEmpty()
            ) {
                DetailCard(
                    title = "Origenes del animal",
                    accentColor = MainGreen
                ) {
                    if (!animal.explotacioNaixement.isNullOrEmpty()) {
                        DetailRow(
                            label = "Explotación de nacimientos",
                            value = animal.explotacioNaixement,
                            mono = true
                        )
                    }
                    if (!animal.paisNaixement.isNullOrEmpty()) {
                        if (!animal.explotacioNaixement.isNullOrEmpty()) DetailDivider()
                        DetailRow(
                            label = "Pais de nacimientos",
                            value = stringResource(elements.getNombrePaisId(animal.paisNaixement))
                        )
                    }
                    if (!animal.identificadorMare.isNullOrEmpty()) {
                        if (!animal.explotacioNaixement.isNullOrEmpty() ||
                            !animal.paisNaixement.isNullOrEmpty()
                        ) DetailDivider()
                        DetailRow(
                            label = "Id de la madre",
                            value = animal.identificadorMare,
                            mono = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor.copy(alpha = 0.08f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                content = content
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, mono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetailRowSexe(label: String, sexe: String) {
    val (text, bgColor, textColor) = when (sexe) {
        "01" -> Triple(
            stringResource(R.string.card_info_sex_male),
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32)
        )
        "02" -> Triple(
            stringResource(R.string.card_info_sex_female),
            Color(0xFFFCE4EC),
            Color(0xFFC2185B)
        )
        else -> Triple(
            sexe,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        thickness = 0.5.dp
    )

}