package com.dinamite.lexlector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.ui.LoginScreen
import com.dinamite.lexlector.ui.PerfilScreen
import com.dinamite.lexlector.ui.RegistroScreen
import com.dinamite.lexlector.ui.theme.LexLectorTheme
import com.dinamite.lexlector.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    // Inicializamos el ViewModel que maneja toda la lógica de cuentas
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LexLectorTheme {
                // Variable simple para controlar qué pantalla se está mostrando en el celular
                var pantallaActual by remember { mutableStateOf("login") }

                // Dependiendo del valor de "pantallaActual", mostramos una vista u otra
                if (pantallaActual == "login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginExitoso = {
                            // Cuando el login es correcto, pasamos a la pantalla de Perfil
                            pantallaActual = "perfil"
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