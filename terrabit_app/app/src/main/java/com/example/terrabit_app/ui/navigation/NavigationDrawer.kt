package com.example.terrabit_app.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
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
import com.example.terrabit_app.ui.screen.bovinos.CorregirSexoBovi
import com.example.terrabit_app.ui.screen.bovinos.Fallecimiento
import com.example.terrabit_app.ui.screen.bovinos.GestionGuias
import com.example.terrabit_app.ui.screen.bovinos.Home
import com.example.terrabit_app.ui.screen.bovinos.IdentificacionApalzada
import com.example.terrabit_app.ui.screen.bovinos.ListarBovinos
import com.example.terrabit_app.ui.screen.bovinos.Material
import com.example.terrabit_app.ui.screen.bovinos.MaterialDupplicadosScreen
import com.example.terrabit_app.ui.screen.bovinos.Movimientos
import com.example.terrabit_app.ui.screen.bovinos.Nacimiento
import com.example.terrabit_app.ui.screen.porcinos.EditarGuiaPorcinos
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.GestionGuiasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.HomePorcinos
import com.example.terrabit_app.ui.screen.porcinos.PorcinosGestionGuias
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.viewmodel.BorradorViewModel
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.IdentificacionAplazaViewModel
import com.example.terrabit_app.viewmodel.ListarBovinosViewModel
import com.example.terrabit_app.viewmodel.MaterialViewModel
import com.example.terrabit_app.viewmodel.porcinos.EditarGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationDrawer(
    bluetooth : BluetoothViewModel,
    navController: NavHostController,
    onMenuClick: () -> Unit,
    drawerViewModel: DrawerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HomeBovinos.route
    ) {
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
                onMenuClick = onMenuClick,
            )
        }

        // Borradores - CON botón de menú
        composable("borradores") {
            val borradorViewModel: BorradorViewModel = viewModel()
            BorradoresScreen(
                viewModel = borradorViewModel,
                onMenuClick = onMenuClick,
                navController = navController
            )
        }

        // ========== PANTALLAS SIN HEADER PERSONALIZADO ==========
        // Estas pantallas NO necesitan onMenuClick

        // Listado de Bovinos
        composable(Routes.ListarBovinos.route) {
            val viewmodel: ListarBovinosViewModel = viewModel()
            ListarBovinos(navController = navController, viewmodel)
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
            PorcinosGestionGuias(navController = navController)
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

        composable(Routes.Material.route) {
            val materialV: MaterialViewModel = viewModel()
            Material(navController = navController, materialV)
        }

        composable(Routes.MaterialDuplicado.route){
            MaterialDupplicadosScreen(navController)
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
            val identificacion: IdentificacionAplazaViewModel = viewModel()
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            IdentificacionApalzada(navController, bluetooth, borradorId)
        }

        // Pantallas Porcinos
        composable(Routes.GestionGuiasPorcinos.route) {
            GestionGuiasPorcinos(
                navController = navController
            )
        }

        composable(Routes.EntradasPorcinos.route) {
            EntradasPorcinos(navController = navController)
        }

        composable(Routes.CrearGuiasPorcinos.route){
            CrearGuiasPorcinos(navController = navController)
        }
        composable(Routes.EditarGuiaPorcinos.route){
            EditarGuiaPorcinos(navController = navController)
        }
    }
}
