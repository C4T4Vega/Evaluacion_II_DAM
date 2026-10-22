package com.dinamite.lexlector.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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

    // 3. Variable de modo oscuro desde el ViewModel
    val esOscuro = viewModel.modoOscuro

    // 4. Lanzador nativo para abrir la cámara del teléfono
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.actualizarFotoPerfil(bitmap)
        }
    }

    // Colores que cambian según el interruptor de modo oscuro
    val colorFondo = if (esOscuro) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
    val colorTexto = if (esOscuro) Color.White else Color.Black
    val colorCampo = if (esOscuro) Color.White else colorResource(id = R.color.azul)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Título de la pantalla
        Text(
            text = "Perfil",
            fontFamily = FontFamily(Font(R.font.clashgrotesk)),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colorTexto
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // FOTO DE PERFIL CON CÁMARA (Tarea 2.6)
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                if (viewModel.fotoPerfil != null) {
                    // Muestra la foto tomada con la cámara nativa
                    Image(
                        bitmap = viewModel.fotoPerfil!!.asImageBitmap(),
                        contentDescription = "Foto tomada con cámara",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Muestra el logo por defecto si todavía no toma foto
                    Image(
                        painter = painterResource(id = R.drawable.lexlector),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                // Botoncito flotante circular con icono de cámara
                IconButton(
                    onClick = { camaraLauncher.launch(null) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.azul))
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Tomar foto con cámara",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de texto alternativo para abrir la cámara
            TextButton(onClick = { camaraLauncher.launch(null) }) {
                Text(
                    text = "Tomar foto con cámara",
                    fontSize = 13.sp,
                    color = colorCampo
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre y Apellido del usuario
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
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = colorTexto),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorCampo,
                    focusedBorderColor = colorCampo,
                    unfocusedTextColor = colorCampo,
                    focusedTextColor = colorCampo,
                    focusedLabelColor = colorCampo,
                    unfocusedLabelColor = colorCampo
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Contraseña oculta (solo lectura)
            OutlinedTextField(
                value = usuario?.contrasena ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = colorTexto),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorCampo,
                    focusedBorderColor = colorCampo,
                    unfocusedTextColor = colorCampo,
                    focusedTextColor = colorCampo,
                    focusedLabelColor = colorCampo,
                    unfocusedLabelColor = colorCampo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Interruptor para cambiar a modo oscuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (viewModel.modoOscuro) "Modo Oscuro Activado" else "Modo Claro Activado",
                    color = colorTexto,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = viewModel.modoOscuro,
                    onCheckedChange = { viewModel.modoOscuro = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.azul),
                        checkedTrackColor = colorResource(id = R.color.cian_azul),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón para cerrar sesión
            Button(
                onClick = { mostrarModal = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.azul),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cerrar Sesión")
            }
        }

        // Modal de confirmacion para salir
        if (mostrarModal) {
            AlertDialog(
                onDismissRequest = { mostrarModal = false },
                title = { Text(text = "Cerrar Sesión") },
                text = { Text(text = "¿Estás seguro de que deseas salir?") },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarModal = false
                            viewModel.cerrarSesion()
                            onCerrarSesion()
                        }
                    ) {
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
}