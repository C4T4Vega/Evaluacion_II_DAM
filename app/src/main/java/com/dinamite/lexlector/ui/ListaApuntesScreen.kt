package com.dinamite.lexlector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
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

@Composable
fun ListaApuntesScreen(
    leyNombreFiltro: String? = null,
    onVolver: () -> Unit,
    onCrearNuevoApunte: () -> Unit
) {
    // 1. Obtenemos los apuntes del usuario logueado
    val correoUsuario = com.dinamite.lexlector.repository.UsuarioRepository.usuarioLogueado?.correo
    val todosLosApuntes = remember { ApunteRepository.obtenerApuntesDeUsuario(correoUsuario) }
    val apuntesUsuario = ApunteRepository.apuntes

    // Filtramos por el usuario logueado y opcionalmente por la ley seleccionada
    val listaMostrada = remember(apuntesUsuario, correoUsuario, leyNombreFiltro) {
        ApunteRepository.obtenerApuntesDeUsuario(correoUsuario).let { misApuntes ->
            if (!leyNombreFiltro.isNullOrBlank()) {
                misApuntes.filter { it.leyNombre.equals(leyNombreFiltro, ignoreCase = true) }
            } else {
                misApuntes
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearNuevoApunte,
                containerColor = colorResource(id = R.color.azul),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo apunte")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Encabezado con flecha volver
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = colorResource(id = R.color.azul)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Apuntes de Estudio",
                        fontFamily = FontFamily(Font(R.font.clashgrotesk)),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.azul)
                    )
                    if (!leyNombreFiltro.isNullOrBlank()) {
                        Text(
                            text = "Norma: $leyNombreFiltro",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Si la lista está vacía
            if (listaMostrada.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tienes apuntes guardados aún",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Toca el botón + para crear tu primera nota con esquema.",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            } else {
                // Lista de tarjetas de apuntes
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(items = listaMostrada, key = { it.id }) { apunte ->
                        TarjetaApunte(
                            apunte = apunte,
                            onEliminar = { ApunteRepository.eliminarApunte(apunte.id, correoUsuario) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaApunte(
    apunte: Apunte,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = apunte.titulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.azul),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Etiqueta de la ley
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorResource(id = R.color.cian_azul).copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = apunte.leyNombre,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contenido de la nota
            Text(
                text = apunte.contenido,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Si el apunte tiene mapa conceptual, lo dibujamos en miniatura
            if (apunte.conceptoPrincipal.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Mapa Conceptual:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.azul)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Concepto principal
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorResource(id = R.color.azul))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = apunte.conceptoPrincipal,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = colorResource(id = R.color.azul),
                        modifier = Modifier.size(18.dp)
                    )

                    // Subconceptos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (apunte.subconcepto1.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, colorResource(id = R.color.azul), RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = apunte.subconcepto1,
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.azul)
                                )
                            }
                        }

                        if (apunte.subconcepto2.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, colorResource(id = R.color.azul), RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = apunte.subconcepto2,
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.azul)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
