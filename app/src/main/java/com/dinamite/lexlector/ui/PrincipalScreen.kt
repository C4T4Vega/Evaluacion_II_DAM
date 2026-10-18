package com.dinamite.lexlector.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.dinamite.lexlector.components.NavInferior
import com.dinamite.lexlector.navigation.Navegacion
import com.dinamite.lexlector.viewmodel.AuthViewModel

@Composable
fun PrincipalScreen(
    viewModel: AuthViewModel,
    onCerrarSesion: () -> Unit
) {

    val navController = rememberNavController()//inicia y recuerda el controlador de navegacion
    Surface(color = Color.White) { //define el color de la pantalla
        Scaffold( //estructura visual
            containerColor = Color.White,
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