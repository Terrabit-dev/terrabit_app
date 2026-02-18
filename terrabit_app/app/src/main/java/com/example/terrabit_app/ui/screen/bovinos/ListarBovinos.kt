package com.example.terrabit_app.ui.screen.bovinos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.viewmodel.ListarBovinosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarBovinos(navController: NavController, viewModel: ListarBovinosViewModel) {
    val listaFiltrada by viewModel.listaFiltrada.observeAsState(emptyList())
    val cargando by viewModel.cargando.observeAsState(false)
    val refrescando by viewModel.refrescando.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val busqueda by viewModel.busqueda.observeAsState("")

    LaunchedEffect(Unit) {
        viewModel.cargarBovinos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Listado de Bovinos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HomeBovinos.route) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A7C59),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de búsqueda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { viewModel.actualizarBusqueda(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Buscar por identificador...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF4A7C59)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A7C59),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        cursorColor = Color(0xFF4A7C59)
                    )
                )
            }

            // Pull to Refresh
            PullToRefreshBox(
                isRefreshing = refrescando,
                onRefresh = { viewModel.refrescar() },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    cargando -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4A7C59))
                        }
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = error ?: "Error desconocido",
                                    color = Color.Red,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.cargarBovinos() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4A7C59)
                                    )
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                    listaFiltrada.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (busqueda.isEmpty()) "No hay bovinos" else "No se encontraron resultados",
                                color = Color(0xFF64748B),
                                fontSize = 16.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listaFiltrada) { animal ->
                                TarjetaBovino(animal)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaBovino(animal: Animal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = animal.identificador,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "Sexo",
                    value = when (animal.sexe) {
                        "01" -> "Macho"
                        "02" -> "Hembra"
                        else -> animal.sexe
                    }
                )
                InfoItem(
                    label = "Fecha Nac.",
                    value = formatearFecha(animal.dataNaixement)
                )
            }

            if (!animal.identificadorMare.isNullOrEmpty()) {
                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                Text(
                    text = "Madre: ${animal.identificadorMare}",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatearFecha(fecha: String): String {
    return try {
        if (fecha.length == 8) {
            val dia = fecha.substring(6, 8)
            val mes = fecha.substring(4, 6)
            val anio = fecha.substring(0, 4)
            "$dia/$mes/$anio"
        } else {
            fecha
        }
    } catch (e: Exception) {
        fecha
    }
}