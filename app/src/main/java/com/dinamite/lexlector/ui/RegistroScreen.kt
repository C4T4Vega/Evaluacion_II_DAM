package com.dinamite.lexlector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.dinamite.lexlector.R
import com.dinamite.lexlector.viewmodel.AuthViewModel

@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    onRegistroExitoso: () -> Unit,
    onVolverALogin: () -> Unit
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
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "LexLector", fontFamily = FontFamily(Font(R.font.clashgrotesk)), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Registrarse", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.nombreRegistro,
            onValueChange = { viewModel.nombreRegistro = it },
            label = { Text("Nombre") },
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.apellidoRegistro,
            onValueChange = { viewModel.apellidoRegistro = it },
            label = { Text("Apellido") },
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.correoRegistro,
            onValueChange = { viewModel.correoRegistro = it },
            label = { Text("Correo") },
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.claveRegistro,
            onValueChange = { viewModel.claveRegistro = it },
            label = { Text("Contraseña") },
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

        if (viewModel.mensajeRegistro.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = viewModel.mensajeRegistro, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (viewModel.registrar()) {
                    onRegistroExitoso()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.azul),
                contentColor = Color.White
            )
        ) {
            Text(text = "Registrarse")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onVolverALogin,
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorResource(id = R.color.azul)
            )) {
            Text(text = "¿Ya tienes cuenta? Inicia sesión")
        }
    }
}