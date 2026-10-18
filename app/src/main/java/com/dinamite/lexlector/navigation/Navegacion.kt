package com.dinamite.lexlector.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dinamite.lexlector.ui.HomeScreen
import com.dinamite.lexlector.ui.PerfilScreen
import com.dinamite.lexlector.viewmodel.AuthViewModel

@Composable
fun Navegacion(
    navController: NavHostController, //maneja el historial de navegacioón
    viewModel: AuthViewModel, //maneja el perfil
    onCerrarSesion: () -> Unit, //funcion para cuando se cierre sesion
    paddingValues: PaddingValues = PaddingValues() //evita que se mueva de lugar
) {
    NavHost(
        navController = navController, //conecta con el controlador de navegacion
        startDestination = NavScreen.HomeScreen.name, //define la pantalla inicial
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavScreen.HomeScreen.name) { //carga el @Composable de Home
            HomeScreen()
        }

        composable(NavScreen.PerfilScreen.name) {//carga el @Composable de perfil
            PerfilScreen(
                viewModel = viewModel,
                onCerrarSesion = onCerrarSesion
            )
        }
    }
}