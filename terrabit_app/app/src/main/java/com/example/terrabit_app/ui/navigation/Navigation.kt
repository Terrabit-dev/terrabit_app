package com.example.terrabit_app.ui.navigation

import com.example.terrabit_app.SplashScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.screen.DrawerScreen
import com.example.terrabit_app.ui.screen.Login
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.terrabit_app.viewmodel.bovinos.DrawerViewModel
import com.example.terrabit_app.viewmodel.bovinos.NavigationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    bluetooth: BluetoothViewModel,
    drawerViewModel: DrawerViewModel
) {
    val mainNavController = rememberNavController()
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    var haySesionActiva by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        haySesionActiva = navigationViewModel.haySesionActiva()
    }

    if (haySesionActiva == null) return

    NavHost(navController = mainNavController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    val destino = if (haySesionActiva == true) Routes.Drawer.route else Routes.Login.route
                    mainNavController.navigate(destino) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Login.route) {
            Login(navController = mainNavController)
        }
        composable(Routes.Drawer.route) {
            DrawerScreen(
                bluetooth = bluetooth,
                mainNavController = mainNavController,
                drawerViewModel = drawerViewModel
            )
        }
    }
}