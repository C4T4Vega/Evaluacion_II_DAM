package com.dinamite.lexlector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.dinamite.lexlector.repository.UsuarioRepository
import com.dinamite.lexlector.ui.components.TarjetaLibroApunte


@Composable
fun ApuntesScreen(
    onCrearNuevoApunte: () -> Unit = {},
    onApunteClick: () -> Unit = {}
) {
    val correoUsuario = UsuarioRepository.usuarioLogueado?.correo
    val listaApuntes = ApunteRepository.apuntes

    val listaMostrada = remember(listaApuntes, correoUsuario) {
        ApunteRepository.obtenerApuntesDeUsuario(correoUsuario)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Mis Apuntes",
                fontFamily = FontFamily(Font(R.font.clashgrotesk)),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.azul)
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Todos mis apuntes",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.azul)
            )
        }

        if (listaMostrada.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colorResource(id = R.color.cian_azul).copy(alpha = 0.3f))
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.cian_azul).copy(alpha = 0.6f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay apuntes",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            gridItems(items = listaMostrada, key = { it.id }) { apunte ->
                TarjetaLibroApunte(
                    apunte = apunte,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            com.dinamite.lexlector.repository.ApunteRepository.apunteSeleccionado = apunte
                            onApunteClick()
                        }
                )
            }
        }
    }
}
