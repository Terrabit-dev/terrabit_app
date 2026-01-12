package com.example.terrabit_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.ui.screen.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Routes.RegisterBirth.route) {
            RegisterBirthScreen(navController = navController)
        }

        composable(Routes.ReportDeath.route) {
            ReportDeathScreen(navController = navController)
        }

        composable(Routes.ManageGuides.route) {
            ManageGuidesScreen(navController = navController)
        }

        composable(Routes.ConfirmMovements.route) {
            ConfirmMovementsScreen(navController = navController)
        }

        composable(Routes.RequestMaterial.route) {
            RequestMaterialScreen(navController = navController)
        }
    }
}