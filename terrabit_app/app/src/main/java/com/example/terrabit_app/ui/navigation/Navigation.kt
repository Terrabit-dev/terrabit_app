package com.example.terrabit_app.navegacion


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.screen.DrawerScreen
import com.example.terrabit_app.ui.screen.Login
import com.example.terrabit_app.ui.screen.bovinos.Movimientos
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.GestionGuiasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.HomePorcinos
import com.example.terrabit_app.viewmodel.BorradorViewModel
import com.example.terrabit_app.viewmodel.CorrecionSexoViewModel
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.MainViewmodel


@Composable
fun Navigation(myViewmodel: MainViewmodel, drawerViewModel: DrawerViewModel) {
    val mainNavController = rememberNavController()

    NavHost(
        navController = mainNavController,
        startDestination = Routes.Login.route
    ) {
        // Pantalla de Login
        composable(Routes.Login.route) {
            Login(navController = mainNavController)
        }

        // Pantalla con Drawer (contiene toda la navegación interna)
        composable("drawer_screen") {
            DrawerScreen(
                mainNavController = mainNavController,
                drawerViewModel = drawerViewModel,
                mainViewModel = myViewmodel
            )
        }
    }
}