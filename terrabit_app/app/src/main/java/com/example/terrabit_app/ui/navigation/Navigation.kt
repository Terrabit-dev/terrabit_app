package com.example.terrabit_app.navegacion

import Movimientos
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.pantallas.*
import com.example.terrabit_app.ui.screen.CorregirSexoBovi
import com.example.terrabit_app.ui.screen.Fallecimiento
import com.example.terrabit_app.ui.screen.GestionGuias
import com.example.terrabit_app.ui.screen.Material
import com.example.terrabit_app.ui.screen.Nacimiento
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.MainViewmodel
import okhttp3.Route

@Composable
fun Navigation(myViewmodel: MainViewmodel, drawerViewModel: DrawerViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        // Pantalla principal
        composable(Routes.Home.route) {
            Home(navController = navController, drawerViewModel)
        }

        // Pantallas de categorías (intermedias)
        composable(Routes.GestionBovinos.route) {
            GestionBovinos(navController = navController)
        }

        composable(Routes.GuiasMovimientos.route) {
            GuiasMovimientos(navController = navController)
        }

        composable(Routes.MaterialCategoria.route) {
            MaterialCategoria(navController = navController)
        }

        // Pantallas de acciones específicas (las que ya tenías)
        composable(Routes.Nacimiento.route) {
            Nacimiento(navController = navController, myViewmodel)
        }

        composable(Routes.Fallecimiento.route) {
            Fallecimiento(navController = navController, myViewmodel)
        }

        composable(Routes.GestionGuias.route) {
            GestionGuias(navController = navController)
        }

        composable(Routes.Movimientos.route) {
            Movimientos(navController = navController)
        }

        composable(Routes.Material.route) {
            Material(navController = navController, myViewmodel)
        }
        composable(Routes.CorregirBovino.route) {
            CorregirSexoBovi(navController, myViewmodel)
        }
    }
}