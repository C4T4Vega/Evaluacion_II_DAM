package com.dinamite.lexlector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.dinamite.lexlector.viewmodel.AuthViewModel
import com.dinamite.lexlector.R // importa "Resources", usa la /res/values

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
        Image(
            painter = painterResource(id = R.drawable.lexlector),
            contentDescription = "icono de app",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "LexLector", fontFamily = FontFamily(Font(R.font.clashgrotesk)), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Correo
        OutlinedTextField(
            value = viewModel.correoLogin,
            onValueChange = { viewModel.correoLogin = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = colorResource(id = R.color.azul)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.azul),   // Borde cuando está seleccionado
                    unfocusedBorderColor = colorResource(id = R.color.cian_azul), // Borde cuando no está seleccionado
                    focusedLabelColor = colorResource(id = R.color.azul),     // Color de la etiqueta "Correo" al hacer clic
                    unfocusedLabelColor = colorResource(id = R.color.cian_azul), // etiqueta cuando no está seleccionado
                    cursorColor = colorResource(id = R.color.azul)            // Color de la barrita parpadeante (cursor)
                )
            )


        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = viewModel.claveLogin,
            onValueChange = { viewModel.claveLogin = it },
            label = { Text("Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = colorResource(id = R.color.azul)
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.azul),
                unfocusedBorderColor = colorResource(id = R.color.cian_azul),
                focusedLabelColor = colorResource(id = R.color.azul),
                unfocusedLabelColor = colorResource(id = R.color.cian_azul),
                cursorColor = colorResource(id = R.color.azul)
            )
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.azul),
                contentColor = Color.White
            )
        ) {
            Text(text = "Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir al Registro
        TextButton(onClick = onIrARegistro,
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorResource(id = R.color.azul)
            )){
            Text(text = "¿No tienes cuenta? Regístrate aquí")
        }
    }
}