package com.dinamite.lexlector.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinamite.lexlector.R
import com.dinamite.lexlector.model.Ley

private const val MAX_CARACTERES_TITULO = 34 //Limita el texto que se ve en pantalla
private const val MAX_CARACTERES_AUTOR = 24

@Composable
fun TarjetaLibro(
    ley: Ley,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compacta: Boolean = false
) {
    val azul = colorResource(id = R.color.azul)

    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        // Portada del documento
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (compacta) 0.72f else 0.75f), //Mantiene proporcion entre ancho y alto
            shape = RoundedCornerShape(if (compacta) 9.dp else 12.dp),// Redondea las esquinas
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),//Añade sombra
            colors = CardDefaults.cardColors(containerColor = azul)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.lexlector),//modificar imagen
                    contentDescription = "Portada de ${ley.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Lomo del libro para documento
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(if (compacta) 4.dp else 6.dp)
                        .background(Color.Black.copy(alpha = 0.18f))
                )
            }
        }

        Spacer(modifier = Modifier.height(if (compacta) 6.dp else 8.dp))

        // Titulo con cantidad máxima de letras y siempre dos líneas para que todas las tarjetas midan igual.
        Text(
            text = limitarTexto(ley.nombre, MAX_CARACTERES_TITULO),
            fontSize = if (compacta) 10.sp else 13.sp,
            lineHeight = if (compacta) 12.sp else 16.sp,
            fontWeight = FontWeight.SemiBold,
            minLines = 2,//Asegura que el documento tenga dos lineas de texto
            maxLines = 2,
            overflow = TextOverflow.Ellipsis, //Corta el texto y añade "..."
            color = azul
        )

        // Autor en una sola línea para mantener prolija la grilla.
        Text(
            text = limitarTexto(ley.autor, MAX_CARACTERES_AUTOR),
            fontSize = if (compacta) 9.sp else 11.sp,
            minLines = 1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Gray
        )
    }
}

private fun limitarTexto(texto: String, maximo: Int): String {
    return if (texto.length <= maximo) texto else texto.take(maximo).trimEnd() + "…"
}
//Funcion que evalua si un texto supera el limite de caracteres para cortarlos