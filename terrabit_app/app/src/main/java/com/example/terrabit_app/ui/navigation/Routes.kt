package com.example.terrabit_app.ui.navigation

sealed class Routes(val route: String) {

    object Home : Routes("home")

    // Organizacion
    object GestionBovinos : Routes("gestion_bovinos")
    object GuiasMovimientos : Routes("guias_movimientos")
    object MaterialCategoria : Routes("material_menu")


    // Screens
    object Nacimiento : Routes("register_birth")
    object Fallecimiento : Routes("report_death")
    object GestionGuias : Routes("manage_guides")
    object Movimientos : Routes("confirm_movements")
    object Material : Routes("request_material")
}