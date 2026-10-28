package com.dinamite.lexlector.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dinamite.lexlector.repository.ApunteRepository
import com.dinamite.lexlector.ui.ApuntesScreen
import com.dinamite.lexlector.ui.CatalogoScreen
import com.dinamite.lexlector.ui.CrearNotaScreen
import com.dinamite.lexlector.ui.DetalleApunteScreen
import com.dinamite.lexlector.ui.DetalleLeyScreen
import com.dinamite.lexlector.ui.HomeScreen
import com.dinamite.lexlector.ui.ListaApuntesScreen
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
            HomeScreen(
                viewModel = leyViewModel,
                onLeyClick = { ley ->
                    // Guarda la ley como "vista recientemente" y abre su detalle.
                    leyViewModel.seleccionarLey(ley)
                    navController.navigate(NavScreen.DetalleLeyScreen.name)
                },
                onApunteClick = {
                    navController.navigate(NavScreen.DetalleApunteScreen.name)
                }
            )
        }

        // Catálogo de Leyes
        composable(NavScreen.CatalogoScreen.name) {
            CatalogoScreen(
                viewModel = leyViewModel,
                onLeyClick = {
                    navController.navigate(NavScreen.DetalleLeyScreen.name)
                }
            )
        }

        // Lista de Apuntes del usuario
        composable(NavScreen.ApuntesScreen.name) {
            ApuntesScreen(
                onCrearNuevoApunte = {
                    navController.navigate(NavScreen.CrearNotaScreen.name)
                },
                onApunteClick = {
                    navController.navigate(NavScreen.DetalleApunteScreen.name)
                }
            )
        }

        // Detalle de un apunte
        composable(NavScreen.DetalleApunteScreen.name) {
            DetalleApunteScreen(
                onVolver = {
                    ApunteRepository.modoEdicion = false
                    navController.popBackStack()
                },
                onEditar = {
                    navController.navigate(NavScreen.CrearNotaScreen.name)
                }
            )
        }

        // Ficha de detalle e historial
        composable(NavScreen.DetalleLeyScreen.name) {
            DetalleLeyScreen(
                viewModel = leyViewModel,
                onVolver = {
                    navController.popBackStack() // Vuelve atrás al catálogo
                },
                onVerApuntes = {
                    // Ahora al tocar "Ver Apuntes", abre la Lista de Apuntes (Tarea 3.4)
                    navController.navigate(NavScreen.ListaApuntesScreen.name)
                }
            )
        }

        // Lista de Apuntes (Tarea 3.4)
        composable(NavScreen.ListaApuntesScreen.name) {
            ListaApuntesScreen(
                leyNombreFiltro = leyViewModel.leySeleccionada?.nombre,
                onVolver = {
                    navController.popBackStack()
                },
                onCrearNuevoApunte = {
                    // El botón "+" abre la pantalla de crear apunte con esquema
                    navController.navigate(NavScreen.CrearNotaScreen.name)
                }
            )
        }

        // Crear Nota con Esquema (Tarea 3.3)
        composable(NavScreen.CrearNotaScreen.name) {
            CrearNotaScreen(
                leyNombre = leyViewModel.leySeleccionada?.nombre ?: "Código Civil",
                onVolver = {
                    ApunteRepository.modoEdicion = false
                    navController.popBackStack()
                },
                onGuardadoExitoso = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Perfil
        composable(NavScreen.PerfilScreen.name) {
            PerfilScreen(
                viewModel = viewModel,
                onCerrarSesion = onCerrarSesion
            )
        }
    }
}
