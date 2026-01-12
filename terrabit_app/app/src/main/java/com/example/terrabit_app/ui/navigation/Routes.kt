package com.example.terrabit_app.ui.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object RegisterBirth : Routes("register_birth")
    object ReportDeath : Routes("report_death")
    object ManageGuides : Routes("manage_guides")
    object ConfirmMovements : Routes("confirm_movements")
    object RequestMaterial : Routes("request_material")
}