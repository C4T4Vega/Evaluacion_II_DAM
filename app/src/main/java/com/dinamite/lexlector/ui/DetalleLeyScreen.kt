package com.dinamite.lexlector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinamite.lexlector.R
import com.dinamite.lexlector.viewmodel.LeyViewModel

@Composable
fun DetalleLeyScreen(
    viewModel: LeyViewModel,
    onVolver: () -> Unit
) {
    // 1. Obtenemos la ley seleccionada desde el ViewModel
    val ley = viewModel.leySeleccionada

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Boton Volver con flechita
        IconButton(onClick = onVolver) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = colorResource(id = R.color.azul)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta con la Ficha Técnica (Boceto "DOCUMENTO")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cian_azul))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen / Logo
                Image(
                    painter = painterResource(id = R.drawable.lexlector),
                    contentDescription = "Logo ley",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(id = R.color.azul))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = ley?.nombre ?: "Sin título",
                        fontFamily = FontFamily(Font(R.font.clashgrotesk)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Autor: ${ley?.autor ?: "Desconocido"}",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Publicado: ${ley?.anioPublicacion} • Actual: ${ley?.anioActual}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Descripción / Resumen
        Text(
            text = "Descripción",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.azul)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ley?.descripcion ?: "Sin descripción disponible.",
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de acción para leer y ver apuntes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* En Semana 3 abrirá el lector */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azul))
            ) {
                Text("Leer", color = Color.White)
            }

            OutlinedButton(
                onClick = { /* En Semana 3 abrirá apuntes */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ver Apuntes", color = colorResource(id = R.color.azul))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // seccion donde se dice todos los cambios
        Text(
            text = "Historial de Modificaciones",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Recorremos la lista de cambios
        ley?.historial?.forEach { cambio ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Año ${cambio.anio}",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.azul)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = cambio.detalle, fontSize = 13.sp)
                }
            }
        }
    }
}