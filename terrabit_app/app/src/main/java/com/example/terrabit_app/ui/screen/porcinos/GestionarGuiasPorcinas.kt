package com.example.terrabit_app.ui.screen.porcinos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.terrabit_app.data.network.ApiInterface
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.UserPreferences
import com.example.terrabit_app.viewmodel.porcinos.CrearGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionGuiasPorcinos(
    navController: NavController,
) {
    val context = LocalContext.current

    // 1. Inicializamos la API usando tu propio companion object
    val repo = remember { Repositorio() }

    // 2. Inicializamos las preferencias
    val userPrefs = remember { UserPreferences(context) }

    // 3. Creamos el ViewModel con la Factory
    val viewModelGestionarGuias: GestionarGuiasViewModel = viewModel(
        factory = GestionarGuiasViewModelFactory(repo, userPrefs)
    )

    val uiStateGestionGuias by viewModelGestionarGuias.uiState.collectAsState()

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModelGestionarGuias.cargarMovimientosDesdeApi()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar y Confirmar Guías",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
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
        // Contenidor principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // CONDICIÓ: Si està carregant, mostra el cercle
            if (uiStateGestionGuias.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MainGreen,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando movimientos de la API...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Si NO està carregant, mostra la llista o el missatge de buit
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiStateGestionGuias.listaGuiasPorcinos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No hay guías para editar o confirmar",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        items(uiStateGestionGuias.listaGuiasPorcinos) { guia ->
                            GuiaCard(
                                navController = navController,
                                guia = guia,
                                viewModelGestionarGuias = viewModelGestionarGuias
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GuiaCard(
    navController: NavController,
    guia: GuiaGTRLista,
    viewModelGestionarGuias: GestionarGuiasViewModel = viewModel(),
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
                        text = "Fecha Salida: " + formatearFecha(guia.dataSortida.toLong()),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Fecha Llegada: " + formatearFecha(guia.dataArribada.toLong()),
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
                        //viewModelGestionarGuias.cargarDatosGuia(guia)
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
fun formatearFecha(dateLong: Long): String {
    val dateString = dateLong.toString()
    val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val dateTime = LocalDateTime.parse(dateString, inputFormatter)
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return dateTime.format(outputFormatter)
}