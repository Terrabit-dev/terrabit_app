package com.example.terrabit_app.ui.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terrabit_app.data.network.ApiInterface
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.pantallas.*
import com.example.terrabit_app.ui.screen.Login
import com.example.terrabit_app.ui.screen.bovinos.CorregirSexoBovi
import com.example.terrabit_app.ui.screen.bovinos.Fallecimiento
import com.example.terrabit_app.ui.screen.bovinos.GestionGuias
import com.example.terrabit_app.ui.screen.bovinos.IdentificacionApalzada
import com.example.terrabit_app.ui.screen.bovinos.ListarBovinos
import com.example.terrabit_app.ui.screen.bovinos.Material
import com.example.terrabit_app.ui.screen.bovinos.Movimientos
import com.example.terrabit_app.ui.screen.bovinos.Nacimiento
import com.example.terrabit_app.ui.screen.porcinos.EditarGuiaPorcinos
import com.example.terrabit_app.ui.screen.porcinos.EntradasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.GestionGuiasPorcinos
import com.example.terrabit_app.ui.screen.porcinos.HomePorcinos
import com.example.terrabit_app.utils.UserPreferences
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
import com.example.terrabit_app.viewmodel.porcinos.CrearGuiaPorcinosViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModel
import com.example.terrabit_app.viewmodel.porcinos.GestionarGuiasViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(myViewmodel: MainViewmodel, drawerViewModel: DrawerViewModel ) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val repositorio = remember { Repositorio() }

    val userPreferences = remember { UserPreferences(context) }

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // Pantalla principal
        composable(Routes.HomeBovinos.route) {
            val borradorviewmodel: BorradorViewModel = viewModel()
            Home(navController = navController, drawerViewModel, myViewmodel, borradorviewmodel)
        }


        // Pantalla principal porcinos
        composable(Routes.HomePorcinos.route) {
            HomePorcinos(
                navController = navController,
                drawerViewModel = drawerViewModel
            )
        }

        // Pantallas de categorías (intermedias)

        composable(Routes.ListarBovinos.route) {
            val viewmodel : ListarBovinosViewModel = viewModel()
            ListarBovinos(navController = navController, viewmodel)
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

        // Pantallas de categorías porcinos (intermedias)
        composable(Routes.GestionPorcinos.route) {
            GestionPorcinos(navController = navController)
        }

        composable(Routes.GuiasMovimientosPorcinos.route) {
            GuiasMovimientosPorcinos(navController = navController)
        }

        // Pantallas de acciones específicas (las que ya tenías)
        composable(Routes.Nacimiento.route) {
            val nacimientos : NacimientoViewmodel = viewModel()
            Nacimiento(navController = navController, nacimientos)
        }

        composable(Routes.Fallecimiento.route) {
            val muertes : ViewModelMuerteBovi = viewModel()
            Fallecimiento(navController = navController, muertes)
        }

        composable(Routes.GestionGuias.route) {
            val guiasViewModel : GuiasViewModel = viewModel()
            GestionGuias(navController = navController, guiasViewModel)
        }

        composable(Routes.Movimientos.route) {
            val MoviViewModel : MovimientosViewModel = viewModel()
            Movimientos(navController = navController, MoviViewModel)
        }

        composable(Routes.Material.route) {
            val MaterialV : MaterialViewModel = viewModel()
            Material(navController = navController, MaterialV)
        }

        // Pantalla de login
        composable(Routes.Login.route) {
            Login(navController = navController)
        }
        composable(Routes.CorregirBovino.route) {
            val corregirSexo : CorrecionSexoViewModel = viewModel()
            CorregirSexoBovi(navController, corregirSexo)
        }
        composable(Routes.IdentificacionAplazada.route) {
            val identificacion: IdentificacionAplazaViewModel = viewModel()
            IdentificacionApalzada(navController, identificacion)
        }

        // Pantallas porcinos
        composable(Routes.GestionGuiasPorcinos.route) { backStackEntry ->

            // 2. Aquí és on passem la Factory
            val gestionarGuiasViewModel: GestionarGuiasViewModel = viewModel(
                factory = GestionarGuiasViewModelFactory( repositorio, userPreferences)
            )

            val sharedViewModel: CrearGuiaPorcinosViewModel = viewModel(
                viewModelStoreOwner = backStackEntry
            )

            GestionGuiasPorcinos(
                navController = navController,
            )
        }

        composable(Routes.EntradasPorcinos.route) {
            EntradasPorcinos(navController = navController)
        }

        composable(Routes.EditarGuiaPorcinos.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.GestionGuiasPorcinos.route)
            }

            val editarGuiasViewModel: CrearGuiaPorcinosViewModel = viewModel(
                viewModelStoreOwner = parentEntry
            )

            EditarGuiaPorcinos(
                navController = navController,
                viewModelCrearGuia = editarGuiasViewModel
            )
        }
    }
}