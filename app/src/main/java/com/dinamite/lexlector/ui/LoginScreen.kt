package com.dinamite.lexlector.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.dinamite.lexlector.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "LexLector", style = MaterialTheme.typography.headlineLarge)
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Correo
        OutlinedTextField(
            value = viewModel.correoLogin,
            onValueChange = { viewModel.correoLogin = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = viewModel.claveLogin,
            onValueChange = { viewModel.claveLogin = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error si falla
        if (viewModel.mensajeErrorLogin.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = viewModel.mensajeErrorLogin, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Iniciar Sesión
        Button(
            onClick = {
                if (viewModel.iniciarSesion()) {
                    onLoginExitoso()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir al Registro
        TextButton(onClick = onIrARegistro) {
            Text(text = "¿No tienes cuenta? Regístrate aquí")
        }
    }
}