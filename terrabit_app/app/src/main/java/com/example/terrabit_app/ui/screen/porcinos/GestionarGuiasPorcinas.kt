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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.viewmodel.porcinos.CrearGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionGuiasPorcinos(
        navController: NavController,
        viewModelGestionarGuias: GestionarGuiasViewModel = viewModel(),
        viewModelCrearGuias: CrearGuiaPorcinosViewModel = viewModel()
    ) {
    val uiStateGestionGuias by viewModelGestionarGuias.uiState.collectAsState()

    val lista: List<GuiaMobilitatPorcinos> = listOf(
        GuiaMobilitatPorcinos(
            moOrigen = "Granja El Prat",
            remo = "ES0809876543",
            moDesti = "Centro de Selección Genética",
            categoria = "Truges Reproductores",
            nombreAnimals = 25,
            transportista = "Logística Osona",
            responsable = "Marta Puig",
            vehicle = "5678-KBC",
            dataSortida = 202602170700L,   // 17/02/2026 07:00
            dataArribada = 202602170915L    // 17/02/2026 09:15
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar y Confirmar Guías") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
//            if (uiStateGestionGuias.listaGuiasPorcinos.isEmpty()) {
//                item {
//                    Text(
//                        "No hay guías para editar o confirmar",
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                }
//            } else {
                items(/*uiStateGestionGuias.listaGuiasPorcinos*/lista) { guia ->
                    GuiaCard(navController, guia, viewModelCrearGuias)
                }
//            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GuiaCard(
    navController: NavController,
    guia: GuiaMobilitatPorcinos,
    viewModelCrearGuias: CrearGuiaPorcinosViewModel = viewModel()
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
                        text = guia.remo,
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
                        text = "Fecha Salida: " + formatearFecha(guia.dataSortida),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Fecha Llegada: " + formatearFecha(guia.dataArribada),
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
                        viewModelCrearGuias.rellenarCampos(guia)
                        navController.navigate(Routes.EditarGuiaPorcinos.route)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MainOrange
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.White
                    )
                }
                FilledIconButton(
                    onClick = {TODO()},
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
fun formatearFecha(dateLong: Long): String {
    val dateString = dateLong.toString()
    val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val dateTime = LocalDateTime.parse(dateString, inputFormatter)
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return dateTime.format(outputFormatter)
}