package com.dinamite.lexlector.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinamite.lexlector.R
import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.ui.components.TarjetaLibro
import com.dinamite.lexlector.viewmodel.LeyViewModel

@Composable
fun HomeScreen(
    viewModel: LeyViewModel,
    onLeyClick: (Ley) -> Unit
) {
    var buscadorAbierto by remember { mutableStateOf(false) }
    var consulta by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val azul = colorResource(id = R.color.azul)
    val cianAzul = colorResource(id = R.color.cian_azul)

    val resultados = remember(consulta) { viewModel.buscarLeyes(consulta) }

    LaunchedEffect(buscadorAbierto) {
        if (buscadorAbierto) {
            focusRequester.requestFocus()
        }
    }

    // El botón atrás cierra el buscador antes de salir de la pantalla.
    BackHandler(enabled = buscadorAbierto) {
        buscadorAbierto = false
        consulta = ""
        keyboardController?.hide()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Encabezado con el buscador en la esquina superior derecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (buscadorAbierto) {
                OutlinedTextField(
                    value = consulta,
                    onValueChange = { consulta = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = { Text("Buscar documentos...", fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azul,
                        unfocusedBorderColor = cianAzul,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = azul
                    )
                )
            } else {
                Text(
                    text = "Inicio",
                    modifier = Modifier.weight(1f),
                    fontFamily = FontFamily(Font(R.font.clashgrotesk)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = azul
                )
            }

            IconButton(
                onClick = {
                    if (buscadorAbierto) {
                        consulta = ""
                        keyboardController?.hide()
                    }
                    buscadorAbierto = !buscadorAbierto
                }
            ) {
                Icon(
                    imageVector = if (buscadorAbierto) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (buscadorAbierto) "Cerrar búsqueda" else "Buscar documentos",
                    tint = azul
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (buscadorAbierto) {
            SeccionBusquedaInicio(
                consulta = consulta,
                resultados = resultados,
                onLeyClick = { ley ->
                    keyboardController?.hide()
                    buscadorAbierto = false
                    consulta = ""
                    onLeyClick(ley)
                }
            )
        } else {
            SeccionAvisos()

            Spacer(modifier = Modifier.height(28.dp))

            SeccionVistosRecientemente(
                leyes = viewModel.vistosRecientemente,
                onLeyClick = onLeyClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SeccionBusquedaInicio(
    consulta: String,
    resultados: List<Ley>,
    onLeyClick: (Ley) -> Unit
) {
    val azul = colorResource(id = R.color.azul)
    val cianAzul = colorResource(id = R.color.cian_azul)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Resultados",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = azul
        )

        Spacer(modifier = Modifier.height(10.dp))

        when {
            consulta.isBlank() -> MensajeBusquedaInicio(
                texto = "Escribe el nombre, autor o descripción de un documento."
            )

            resultados.isEmpty() -> MensajeBusquedaInicio(
                texto = "No se encontraron documentos para \"${consulta.trim()}\"."
            )

            else -> resultados.forEach { ley ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onLeyClick(ley) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cianAzul.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = ley.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${ley.autor} • Actualizado: ${ley.anioActual}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeBusquedaInicio(texto: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.cian_azul).copy(alpha = 0.25f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SeccionAvisos() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Avisos y noticias",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Dashboard: un rectángulo redondeado con el mismo color de fondo de la pantalla.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.cian_azul).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(24.dp)
                )
        )
    }
}

@Composable
private fun SeccionVistosRecientemente(
    leyes: List<Ley>,
    onLeyClick: (Ley) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Vistos recientemente",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (leyes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colorResource(id = R.color.cian_azul).copy(alpha = 0.3f))
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.cian_azul),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = colorResource(id = R.color.azul)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No se han visto archivos recientemente",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(items = leyes, key = { it.id }) { ley ->
                    TarjetaLibro(
                        ley = ley,
                        onClick = { onLeyClick(ley) },
                        modifier = Modifier.width(72.dp),
                        compacta = true
                    )
                }
            }
        }
    }
}
