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
import com.dinamite.lexlector.ui.PerfilScreen
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

        val splashScreen =  installSplashScreen()
        var showSplashScreen = true
        splashScreen.setKeepOnScreenCondition {showSplashScreen}

        lifecycleScope.launch {
            delay(2000.milliseconds)
            showSplashScreen = false
        }

        // Inicializamos el ViewModel que maneja toda la lógica de cuentas

        super.onCreate(savedInstanceState)
        setContent {
            LexLectorTheme {
                // Variable simple para controlar qué pantalla se está mostrando en el celular
                var pantallaActual by remember { mutableStateOf("bienvenida") }

                //pantalla de bienvenida
                if (pantallaActual == "bienvenida") {
                    BienvenidaScreen(
                        // va al login cuando presiona el boton
                        onLoginClick = {
                            pantallaActual = "login"
                        },
                        // va a registro
                        onRegisterClick = {
                            pantallaActual = "registro"
                        }
                    )
                //Pantalla principal
                } else if (pantallaActual == "inicio") {
                    PrincipalScreen(
                        viewModel = authViewModel,
                        onCerrarSesion = {
                            pantallaActual = "login"
                        }
                    )

                // Dependiendo del valor de "pantallaActual", mostramos una vista u otra
                } else if (pantallaActual == "login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginExitoso = {
                            // Cuando el login es correcto, pasamos a la pantalla de Perfil
                            pantallaActual = "inicio"
                        },
                        onIrARegistro = {
                            pantallaActual = "registro"
                        }
                    )
                } else if (pantallaActual == "registro") {
                    RegistroScreen(
                        viewModel = authViewModel,
                        onRegistroExitoso = {
                            pantallaActual = "login"
                        },
                        onVolverALogin = {
                            pantallaActual = "login"
                        }
                    )
                } else if (pantallaActual == "perfil") {
                    PerfilScreen(
                        viewModel = authViewModel,
                        onCerrarSesion = {
                            // Cuando el usuario cierra sesion, volvemos a la pantalla de Login
                            pantallaActual = "login"
                        }
                    )
                }
            }
        }
    }
}