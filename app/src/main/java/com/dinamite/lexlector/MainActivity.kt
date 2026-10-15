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
import com.dinamite.lexlector.ui.RegistroScreen
import com.dinamite.lexlector.ui.theme.LexLectorTheme
import com.dinamite.lexlector.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LexLectorTheme {
                // Una simple variable para saber qué pantalla mostrar
                var pantallaActual by remember { mutableStateOf("login") }

                if (pantallaActual == "login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginExitoso = {
                            // Aquí más adelante pasará al perfil (Tarea 1.6)
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
                }
            }
        }
    }
}
