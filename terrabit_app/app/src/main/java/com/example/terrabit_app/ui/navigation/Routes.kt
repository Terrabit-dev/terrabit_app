package com.example.terrabit_app.ui.navigation

sealed class Routes(val route: String) {


    object Login : Routes("login")

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
    object Nacimiento : Routes("register_birth?borradorId={borradorId}") {
        fun conBorrador(id: String) = "register_birth?borradorId=$id"
    }
    object Fallecimiento : Routes("report_death?borradorId={borradorId}") {
        fun conBorrador(id: String) = "report_death?borradorId=$id"
    }
    object GestionGuias : Routes("manage_guides?borradorId={borradorId}") {
        fun conBorrador(id: String) = "manage_guides?borradorId=$id"
    }

    object Movimientos : Routes("confirm_movements?borradorId={borradorId}") {
        fun conBorrador(id: String) = "confirm_movements?borradorId=$id"
    }

    object Material : Routes("request_material?borradorId={borradorId}") {
        fun conBorrador(id: String) = "request_material?borradorId=$id"
    }

    object MaterialDuplicado : Routes("material_duplicado?borradorId={borradorId}") {
        fun conBorrador(id: String) = "material_duplicado?borradorId=$id"
    }

    object CorregirBovino : Routes("modificar_animal?borradorId={borradorId}") {
        fun conBorrador(id: String) = "modificar_animal?borradorId=$id"
    }
    object IdentificacionAplazada : Routes("IdentificacioAplaz?borradorId={borradorId}") {
        fun conBorrador(id: String) = "IdentificacioAplaz?borradorId=$id"
    }

    // Screens Porcinos
    object GestionGuiasPorcinos : Routes("gestion_guias_porcinos")
    object EntradasPorcinos : Routes("entradas_porcinos")
    object EditarGuiaPorcinos : Routes("editar_guia_porcinos")

    object CrearGuiasPorcinos : Routes("crear_guias_porcinos")

    object GestionarGuiasPorcinos : Routes("editar_confirmar_guias_porcin")


    object Drawer : Routes("drawer_screen")
    object Configuration : Routes("configuration")

    object Historial : Routes("historial")
    object  Usb : Routes("usb")
}