package com.example.terrabit_app.navegacion

import Movimientos
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.pantallas.*
import com.example.terrabit_app.ui.screen.Fallecimiento
import com.example.terrabit_app.ui.screen.GestionGuias
import com.example.terrabit_app.ui.screen.Material
import com.example.terrabit_app.ui.screen.Nacimiento

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            Home(navController = navController)
        }

        composable(Routes.Nacimiento.route) {
            Nacimiento(navController = navController)
        }

        composable(Routes.Fallecimiento.route) {
            Fallecimiento(navController = navController)
        }

        composable(Routes.GestionGuias.route) {
            GestionGuias(navController = navController)
        }

        composable(Routes.Movimientos.route) {
            Movimientos(navController = navController)
        }

        composable(Routes.Material.route) {
            Material(navController = navController)
        }
    }
}