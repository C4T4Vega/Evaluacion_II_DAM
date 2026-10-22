package com.dinamite.lexlector.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.dinamite.lexlector.ui.components.NavInferior
import com.dinamite.lexlector.ui.navigation.Navegacion
import com.dinamite.lexlector.viewmodel.AuthViewModel

@Composable
fun PrincipalScreen(
    viewModel: AuthViewModel,
    onCerrarSesion: () -> Unit
) {

    val navController = rememberNavController()//inicia y recuerda el controlador de navegacion
    Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
        Scaffold(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
            //coloca el menu en la parte inferior pasandole navController
            bottomBar = {
                NavInferior(navController = navController)
            }
        ) { paddingValues -> //ejecuta la funcion
            Navegacion( //inyecta:
                navController = navController,
                viewModel = viewModel,
                onCerrarSesion = onCerrarSesion,
                paddingValues = paddingValues //esto lo provee el Scaffold
            )
        }
    }
}