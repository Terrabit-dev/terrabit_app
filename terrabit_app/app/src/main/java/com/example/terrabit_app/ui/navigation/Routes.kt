package com.example.terrabit_app.ui.navigation

sealed class Routes(val route: String) {

    // Home Bovinos
    object HomeBovinos : Routes("home_bovinos")

    // Home Porcinos
    object HomePorcinos : Routes("home_porcinos")

    // Organización Bovinos
    object GestionBovinos : Routes("gestion_bovinos")
    object GuiasMovimientos : Routes("guias_movimientos")
    object ListarBovinos : Routes("listar_bovinos")

    // Organización Porcinos
    object GestionPorcinos : Routes("gestion_porcinos")
    object GuiasMovimientosPorcinos : Routes("guias_movimientos_porcinos")

    // Organización General
    object MaterialCategoria : Routes("material_menu")

    // Screens
    object Nacimiento : Routes("register_birth")
    object Fallecimiento : Routes("report_death")
    object GestionGuias : Routes("manage_guides")
    object Movimientos : Routes("confirm_movements")
    object Material : Routes("request_material")
    object Login : Routes("login")
    object CorregirBovino :Routes("modificar_animal")
    object IdentificacionAplazada : Routes("IdentificacioAplaz")

    // Screens Porcinos
    object GestionGuiasPorcinos : Routes("gestion_guias_porcinos")
    object EntradasPorcinos : Routes("entradas_porcinos")
}