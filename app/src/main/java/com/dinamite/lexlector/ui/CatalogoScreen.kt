package com.dinamite.lexlector.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinamite.lexlector.R
import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.ui.components.TarjetaLibro
import com.dinamite.lexlector.viewmodel.LeyViewModel

@Composable
fun CatalogoScreen(
    viewModel: LeyViewModel = viewModel(),
    onLeyClick: (Ley) -> Unit
) {
    val todasLasLeyes = viewModel.todasLasLeyes
    val leyesRecientes = viewModel.leyesRecientes

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

    BackHandler(enabled = buscadorAbierto) {
        buscadorAbierto = false
        consulta = ""
        keyboardController?.hide()
    }

    // Encabezado fijo arriba y contenido con scroll debajo
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // "Documentos" a la izquierda y el buscador a la derecha
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
                    text = "Documentos",
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

        Spacer(modifier = Modifier.height(16.dp))

        if (buscadorAbierto) {
            SeccionBusqueda(
                consulta = consulta,
                resultados = resultados,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onLeyClick = { ley ->
                    keyboardController?.hide()
                    buscadorAbierto = false
                    consulta = ""
                    viewModel.seleccionarLey(ley)
                    onLeyClick(ley)
                }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Actualizados recientemente",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = azul
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        listItems(items = leyesRecientes, key = { it.id }) { ley ->
                            TarjetaLibro(
                                ley = ley,
                                onClick = {
                                    viewModel.seleccionarLey(ley)
                                    onLeyClick(ley)
                                },
                                modifier = Modifier.width(72.dp)
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Todos los documentos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = azul
                    )
                }

                gridItems(items = todasLasLeyes, key = { it.id }) { ley ->
                    TarjetaLibro(
                        ley = ley,
                        onClick = {
                            viewModel.seleccionarLey(ley)
                            onLeyClick(ley)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        compacta = true
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionBusqueda(
    consulta: String,
    resultados: List<Ley>,
    modifier: Modifier = Modifier,
    onLeyClick: (Ley) -> Unit
) {
    val azul = colorResource(id = R.color.azul)
    val cianAzul = colorResource(id = R.color.cian_azul)

    Column(modifier = modifier) {
        Text(
            text = "Resultados",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = azul
        )

        Spacer(modifier = Modifier.height(10.dp))

        when {
            consulta.isBlank() -> MensajeBusqueda(
                texto = "Escribe el nombre, autor o descripción de un documento."
            )

            resultados.isEmpty() -> MensajeBusqueda(
                texto = "No se encontraron documentos para \"${consulta.trim()}\"."
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                listItems(items = resultados, key = { it.id }) { ley ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                text = "${ley.autor} · Actualizado: ${ley.anioActual}",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeBusqueda(texto: String) {
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
