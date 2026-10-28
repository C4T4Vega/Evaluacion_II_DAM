package com.dinamite.lexlector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.dinamite.lexlector.repository.ApunteRepository

/**
 * Pantalla de detalle de un apunte
 * - Muestra título, ley asociada, contenido y mapa conceptual si existe
 * - Botón "Editar" para abrir pantalla de creación/edición en modo edición
 */
@Composable
fun DetalleApunteScreen(
    onVolver: () -> Unit,
    onEditar: () -> Unit = {}
) {
    val apunte = ApunteRepository.apunteSeleccionado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = colorResource(id = R.color.azul)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.TextButton(
                onClick = {
                    ApunteRepository.modoEdicion = true
                    onEditar()
                }
            ) {
                Text(
                    text = "Editar",
                    color = colorResource(id = R.color.azul),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = apunte?.titulo ?: "Sin título",
            fontFamily = FontFamily(Font(R.font.clashgrotesk)),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Ley: ${apunte?.leyNombre ?: "General"}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Contenido",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = apunte?.contenido ?: "",
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        if (!apunte?.conceptoPrincipal.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mapa Conceptual",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.azul)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cian_azul).copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorResource(id = R.color.azul))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = apunte?.conceptoPrincipal.orEmpty(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = colorResource(id = R.color.azul),
                        modifier = Modifier.size(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!apunte?.subconcepto1.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = apunte?.subconcepto1.orEmpty(),
                                    color = colorResource(id = R.color.azul)
                                )
                            }
                        }
                        if (!apunte?.subconcepto2.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = apunte?.subconcepto2.orEmpty(),
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
