package com.example.terrabit_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.terrabit_app.ui.pantallas.BorradoresScreen
import com.example.terrabit_app.ui.pantallas.GestionBovinos
import com.example.terrabit_app.ui.screen.porcinos.GestionPorcinos
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
import com.example.terrabit_app.ui.screen.bovinos.Movimientos
import com.example.terrabit_app.ui.screen.bovinos.Nacimiento
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.GestionGuiasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.HomePorcinos
import com.example.terrabit_app.viewmodel.BorradorViewModel
import com.example.terrabit_app.viewmodel.CorrecionSexoViewModel
import com.example.terrabit_app.viewmodel.DrawerViewModel
import com.example.terrabit_app.viewmodel.GuiasViewModel
import com.example.terrabit_app.viewmodel.IdentificacionAplazaViewModel
import com.example.terrabit_app.viewmodel.ListarBovinosViewModel
import com.example.terrabit_app.viewmodel.MainViewmodel
import com.example.terrabit_app.viewmodel.MaterialViewModel
import com.example.terrabit_app.viewmodel.MovimientosViewModel
import com.example.terrabit_app.viewmodel.NacimientoViewmodel
import com.example.terrabit_app.viewmodel.ViewModelMuerteBovi

@Composable
fun NavigationDrawer(
    myViewmodel: MainViewmodel,
    drawerViewModel: DrawerViewModel,
    navController: NavHostController,
    onMenuClick: () -> Unit
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
                onMenuClick = onMenuClick  // Solo necesita esto
            )
        }

        // Borradores - CON botón de menú
        composable("borradores") {
            val borradorViewModel: BorradorViewModel = viewModel()
            BorradoresScreen(
                viewModel = borradorViewModel,
                onMenuClick = onMenuClick
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
            GestionPorcinos(navController = navController)
        }

        composable(Routes.GuiasMovimientosPorcinos.route) {
            GuiasMovimientosPorcinos(navController = navController)
        }

        // Pantallas de acciones específicas
        composable(Routes.Nacimiento.route) {
            val nacimientos: NacimientoViewmodel = viewModel()
            Nacimiento(navController = navController, nacimientos)
        }

        composable(Routes.Fallecimiento.route) {
            val muertes: ViewModelMuerteBovi = viewModel()
            Fallecimiento(navController = navController, muertes)
        }

        composable(Routes.GestionGuias.route) {
            val guiasViewModel: GuiasViewModel = viewModel()
            GestionGuias(navController = navController, guiasViewModel)
        }

        composable(Routes.Movimientos.route) {
            val moviViewModel: MovimientosViewModel = viewModel()
            Movimientos(navController = navController, moviViewModel)
        }

        composable(Routes.Material.route) {
            val materialV: MaterialViewModel = viewModel()
            Material(navController = navController, materialV)
        }

        composable(Routes.CorregirBovino.route) {
            val corregirSexo: CorrecionSexoViewModel = viewModel()
            CorregirSexoBovi(navController, corregirSexo)
        }

        composable(Routes.IdentificacionAplazada.route) {
            val identificacion: IdentificacionAplazaViewModel = viewModel()
            IdentificacionApalzada(navController, identificacion)
        }

        // Pantallas Porcinos
        composable(Routes.GestionGuiasPorcinos.route) {
            GestionGuiasPorcinos(navController = navController)
        }

        composable(Routes.EntradasPorcinos.route) {
            EntradasPorcinos(navController = navController)
        }
    }
}
