package com.example.terrabit_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.screen.DrawerScreen
import com.example.terrabit_app.ui.screen.Login
import com.example.terrabit_app.utils.UserPreferences
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.MainViewmodel
import androidx.compose.ui.platform.LocalContext

@Composable
fun Navigation(myViewmodel: MainViewmodel, drawerViewModel: DrawerViewModel) {
    val mainNavController = rememberNavController()
    val context = LocalContext.current

    // Leer si hay sesión guardada
    val userPreferences = remember { UserPreferences(context) }
    val haySesionActiva = remember {
        userPreferences.getRememberMe() &&
                !userPreferences.getNif().isNullOrEmpty() &&
                !userPreferences.getPassword().isNullOrEmpty()
    }

    // Arrancar en DrawerScreen si hay sesión, o en Login si no
    val startDestination = if (haySesionActiva) Routes.Drawer.route else Routes.Login.route

    NavHost(
        navController = mainNavController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            Login(navController = mainNavController)
        }

        composable(Routes.Drawer.route) {
            DrawerScreen(
                mainNavController = mainNavController,
                drawerViewModel = drawerViewModel,
                mainViewModel = myViewmodel
            )
        }
    }
}
