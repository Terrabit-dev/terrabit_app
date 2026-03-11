package com.example.terrabit_app.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.terrabit_app.ui.pantallas.BorradoresScreen
import com.example.terrabit_app.ui.pantallas.CrearGuiasPorcinos
import com.example.terrabit_app.ui.pantallas.GestionBovinos
import com.example.terrabit_app.ui.pantallas.GuiasMovimientos
import com.example.terrabit_app.ui.pantallas.GuiasMovimientosPorcinos
import com.example.terrabit_app.ui.pantallas.MaterialCategoria
import com.example.terrabit_app.ui.screen.HistorialScreen
import com.example.terrabit_app.ui.screen.bovinos.CorregirSexoBovi
import com.example.terrabit_app.ui.screen.bovinos.Fallecimiento
import com.example.terrabit_app.ui.screen.bovinos.GestionGuias
import com.example.terrabit_app.ui.screen.bovinos.Home
import com.example.terrabit_app.ui.screen.bovinos.IdentificacionApalzada
import com.example.terrabit_app.ui.screen.bovinos.ListarBovinos
import com.example.terrabit_app.ui.screen.bovinos.Material
import com.example.terrabit_app.ui.screen.bovinos.MaterialDuplicadosScreen
import com.example.terrabit_app.ui.screen.bovinos.Movimientos
import com.example.terrabit_app.ui.screen.bovinos.Nacimiento
import com.example.terrabit_app.ui.screen.porcinos.ConfirmarEditarGuiasPorci
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.ListaGuiasPorcinas
import com.example.terrabit_app.ui.screen.porcinos.HomePorcinos
import com.example.terrabit_app.ui.screen.porcinos.GestionGuiasPorcinos
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.ui.screen.bovinos.ConfigurationScreen
import com.example.terrabit_app.ui.screen.bovinos.UsbTestScreen
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationDrawer(
    bluetooth : BluetoothViewModel,
    navController: NavHostController,
    onMenuClick: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HomeBovinos.route
    ) {
        composable(Routes.Usb.route) {
            UsbTestScreen( navController = navController)
        }
        // ========== PANTALLAS CON HEADER PERSONALIZADO ==========

        // Pantalla principal Bovinos - CON header verde
        composable(Routes.HomeBovinos.route) {
            Home(
                tipoAnimalSeleccionado = "Bovinos",
                onMenuClick = onMenuClick,
                navController = navController
            )
        }

        // Pantalla principal Porcinos - CON header naranja
        composable(Routes.HomePorcinos.route) {
            HomePorcinos(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        // Borradores - CON botón de menú
        composable("borradores") {
            BorradoresScreen(
                viewModel = hiltViewModel(),
                onMenuClick = onMenuClick,
                navController = navController
            )
        }

        // Historial
        composable("historial"){
            HistorialScreen(
                viewModel = hiltViewModel(),
                onMenuClick = onMenuClick,
                navController = navController
            )
        }


        // Configuracion
        composable(Routes.Configuration.route) {
            ConfigurationScreen(
                onMenuClick = onMenuClick,
                navController = navController,
            )
        }

        // ========== PANTALLAS SIN HEADER PERSONALIZADO ==========
        // Estas pantallas NO necesitan onMenuClick

        // Listado de Bovinos
        composable(Routes.ListarBovinos.route) {
            ListarBovinos(navController)
        }

        // Gestión de Bovinos
        composable(Routes.GestionBovinos.route) {
            GestionBovinos(navController = navController)
        }

        // Guías y Movimientos
        composable(Routes.GuiasMovimientos.route) {
            GuiasMovimientos(navController = navController)
        }

        // Material Categoría
        composable(Routes.MaterialCategoria.route) {
            MaterialCategoria(navController = navController)
        }

        // Pantallas de categorías porcinos
        composable(Routes.GestionPorcinos.route) {
            GestionGuiasPorcinos(navController = navController)
        }

        composable(Routes.GuiasMovimientosPorcinos.route) {
            GuiasMovimientosPorcinos(navController = navController)
        }

        // Pantallas de acciones específicas
        composable(
            route = Routes.Nacimiento.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            Nacimiento(navController = navController, bluetooth, borradorId)
        }

        composable(
            route = Routes.Fallecimiento.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            Fallecimiento(navController = navController, bluetooth, borradorId)
        }

        composable(
            route = Routes.GestionGuias.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            GestionGuias(navController = navController, bluetooth, borradorId)
        }

        composable(
            route = Routes.Movimientos.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            Movimientos(navController = navController, bluetooth, borradorId)
        }

        composable(
            route = Routes.Material.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            Material(navController = navController, borradorId = borradorId)
        }

        composable(
            route = Routes.MaterialDuplicado.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            MaterialDuplicadosScreen(navController = navController, bluetoothViewModel = bluetooth, borradorId = borradorId)
        }

        composable(
            route = Routes.CorregirBovino.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            CorregirSexoBovi(navController, bluetooth, borradorId)
        }

        composable(
            route = Routes.IdentificacionAplazada.route,
            arguments = listOf(navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            IdentificacionApalzada(navController, bluetooth, borradorId)
        }

        // Pantallas Porcinos
        composable(Routes.GestionGuiasPorcinos.route) {
            val viewModelGestionarGuias = viewModel<GestionarGuiasViewModel>(it)
            val viewModelEditarGuias = viewModel<EditarGuiaPorcinosViewModel>(it)
            ListaGuiasPorcinas(
                navController = navController,
                viewModelGestionarGuias = viewModelGestionarGuias,
                viewModelEditarGuias = viewModelEditarGuias
            )
        }

        composable(Routes.EntradasPorcinos.route) {
            EntradasPorcinos(navController = navController)
        }

        composable(Routes.CrearGuiasPorcinos.route){
            CrearGuiasPorcinos(navController = navController)
        }
        composable(Routes.EditarGuiaPorcinos.route) { currentEntry ->
            val parentEntry = remember(currentEntry) {
                navController.getBackStackEntry(Routes.GestionGuiasPorcinos.route)
            }
            val viewModelGestionarGuias = viewModel<GestionarGuiasViewModel>(parentEntry)
            val viewModelEditarGuias = viewModel<EditarGuiaPorcinosViewModel>(parentEntry)
            ConfirmarEditarGuiasPorci(
                navController = navController,
                viewModelGestionarGuias = viewModelGestionarGuias,
                viewModelEditarGuias = viewModelEditarGuias
            )
        }
    }
}
