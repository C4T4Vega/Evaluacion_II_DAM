package com.dinamite.lexlector.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.dinamite.lexlector.model.Apunte

private const val MAX_CARACTERES_TITULO_APUNTE = 34
private const val MAX_CARACTERES_LEY_APUNTE = 24


@Composable
fun TarjetaLibroApunte(
    apunte: Apunte,
    modifier: Modifier = Modifier
) {
    val azul = colorResource(id = R.color.azul)

    Column(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f),
            shape = RoundedCornerShape(7.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = azul)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.lexlector),
                    contentDescription = "Portada del apunte",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(3.dp)
                        .background(Color.Black.copy(alpha = 0.18f))
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = limitarTexto(apunte.titulo, MAX_CARACTERES_TITULO_APUNTE),
            fontSize = 9.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.SemiBold,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = azul
        )

        Text(
            text = limitarTexto(apunte.leyNombre, MAX_CARACTERES_LEY_APUNTE),
            fontSize = 8.sp,
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
