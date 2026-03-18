package com.example.terrabit_app.ui.screen.bovinos

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.viewmodel.bovinos.ConfigurationViewModel
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.theme.BlueGrey
import com.example.terrabit_app.ui.theme.DarkBlueGrey
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.utils.CampoTexto
import com.example.terrabit_app.utils.PantallaCargaIdioma
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConfigurationScreen(
    onMenuClick: () -> Unit = {},
    navController: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: ConfigurationViewModel = hiltViewModel(activity)
    val isDark by viewModel.isDarkTheme.collectAsState()
    var expandedIdioma by remember { mutableStateOf(false) }
    var agregarMo by remember { mutableStateOf(false) }
    val codiMo by viewModel.codiMo.observeAsState("")
    val loading by viewModel.isLoading.observeAsState(false)
    val succes by viewModel.isSuccess.observeAsState(false)
    val error by viewModel.isError.observeAsState(false)
    var cambiandoIdioma by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (agregarMo) {
        Dialog(onDismissRequest = { agregarMo = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (loading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(64.dp), color = MainGreen, strokeWidth = 5.dp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Validando Codigo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlueGrey)
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        CampoTexto(
                            label = "Ingrese el código de la explotación",
                            valor = codiMo,
                            placeholder = "",
                            onValueChange = { viewModel.actualizarCodiMo(it) },
                            defectColor = true
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        if (succes) Text("Codigo valido", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MainGreen)
                        if (error) Text("Codigo invalido", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.resetState(); agregarMo = false },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueGrey)
                            ) { Text(stringResource(R.string.cancel_buttom)) }
                            Button(
                                onClick = { viewModel.verificarCodiMo() },
                                colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
                            ) { Text("Aceptar") }
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(MainGreen)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                ) {
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
                                onClick = { expandedIdioma = true },
                                modifier = Modifier.size(40.dp).background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = expandedIdioma,
                                onDismissRequest = { expandedIdioma = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Castellano") },
                                    onClick = {
                                        expandedIdioma = false
                                        val localeActual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                                        if (!localeActual.startsWith("es")) {
                                            scope.launch {
                                                cambiandoIdioma = true
                                                delay(300)
                                                cambiarIdioma("es")
                                                delay(300)
                                                cambiandoIdioma = false
                                            }
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Català") },
                                    onClick = {
                                        expandedIdioma = false
                                        val localeActual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                                        if (!localeActual.startsWith("ca")) {
                                            scope.launch {
                                                cambiandoIdioma = true
                                                delay(300)
                                                cambiarIdioma("ca")
                                                delay(300)
                                                cambiandoIdioma = false
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.configuration_title),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(text = stringResource(R.string.actual_user_title), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(MainGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = MainGreen, modifier = Modifier.size(32.dp))
                        }
                        Column {
                            Text(text = "NIF", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = viewModel.nif, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Text(text = "Añadir explotacíon", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { agregarMo = true }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(MainGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(32.dp))
                        }
                        Column {
                            Text(text = "Añadir nueva explotación", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Text(text = stringResource(R.string.appearance_title), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MainGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = MainGreen, modifier = Modifier.size(22.dp))
                            }
                            Column {
                                Text(text = stringResource(R.string.dark_theme_title), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = if (isDark) stringResource(R.string.activated_theme_subtitle) else stringResource(R.string.desactivated_theme_subtitle), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.toggleTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MainGreen,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }

        PantallaCargaIdioma(visible = cambiandoIdioma)
    }
}