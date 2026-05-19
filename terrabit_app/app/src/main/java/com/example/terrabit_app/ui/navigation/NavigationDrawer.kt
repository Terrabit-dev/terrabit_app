package com.example.terrabit_app.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.terrabit_app.R
import com.example.terrabit_app.data.network.movimientos.modelos.Moviment
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
import com.example.terrabit_app.ui.screen.bovinos.DetailBovino
import com.example.terrabit_app.ui.screen.bovinos.EditarGuiaBovi
import com.example.terrabit_app.ui.screen.bovinos.ListaGuiasBovi
import com.example.terrabit_app.ui.screen.bovinos.ListarMovimientosBovi

import com.example.terrabit_app.viewmodel.bovinos.ListarBovinosViewModel
import com.example.terrabit_app.viewmodel.bovinos.ListarGuiasBoviViewModel
import com.example.terrabit_app.viewmodel.bovinos.ListarMovisBoviViewModel
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

        // ========== PANTALLAS CON HEADER PERSONALIZADO ==========

        composable(Routes.HomeBovinos.route) {
            Home(
                tipoAnimalSeleccionado = stringResource(R.string.bovinos_name),
                onMenuClick = onMenuClick,
                navController = navController
            )
        }

        composable(Routes.HomePorcinos.route) {
            HomePorcinos(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable("borradores") {
            BorradoresScreen(
                viewModel = hiltViewModel(),
                onMenuClick = onMenuClick,
                navController = navController
            )
        }

        composable("historial"){
            HistorialScreen(
                viewModel = hiltViewModel(),
                onMenuClick = onMenuClick,
                navController = navController
            )
        }


        composable(Routes.Configuration.route) {
            ConfigurationScreen(
                onMenuClick = onMenuClick,
                navController = navController,
            )
        }

        // ========== PANTALLAS SIN HEADER PERSONALIZADO ==========
        // Estas pantallas NO necesitan onMenuClick

        composable(Routes.ListarBovinos.route) {
            ListarBovinos(navController)
        }
        composable(Routes.DeatilBovino.route) { currentEntry ->
            val parentEntry = remember(currentEntry) {
                navController.getBackStackEntry(Routes.ListarBovinos.route)
            }
            val viewModelLista = hiltViewModel<ListarBovinosViewModel>(parentEntry)
            val animal by viewModelLista.animalSeleccionado.observeAsState(null)

            animal?.let {
                DetailBovino(navController = navController, animal = it)
            }
        }

        composable(Routes.GestionBovinos.route) {
            GestionBovinos(navController = navController)
        }

        composable(Routes.GuiasMovimientos.route) {
            GuiasMovimientos(navController = navController)
        }

        composable(Routes.MaterialCategoria.route) {
            MaterialCategoria(navController = navController)
        }

        composable(Routes.GestionPorcinos.route) {
            GestionGuiasPorcinos(navController = navController)
        }

        composable(Routes.GuiasMovimientosPorcinos.route) {
            GuiasMovimientosPorcinos(navController = navController)
        }


        composable(
            route = Routes.Nacimiento.route,
            arguments = listOf(
                navArgument("borradorId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            Nacimiento(navController = navController, bluetooth, borradorId, historialId)
        }

        composable(
            route = Routes.Fallecimiento.route,

            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            Fallecimiento(navController = navController, bluetooth, borradorId, historialId)
        }


        composable(
            route = Routes.GestionGuias.route,
            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            GestionGuias(navController = navController, bluetooth, borradorId, historialId)
        }

        composable(
            route = Routes.Movimientos.route,
            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            Movimientos(navController = navController, bluetooth, borradorId, historialId, Moviment())
        }
        composable(Routes.ConfirmarMovimientoBovi.route) { currentEntry ->
            val parentEntry = remember(currentEntry) {
                navController.getBackStackEntry(Routes.MovimientosBovinos.route)
            }
            val viewModelLista = hiltViewModel<ListarMovisBoviViewModel>(parentEntry)
            val movimientoSeleccionado by viewModelLista.movimientoSeleccionado.observeAsState(null)

            movimientoSeleccionado?.let { movimiento ->
                Movimientos(
                    navController = navController,
                    bluetoothViewModel = bluetooth,
                    borradorId = "",
                    historialId = "",
                    movimientoSeleccionado = movimiento
                )
            }
        }

        composable(
            route = Routes.Material.route,
            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            Material(navController = navController, borradorId = borradorId, historialId)
        }

        composable(
            route = Routes.MaterialDuplicado.route,
            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }

            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            MaterialDuplicadosScreen(navController = navController, bluetoothViewModel = bluetooth, borradorId = borradorId, historialId)
        }

        composable(
            route = Routes.CorregirBovino.route,
            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            CorregirSexoBovi(navController, bluetooth, borradorId, historialId)
        }


        composable(
            route = Routes.IdentificacionAplazada.route,
            arguments = listOf(
                navArgument("borradorId") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true },

                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            IdentificacionApalzada(navController, bluetooth, borradorId, historialId)
        }

        composable(Routes.GestionGuiasPorcinos.route) {
            val viewModelGestionarGuias = hiltViewModel<GestionarGuiasViewModel>(it)
            val viewModelEditarGuias    = hiltViewModel<EditarGuiaPorcinosViewModel>(it)
            ListaGuiasPorcinas(
                navController           = navController,
                viewModelGestionarGuias = viewModelGestionarGuias,
                viewModelEditarGuias    = viewModelEditarGuias
            )
        }

        composable(Routes.EntradasPorcinos.route) {
            EntradasPorcinos(navController = navController)
        }
        composable(Routes.GuiasBovinos.route) {
            val viewModel = hiltViewModel<ListarGuiasBoviViewModel>(it)   // ViewModel vive en esta entrada
            ListaGuiasBovi(
                navController = navController,
                viewModel     = viewModel
            )
        }
        composable(Routes.MovimientosBovinos.route) {
            val viewModel = hiltViewModel<ListarMovisBoviViewModel>(it)   // ViewModel vive en esta entrada
            ListarMovimientosBovi(
                navController = navController,
                viewModel     = viewModel
            )
        }

        composable(Routes.EditarGuiaBovi.route) { currentEntry ->
            val parentEntry = remember(currentEntry) {
                navController.getBackStackEntry(Routes.GuiasBovinos.route)
            }
            val viewModelLista = hiltViewModel<ListarGuiasBoviViewModel>(parentEntry)
            val guiaSeleccionada by viewModelLista.guiaSeleccionada.observeAsState(null)
            guiaSeleccionada?.let { guia ->
                EditarGuiaBovi(
                    navController     = navController,
                    guiaSeleccionada  = guia
                )
            }
        }

        composable(
            route = Routes.CrearGuiasPorcinos.route,
            arguments = listOf(
                navArgument("borradorId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("historialId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val borradorId = backStackEntry.arguments?.getString("borradorId") ?: ""
            val historialId = backStackEntry.arguments?.getString("historialId") ?: ""
            CrearGuiasPorcinos(navController = navController, borradorId = borradorId, historialId = historialId)
        }

        composable(Routes.EditarGuiaPorcinos.route) { currentEntry ->
            val parentEntry = remember(currentEntry) {
                navController.getBackStackEntry(Routes.GestionGuiasPorcinos.route)
            }
            val viewModelGestionarGuias = hiltViewModel<GestionarGuiasViewModel>(parentEntry)
            val viewModelEditarGuias    = hiltViewModel<EditarGuiaPorcinosViewModel>(parentEntry)
            ConfirmarEditarGuiasPorci(
                navController           = navController,
                viewModelGestionarGuias = viewModelGestionarGuias,
                viewModelEditarGuias    = viewModelEditarGuias
            )
        }
    }
}
