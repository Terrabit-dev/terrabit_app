package com.example.terrabit_app.ui.screen.porcinos

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.bovinos.cambiarIdioma
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainOrange
import com.example.terrabit_app.utils.PantallaCargaIdioma
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePorcinos(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    var cambiandoIdioma by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderBienvenidaPorcinos(
                    onMenuClick = onMenuClick,
                    onCambiarIdioma = { idioma ->
                        val localeActual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                        if (!localeActual.startsWith(idioma)) {
                            scope.launch {
                                cambiandoIdioma = true
                                delay(300)
                                cambiarIdioma(idioma)
                                delay(300)
                                cambiandoIdioma = false
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.subtitle_home),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaMenuPorcinos(
                        icono = Icons.Default.Agriculture,
                        titulo = "Gestión de Guias",
                        descripcion = stringResource(R.string.card_description_crear_guias_porcinos),
                        colorFondo = MainOrange,
                        onClick = { navController.navigate(Routes.GestionPorcinos.route) }
                    )
                    TarjetaMenuPorcinos(
                        icono = Icons.Default.ShoppingCart,
                        titulo = "Gestion de movimientos",
                        descripcion = stringResource(R.string.card_description_material),
                        colorFondo = MainOrange,
                        onClick = { navController.navigate(Routes.GuiasMovimientosPorcinos.route) }
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        PantallaCargaIdioma(visible = cambiandoIdioma)
    }
}

@Composable
fun TarjetaMenuPorcinos(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    colorFondo: Color,
    contadorBadge: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(shape = RoundedCornerShape(12.dp), color = colorFondo, modifier = Modifier.size(70.dp), shadowElevation = 2.dp) {
                    Icon(icono, contentDescription = titulo, tint = Color.White, modifier = Modifier.fillMaxSize().padding(16.dp))
                }
                if (contadorBadge != null) {
                    Badge(containerColor = ErrorRed, modifier = Modifier.offset(x = 4.dp, y = (-4).dp)) {
                        Text(contadorBadge.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface, letterSpacing = 0.2.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(descripcion, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Ver más", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun HeaderBienvenidaPorcinos(
    onMenuClick: () -> Unit,
    onCambiarIdioma: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(color = MainOrange)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                }

                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Idioma", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.language_cas)) },
                            onClick = { expanded = false; onCambiarIdioma("es") }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.language_cat)) },
                            onClick = { expanded = false; onCambiarIdioma("ca") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = stringResource(R.string.title_home), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.EmojiNature, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.porcionos_name), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}