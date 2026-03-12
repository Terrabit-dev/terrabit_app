package com.example.terrabit_app.ui.navigation

sealed class Routes(val route: String) {

    object Login : Routes("login")

    object HomeBovinos : Routes("home_bovinos")
    object HomePorcinos : Routes("home_porcinos")

    object GestionBovinos : Routes("gestion_bovinos")
    object GuiasMovimientos : Routes("guias_movimientos")
    object ListarBovinos : Routes("listar_bovinos")

    object GestionPorcinos : Routes("gestion_porcinos")
    object GuiasMovimientosPorcinos : Routes("guias_movimientos_porcinos")

    object MaterialCategoria : Routes("material_menu")

    object Nacimiento : Routes("register_birth?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "register_birth?borradorId=&historialId="
        fun conBorrador(id: String) = "register_birth?borradorId=$id&historialId="
        fun conHistorial(id: String) = "register_birth?borradorId=&historialId=$id"
    }

    object Fallecimiento : Routes("report_death?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "report_death?borradorId=&historialId="
        fun conBorrador(id: String) = "report_death?borradorId=$id&historialId="
        fun conHistorial(id: String) = "report_death?borradorId=&historialId=$id"
    }

    object GestionGuias : Routes("manage_guides?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "manage_guides?borradorId=&historialId="
        fun conBorrador(id: String) = "manage_guides?borradorId=$id&historialId="
        fun conHistorial(id: String) = "manage_guides?borradorId=&historialId=$id"
    }

    object Movimientos : Routes("confirm_movements?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "confirm_movements?borradorId=&historialId="
        fun conBorrador(id: String) = "confirm_movements?borradorId=$id&historialId="
        fun conHistorial(id: String) = "confirm_movements?borradorId=&historialId=$id"
    }

    object CorregirBovino : Routes("modificar_animal?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "modificar_animal?borradorId=&historialId="
        fun conBorrador(id: String) = "modificar_animal?borradorId=$id&historialId="
        fun conHistorial(id: String) = "modificar_animal?borradorId=&historialId=$id"
    }

    object Material : Routes("request_material?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "request_material?borradorId=&historialId="
        fun conBorrador(id: String) = "request_material?borradorId=$id&historialId="
        fun conHistorial(id: String) = "request_material?borradorId=&historialId=$id"
    }

    object MaterialDuplicado : Routes("material_duplicado?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "material_duplicado?borradorId=&historialId="
        fun conBorrador(id: String) = "material_duplicado?borradorId=$id&historialId="
        fun conHistorial(id: String) = "material_duplicado?borradorId=&historialId=$id"
    }

    object IdentificacionAplazada : Routes("IdentificacioAplaz?borradorId={borradorId}&historialId={historialId}") {
        fun nuevo() = "IdentificacioAplaz?borradorId=&historialId="
        fun conBorrador(id: String) = "IdentificacioAplaz?borradorId=$id&historialId="
        fun conHistorial(id: String) = "IdentificacioAplaz?borradorId=&historialId=$id"
    }

    object GestionGuiasPorcinos : Routes("gestion_guias_porcinos")
    object EntradasPorcinos : Routes("entradas_porcinos")
    object EditarGuiaPorcinos : Routes("editar_guia_porcinos")
    object CrearGuiasPorcinos : Routes("crear_guias_porcinos")
    object GestionarGuiasPorcinos : Routes("editar_confirmar_guias_porcin")

    object Drawer : Routes("drawer_screen")
    object Configuration : Routes("configuration")
    object Historial : Routes("historial")
    object Usb : Routes("usb")
}