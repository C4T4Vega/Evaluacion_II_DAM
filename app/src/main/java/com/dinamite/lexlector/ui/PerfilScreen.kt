package com.dinamite.lexlector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinamite.lexlector.R
import com.dinamite.lexlector.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(
    viewModel: AuthViewModel,
    onCerrarSesion: () -> Unit
) {
    // 1. Obtenemos el usuario que inició sesión
    val usuario = viewModel.usuarioActual

    // 2. Variable simple para abrir o cerrar el modal de cerrar sesión
    var mostrarModal by remember { mutableStateOf(false) }

    // 3. Variable simple para el modo oscuro (Requisito de tu tarjeta de Trello)
    var esOscuro by remember { mutableStateOf(false) }

    // Colores simples que cambian según el interruptor
    val colorFondo = if (esOscuro) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
    val colorTexto = if (esOscuro) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la pantalla
        Text(
            text = "Perfil de Usuario",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colorTexto
        )

        Spacer(modifier = Modifier.height(20.dp))

        // REQUISITO: Una imagen (Ícono circular de la app)
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre y Apellido del usuario (Datos personales)
        Text(
            text = "${usuario?.nombre ?: ""} ${usuario?.apellido ?: ""}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colorTexto
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Correo (solo lectura)
        OutlinedTextField(
            value = usuario?.correo ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de Contraseña oculta (solo lectura)
        OutlinedTextField(
            value = usuario?.contrasena ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interruptor para cambiar a modo oscuro (Cumple con tu tarjeta de Trello)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (esOscuro) "Modo Oscuro Activado" else "Modo Claro Activado",
                color = colorTexto,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = esOscuro,
                onCheckedChange = { esOscuro = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para activar el Modal de Cerrar Sesión
        Button(
            onClick = { mostrarModal = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cerrar Sesión")
        }
    }

    // REQUISITO: Un Modal (AlertDialog simple)
    if (mostrarModal) {
        AlertDialog(
            onDismissRequest = { mostrarModal = false },
            title = { Text(text = "Cerrar Sesión") },
            text = { Text(text = "¿Estás seguro de que deseas salir?") },
            confirmButton = {
                Button(onClick = {
                    mostrarModal = false
                    viewModel.cerrarSesion()
                    onCerrarSesion()
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}