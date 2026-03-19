package com.example.terrabit_app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.terrabit_app.R
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.theme.ErrorRed
import com.example.terrabit_app.ui.theme.MainGreen
import com.example.terrabit_app.viewmodel.bovinos.LoginState
import com.example.terrabit_app.viewmodel.bovinos.LoginViewModel

@Composable
fun Login(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    var mostrarDialogoError by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate(Routes.Drawer.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
            is LoginState.Error -> {
                mensajeError = (loginState as LoginState.Error).message
                mostrarDialogoError = true
            }
            else -> {}
        }
    }

    if (mostrarDialogoError && mensajeError.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoError = false; viewModel.resetState() },
            icon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = MainGreen, modifier = Modifier.size(48.dp)) },
            title = { Text(stringResource(R.string.title_auth_error), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(mensajeError, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoError = false; viewModel.resetState() },
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.error_buttom), fontWeight = FontWeight.SemiBold) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (loginState is LoginState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = MainGreen, strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.loading_processing), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Image(
                    painter = painterResource(R.drawable.terrabit_prime_sin_letra),
                    contentDescription = "App logo",
                    modifier = Modifier.size(80.dp)
                )
                Text("Terrabit", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(stringResource(R.string.subtitle_login), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LoginCard(viewModel, loginState, uriHandler)
                Text(stringResource(R.string.footer_copyright), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(115.dp))
            }
        }
    }
}

@Composable
fun LoginCard(
    viewModel: LoginViewModel,
    loginState: LoginState,
    uriHandler: UriHandler
) {
    val savedNif by viewModel.savedNif.collectAsState()
    val savedPassword by viewModel.savedPassword.collectAsState()
    val savedCodiMO by viewModel.savedCodiMO.collectAsState()
    val savedRememberMe by viewModel.savedRememberMe.collectAsState()

    var nif by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var codiMO by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var credencialesCargadas by remember { mutableStateOf(false) }

    val nifError by viewModel.nifError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val codiMOError by viewModel.codiMOError.collectAsState()

    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    val codiMOFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (!credencialesCargadas) {
            savedNif?.let { nif = it }
            savedPassword?.let { password = it }
            savedCodiMO?.let { codiMO = it }
            rememberMe = savedRememberMe
            credencialesCargadas = true
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(stringResource(R.string.title_login), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)

                Text(stringResource(R.string.label_user_code), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                CustomOutlinedTextField(
                    value = nif,
                    onValueChange = { nif = it; viewModel.clearFieldError("nif") },
                    placeholder = stringResource(R.string.hint_user_code),
                    icon = Icons.Outlined.AccountCircle,
                    isError = nifError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next, autoCorrect = false),
                    keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() })
                )
                if (nifError != null) {
                    Text(nifError ?: "", color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                }

                Text(stringResource(R.string.label_password_mobility), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it; viewModel.clearFieldError("password") },
                    placeholder = stringResource(R.string.hint_password_mobility),
                    icon = Icons.Outlined.Lock,
                    isPassword = true,
                    isError = passwordError != null,
                    focusRequester = passwordFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next, autoCorrect = false),
                    keyboardActions = KeyboardActions(onNext = { codiMOFocusRequester.requestFocus() })
                )
                if (passwordError != null) {
                    Text(passwordError ?: "", color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                }

                Text(stringResource(R.string.label_codimo), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                CustomOutlinedTextField(
                    value = codiMO,
                    onValueChange = { codiMO = it; viewModel.clearFieldError("codiMO") },
                    placeholder = stringResource(R.string.hint_codimo),
                    icon = Icons.Outlined.Badge,
                    isError = codiMOError != null,
                    focusRequester = codiMOFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done, autoCorrect = false),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.login(nif, password, codiMO, rememberMe) })
                )
                if (codiMOError != null) {
                    Text(codiMOError ?: "", color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CheckboxWithText(isChecked = rememberMe, onCheckedChange = { rememberMe = it })
                Text(
                    text = stringResource(R.string.action_forgot_password),
                    fontWeight = FontWeight.Bold,
                    color = MainGreen,
                    textAlign = TextAlign.End,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://aplicacions.agricultura.gencat.cat/gtr/porci/AppJava/views/recuperarContrasenya.xhtml")
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.login(nif, password, codiMO, rememberMe) },
                    enabled = loginState !is LoginState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.btn_login), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    isError: Boolean = false,
    focusRequester: FocusRequester? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MainGreen,
            focusedLeadingIconColor = MainGreen,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            errorIndicatorColor = ErrorRed,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            errorLeadingIconColor = ErrorRed
        )
    )
}

@Composable
fun CheckboxWithText(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(stringResource(R.string.label_remember_me), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}