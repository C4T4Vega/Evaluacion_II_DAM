package com.dinamite.lexlector.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dinamite.lexlector.ui.CatalogoScreen
import com.dinamite.lexlector.ui.DetalleLeyScreen
import com.dinamite.lexlector.ui.HomeScreen
import com.dinamite.lexlector.ui.PerfilScreen
import com.dinamite.lexlector.viewmodel.AuthViewModel
import com.dinamite.lexlector.viewmodel.LeyViewModel

@Composable
fun Navegacion(
    navController: NavHostController,
    viewModel: AuthViewModel,
    onCerrarSesion: () -> Unit,
    paddingValues: PaddingValues = PaddingValues()
) {
    // 1. UN SOLO ViewModel de leyes compartido para catalogo y detalle
    val leyViewModel: LeyViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavScreen.HomeScreen.name,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavScreen.HomeScreen.name) {
            HomeScreen()
        }

        // Catálogo de Leyes
        composable(NavScreen.CatalogoScreen.name) {
            CatalogoScreen(
                viewModel = leyViewModel,
                onLeyClick = {
                    // 2. Al tocar la ley, navegamos a la pantalla de detalle (Tarea 2.5)
                    navController.navigate(NavScreen.DetalleLeyScreen.name)
                }
            )
        }

        // Ficha de detalle e historial
        composable(NavScreen.DetalleLeyScreen.name) {
            DetalleLeyScreen(
                viewModel = leyViewModel,
                onVolver = {
                    navController.popBackStack() // Vuelve atrás al catálogo
                }
            )
        }

        composable(NavScreen.PerfilScreen.name) {
            PerfilScreen(
                viewModel = viewModel,
                onCerrarSesion = onCerrarSesion
            )
        }
    }
}