package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.DataClassPorcinos.MovimentPteDetail
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.viewmodel.porcinos.EntradasPorcinosViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradasPorcinos(
    navController: NavController,
    viewModelEntradasGuias: EntradasPorcinosViewModel = viewModel(),
) {
    val uiState by viewModelEntradasGuias.uiState.collectAsState()
    val context = LocalContext.current

    // Se cargan las User Preferences y se hace la llamada para obtener las entradas
    LaunchedEffect(Unit) {
        viewModelEntradasGuias.inicializarUserPreferences(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.name_confirmar_entradas),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.listaEntradasPorcinos.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.form_porcinos_no_entr),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                items(uiState.listaEntradasPorcinos) { guia ->
                    EntradaCard(guia, viewModelEntradasGuias)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntradaCard(
    guia: MovimentPteDetail,
    viewModelGestionarGuias: EntradasPorcinosViewModel = viewModel(),
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Column(
                modifier = Modifier.fillMaxHeight().padding(4.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    Text(
                        text = guia.codiRemo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = guia.moDesti,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Column {
                    Text(
                        text = "Fecha Salida: " + formatearFechaEntrega(guia.dataSortida.toLong()),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Fecha Llegada: " + formatearFechaEntrega(guia.dataArribada.toLong()),
                        fontSize = 16.sp
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledIconButton(
                    onClick = {
                        viewModelGestionarGuias.confirmarGuia()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MainGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatearFechaEntrega(dateLong: Long): String {
    val dateString = dateLong.toString()
    val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val dateTime = LocalDateTime.parse(dateString, inputFormatter)
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return dateTime.format(outputFormatter)
}