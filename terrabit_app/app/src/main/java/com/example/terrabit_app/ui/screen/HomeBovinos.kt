package com.example.terrabit_app.ui.pantallas

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.DarkWhiteBackground
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.ui.theme.MintCreamGreen
import com.example.terrabit_app.ui.theme.WhiteBackground
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.MainViewmodel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    drawerViewModel: DrawerViewModel,
    mainViewModel: MainViewmodel
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tipoAnimalSeleccionado by drawerViewModel.tipoAnimalSeleccionado.observeAsState("Bovinos")

    // Estado para controlar qué pantalla mostrar
    var mostrarBorradores by remember { mutableStateOf(false) }

    // Inicializar SharedPreferences y cargar borradores
    LaunchedEffect(Unit) {
        mainViewModel.inicializarSharedPreferences(context)
        mainViewModel.cargarBorradores()
    }

    // Drawer con menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                tipoSeleccionado = tipoAnimalSeleccionado,
                onTipoSeleccionado = { tipo ->
                    drawerViewModel.seleccionarTipoAnimal(tipo)
                    mostrarBorradores = false
                    scope.launch { drawerState.close() }
                },
                onBorradoresClick = {
                    mostrarBorradores = true
                    scope.launch { drawerState.close() }
                },
                borradoresSeleccionado = mostrarBorradores,
                navController = navController
            )
        }
    ) {
        Scaffold(
            containerColor = WhiteBackground
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (mostrarBorradores) {
                    BorradoresScreen(
                        viewModel = mainViewModel,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                } else {
                    HomeContent(
                        tipoAnimalSeleccionado = tipoAnimalSeleccionado,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    tipoAnimalSeleccionado: String,
    onMenuClick: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con gradiente y bienvenida
        HeaderBienvenida(
            tipoAnimal = tipoAnimalSeleccionado,
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título de sección
        Text(
            stringResource(R.string.subtitle_home),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBlueGrey,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Grid de tarjetas organizadoras
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta Gestión de Bovinos
            TarjetaMenu(
                icono = Icons.Default.Agriculture,
                titulo = stringResource(R.string.card_name_animals),
                descripcion = stringResource(R.string.card_description_animals),
                colorFondo = MainGreen,
                onClick = { navController.navigate(Routes.GestionBovinos.route) }
            )

            // Tarjeta Guías/Movimientos
            TarjetaMenu(
                icono = Icons.Default.LocalShipping,
                titulo = stringResource(R.string.card_name_guias),
                descripcion = stringResource(R.string.card_description_guias),
                colorFondo = MainOrange,
                contadorBadge = 2,
                onClick = { navController.navigate(Routes.GuiasMovimientos.route) }
            )

            // Tarjeta Material
            TarjetaMenu(
                icono = Icons.Default.ShoppingCart,
                titulo = stringResource(R.string.card_name_material),
                descripcion = stringResource(R.string.card_description_material),
                colorFondo = MainGreen,
                onClick = { navController.navigate(Routes.MaterialCategoria.route) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Card de Información del Sistema
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MintCreamGreen
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MainGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MainGreen,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.information_title_home),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MainGreen,
                        letterSpacing = 0.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.information_description_home),
                        fontSize = 14.sp,
                        color = DarkBlueGrey,
                        lineHeight = 20.sp,
                        letterSpacing = 0.1.sp
                    )
                }
            }
        }

        // Espaciado inferior
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TarjetaMenu(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    colorFondo: Color,
    contadorBadge: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con badge
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorFondo,
                    modifier = Modifier.size(70.dp),
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        icono,
                        contentDescription = titulo,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }

                // Badge de notificación
                if (contadorBadge != null) {
                    Badge(
                        containerColor = ErrorRed,
                        modifier = Modifier
                            .offset(x = 4.dp, y = (-4).dp)
                    ) {
                        Text(
                            contadorBadge.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Textos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = DarkBlueGrey,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    descripcion,
                    fontSize = 14.sp,
                    color = BlueGrey,
                    lineHeight = 18.sp
                )
            }

            // Icono flecha
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver más",
                tint = BlueGrey,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// Función mágica que cambia el idioma
fun cambiarIdioma(codigoIdioma: String) {
    val appLocale = LocaleListCompat.forLanguageTags(codigoIdioma)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@Composable
fun DrawerContent(
    tipoSeleccionado: String,
    onTipoSeleccionado: (String) -> Unit,
    onBorradoresClick: () -> Unit,
    borradoresSeleccionado: Boolean,
    navController: NavController
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header del drawer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    "Terrabit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainGreen
                )
                Text(
                    stringResource(R.string.drawer_subtitle),
                    fontSize = 14.sp,
                    color = BlueGrey,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(color = DarkWhiteBackground)

            Spacer(modifier = Modifier.height(24.dp))

            // Título de sección
            Text(
                stringResource(R.string.drawer_explained),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlueGrey,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            // Opción Bovinos
            OpcionTipoAnimal(
                icono = Icons.Default.Agriculture,
                titulo = stringResource(R.string.bovinos_name),
                seleccionado = tipoSeleccionado == stringResource(R.string.bovinos_name) && !borradoresSeleccionado,
                onClick = {
                    onTipoSeleccionado("Bovinos")
                    navController.navigate(Routes.HomeBovinos.route)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Porcinos
            OpcionTipoAnimal(
                icono = Icons.Default.EmojiNature,
                titulo = stringResource(R.string.porcionos_name),
                seleccionado = tipoSeleccionado == stringResource(R.string.porcionos_name) && !borradoresSeleccionado,
                onClick = {
                    onTipoSeleccionado("Porcinos")
                    navController.navigate(Routes.HomePorcinos.route)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opción Borradores
            OpcionTipoAnimal(
                icono = Icons.Default.Drafts,
                titulo = "Borradores",
                seleccionado = borradoresSeleccionado,
                onClick = onBorradoresClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = WhiteBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Versión 1.0.0",
                        fontSize = 12.sp,
                        color = DarkBlueGrey
                    )
                    Text(
                        "© 2026 Terrabit",
                        fontSize = 10.sp,
                        color = BlueGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OpcionTipoAnimal(
    icono: ImageVector,
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (seleccionado) MainGreen.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (seleccionado) MainGreen else DarkBlueGrey
    val iconColor = if (seleccionado) MainGreen else BlueGrey

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icono,
                contentDescription = titulo,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                titulo,
                fontSize = 15.sp,
                fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))
            if (seleccionado) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = MainGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderBienvenida(
    tipoAnimal: String,
    onMenuClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(
                color = MainGreen
                /*
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2F6F4E),
                        Color(0xFF3F8F6B)
                    )
                )
                */
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Barra superior con menú y notificaciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de menú
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Botón de configuración
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White
                        )
                    }

                    // Menú Desplegable
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Castellano") },
                            onClick = {
                                expanded = false
                                cambiarIdioma("es")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Català") },
                            onClick = {
                                expanded = false
                                cambiarIdioma("ca")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto de bienvenida
            Text(
                stringResource(R.string.title_home),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tipo de animal seleccionado
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        tipoAnimal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}