package com.example.terrabit_app.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.navigation.Routes
import com.example.terrabit_app.ui.pantallas.*
import com.example.terrabit_app.ui.screen.PantallaRegistrarNacimiento
import com.example.terrabit_app.ui.screen.PantallaReportarMuerte

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

        composable(Routes.RegistrarNacimiento.ruta) {
            PantallaRegistrarNacimiento(navController = navController)
        }

        composable(Routes.ReportarMuerte.ruta) {
            PantallaReportarMuerte(navController = navController)
        }

        composable(Routes.GestionarGuias.ruta) {
            PantallaGestionarGuias(navController = navController)
        }

        composable(Routes.ConfirmarMovimientos.ruta) {
            PantallaConfirmarMovimientos(navController = navController)
        }

        composable(Rutas.SolicitarMaterial.ruta) {
            PantallaSolicitarMaterial(navController = navController)
        }
    }
}