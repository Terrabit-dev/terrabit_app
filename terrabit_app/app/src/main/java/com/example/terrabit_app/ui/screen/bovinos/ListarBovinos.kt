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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.utils.SwipeBackContainer
import com.example.terrabit_app.viewmodel.ListarBovinosViewModel
import com.example.terrabit_app.ui.screen.bovinos.Home
import com.example.terrabit_app.utils.CodiMoSelector
import com.example.terrabit_app.viewmodel.CodiMoManagerViewModel

import okhttp3.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarBovinos(navController: NavController) {
    val viewModel = hiltViewModel<ListarBovinosViewModel>()
    val listaFiltrada by viewModel.listaFiltrada.observeAsState(emptyList())
    val cargando by viewModel.cargando.observeAsState(false)
    val refrescando by viewModel.refrescando.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val busqueda by viewModel.busqueda.observeAsState("")
    val codiMoViewModel = hiltViewModel<CodiMoManagerViewModel>()
    val codisMoExpandido by codiMoViewModel.codisMoExpandido.observeAsState(false)
    val codiMoActivo by codiMoViewModel.codiMoActivo.observeAsState(null)


    LaunchedEffect(Unit) {
        viewModel.cargarBovinos()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.list_bovinos_title), fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Routes.HomeBovinos.route) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { viewModel.actualizarBusqueda(it) },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        placeholder = { Text(stringResource(R.string.search_bar_list_bovinos), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MainGreen) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MainGreen,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    CodiMoSelector(
                        codisMos = codiMoViewModel.getCodisMos(),
                        seleccionado = codiMoActivo,
                        expanded = codisMoExpandido,
                        onToggle = { codiMoViewModel.toggleCodisMoExpandido() },
                        onDismiss = { codiMoViewModel.cerrarCodisMo() },
                        onSeleccionar = { codi -> codiMoViewModel.seleccionarCodiMo(codi)  },
                        accentColor = MainGreen
                    )
                }

                PullToRefreshBox(
                    isRefreshing = refrescando,
                    onRefresh = { viewModel.refrescar() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        cargando -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MainGreen)
                            }
                        }
                        error != null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = error ?: stringResource(R.string.error_loading_list_bovinos), color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { viewModel.cargarBovinos() }, colors = ButtonDefaults.buttonColors(containerColor = MainGreen)) {
                                        Text(stringResource(R.string.retry_loading_list_bovinos))
                                    }
                                }
                            }
                        }
                        listaFiltrada.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (busqueda.isEmpty()) stringResource(R.string.empty_list_bovinos) else "No se encontraron resultados",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                items(listaFiltrada) { animal -> TarjetaBovino(animal) }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = animal.identificador, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoItem(
                    label = stringResource(R.string.card_info_sex),
                    value = when (animal.sexe) {
                        "01" -> stringResource(R.string.card_info_sex_male)
                        "02" -> stringResource(R.string.card_info_sex_female)
                        else -> animal.sexe
                    }
                )
                InfoItem(label = stringResource(R.string.card_info_date_born), value = formatearFecha(animal.dataNaixement))
            }
            if (!animal.identificadorMare.isNullOrEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                Text(text = "${stringResource(R.string.card_info_mom)} ${animal.identificadorMare}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun formatearFecha(fecha: String): String {
    return try {
        if (fecha.length == 8) {
            val dia = fecha.substring(6, 8)
            val mes = fecha.substring(4, 6)
            val anio = fecha.substring(0, 4)
            "$dia/$mes/$anio"
        } else fecha
    } catch (e: Exception) { fecha }
}