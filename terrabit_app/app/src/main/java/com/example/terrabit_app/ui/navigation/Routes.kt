package com.example.terrabit_app.ui.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Nacimiento : Routes("register_birth")
    object Fallecimiento : Routes("report_death")
    object GestionGuias : Routes("manage_guides")
    object Movimientos : Routes("confirm_movements")
    object Material : Routes("request_material")
}