package com.dinamite.lexlector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.dinamite.lexlector.ui.BienvenidaScreen
import com.dinamite.lexlector.ui.LoginScreen
import com.dinamite.lexlector.ui.PrincipalScreen
import com.dinamite.lexlector.ui.RegistroScreen
import com.dinamite.lexlector.ui.theme.LexLectorTheme
import com.dinamite.lexlector.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Inicializa el repositorio de usuarios para restaurar sesión guardada
        com.dinamite.lexlector.repository.UsuarioRepository.iniciar(this)

        val splashScreen = installSplashScreen()
        var showSplashScreen = true
        splashScreen.setKeepOnScreenCondition { showSplashScreen }

        lifecycleScope.launch {
            delay(2000.milliseconds)
            showSplashScreen = false
        }

        // Inicializamos el ViewModel que maneja toda la lógica de cuentas
        super.onCreate(savedInstanceState)
        setContent {
            LexLectorTheme(darkTheme = authViewModel.modoOscuro) {
                // Surface aplica el fondo oscuro o claro a TODAS las pantallas de la app
                androidx.compose.material3.Surface(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    //Si ya hay usuario logueado, parte en "inicio", sino en "bienvenida"
                    val sesionIniciada = com.dinamite.lexlector.repository.UsuarioRepository.usuarioLogueado != null
                    var pantallaActual by remember { mutableStateOf(if (sesionIniciada) "inicio" else "bienvenida") }

                    when (pantallaActual) {
                        "bienvenida" -> {
                            BienvenidaScreen(
                                onLoginClick = { pantallaActual = "login" },
                                onRegisterClick = { pantallaActual = "registro" }
                            )
                        }

                        "login" -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginExitoso = { pantallaActual = "inicio" },
                                onIrARegistro = { pantallaActual = "registro" }
                            )
                        }

                        "registro" -> {
                            RegistroScreen(
                                viewModel = authViewModel,
                                onRegistroExitoso = { pantallaActual = "login" },
                                onVolverALogin = { pantallaActual = "login" }
                            )
                        }

                        "inicio" -> {
                            PrincipalScreen(
                                viewModel = authViewModel,
                                onCerrarSesion = { pantallaActual = "login" }
                            )
                        }
                    }
                }
            }
        }
    }
}
