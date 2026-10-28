package com.dinamite.lexlector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinamite.lexlector.R
import com.dinamite.lexlector.model.Apunte
import com.dinamite.lexlector.repository.ApunteRepository


//Pantalla para crear o editar apunte actualiza el apunte con ApunteRepository.actualizarApunte()

@Composable
fun CrearNotaScreen(
    leyNombre: String = "Código Civil",
    onVolver: () -> Unit,
    onGuardadoExitoso: () -> Unit
) {
    val apunteEnEdicion = if (ApunteRepository.modoEdicion) ApunteRepository.apunteSeleccionado else null
    var titulo by remember { mutableStateOf(apunteEnEdicion?.titulo ?: "") }
    var contenido by remember { mutableStateOf(apunteEnEdicion?.contenido ?: "") }
    var conceptoPrincipal by remember { mutableStateOf(apunteEnEdicion?.conceptoPrincipal ?: "") }
    var subconcepto1 by remember { mutableStateOf(apunteEnEdicion?.subconcepto1 ?: "") }
    var subconcepto2 by remember { mutableStateOf(apunteEnEdicion?.subconcepto2 ?: "") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Botón volver
        IconButton(onClick = onVolver) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = colorResource(id = R.color.azul)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (ApunteRepository.modoEdicion) "Editar Apunte" else "Nuevo Apunte de Estudio",
            fontFamily = FontFamily(Font(R.font.clashgrotesk)),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.azul)
        )
        Text(
            text = "Asociado a: ${apunteEnEdicion?.leyNombre ?: leyNombre}",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo 1: Título
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título del apunte") },
            placeholder = { Text("Ej: Análisis Art. 1437") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo 2: Contenido
        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            label = { Text("Notas y apuntes del artículo") },
            placeholder = { Text("Escribe aquí tu resumen...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECCIÓN ESQUEMA / MAPA CONCEPTUAL (Tu boceto)
        Text(
            text = "Esquema / Mapa Conceptual",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.azul)
        )
        Text(
            text = "Ingresa los conceptos para armar el mapa visual:",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = conceptoPrincipal,
            onValueChange = { conceptoPrincipal = it },
            label = { Text("Concepto Principal (Arriba)") },
            placeholder = { Text("Ej: Contratos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = subconcepto1,
                onValueChange = { subconcepto1 = it },
                label = { Text("Subconcepto 1") },
                placeholder = { Text("Ej: Unilateral") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = subconcepto2,
                onValueChange = { subconcepto2 = it },
                label = { Text("Subconcepto 2") },
                placeholder = { Text("Ej: Bilateral") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vista previa del esquma
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cian_azul).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "vista previa esquma",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.azul)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Caja Superior: Concepto Principal
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(id = R.color.azul))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (conceptoPrincipal.isNotBlank()) conceptoPrincipal else "Concepto Principal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Flechita hacia abajo
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Conector",
                    tint = colorResource(id = R.color.azul),
                    modifier = Modifier.size(24.dp)
                )

                // Cajas Inferiores conectadas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colorResource(id = R.color.azul), RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (subconcepto1.isNotBlank()) subconcepto1 else "Subconcepto 1",
                            color = colorResource(id = R.color.azul),
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colorResource(id = R.color.azul), RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (subconcepto2.isNotBlank()) subconcepto2 else "Subconcepto 2",
                            color = colorResource(id = R.color.azul),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (mensajeError.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar
        Button(
            onClick = {
                if (titulo.isBlank() || contenido.isBlank()) {
                    mensajeError = "Debes ingresar al menos título y contenido."
                } else {
                    if (ApunteRepository.modoEdicion && apunteEnEdicion != null) {
                        val correoUsuario = apunteEnEdicion.usuarioCorreo.ifBlank {
                            com.dinamite.lexlector.repository.UsuarioRepository.usuarioLogueado?.correo.orEmpty()
                        }
                        ApunteRepository.actualizarApunte(
                            apunteEnEdicion.copy(
                                titulo = titulo,
                                contenido = contenido,
                                leyNombre = apunteEnEdicion.leyNombre,
                                conceptoPrincipal = conceptoPrincipal,
                                subconcepto1 = subconcepto1,
                                subconcepto2 = subconcepto2,
                                usuarioCorreo = correoUsuario
                            )
                        )
                        ApunteRepository.modoEdicion = false
                        onGuardadoExitoso()
                    } else {
                        val correoUsuario = com.dinamite.lexlector.repository.UsuarioRepository.usuarioLogueado?.correo.orEmpty()
                        ApunteRepository.agregarApunte(
                            Apunte(
                                titulo = titulo,
                                contenido = contenido,
                                leyNombre = apunteEnEdicion?.leyNombre ?: leyNombre,
                                conceptoPrincipal = conceptoPrincipal,
                                subconcepto1 = subconcepto1,
                                subconcepto2 = subconcepto2,
                                usuarioCorreo = correoUsuario
                            )
                        )
                        onGuardadoExitoso()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azul))
        ) {
            Text(if (ApunteRepository.modoEdicion) "Guardar Cambios" else "Guardar Apunte", color = Color.White, fontSize = 15.sp)
        }
    }
}
