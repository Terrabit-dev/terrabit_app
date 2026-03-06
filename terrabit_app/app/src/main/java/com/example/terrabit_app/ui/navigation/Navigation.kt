package com.example.terrabit_app.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.screen.DrawerScreen
import com.example.terrabit_app.ui.screen.Login
import com.example.terrabit_app.utils.UserPreferences
import com.example.terrabit_app.viewmodel.DrawerViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    bluetooth: BluetoothViewModel,
    drawerViewModel: DrawerViewModel
) {
    val mainNavController = rememberNavController()
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val haySesionActiva = remember {
        userPreferences.getRememberMe() &&
                !userPreferences.getNif().isNullOrEmpty() &&
                !userPreferences.getPassword().isNullOrEmpty()
    }

    val startDestination = if (haySesionActiva) Routes.Drawer.route else Routes.Login.route

    NavHost(navController = mainNavController, startDestination = startDestination) {
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