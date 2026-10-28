package com.dinamite.lexlector.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinamite.lexlector.R
import com.dinamite.lexlector.viewmodel.LeyViewModel

private data class ArticuloLectura(
    val numero: String,
    val titulo: String,
    val contenido: String
)

/** Devuelve artículos de ejemplo según la ley seleccionada. */
private fun obtenerArticulos(nombreLey: String): List<ArticuloLectura> = when (nombreLey) {
    "Código del Trabajo" -> listOf(
        ArticuloLectura("1", "Ámbito de aplicación", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente."),
        ArticuloLectura("2", "Relaciones laborales", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente."),
        ArticuloLectura("3", "Definiciones generales", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente.")
    )
    "Constitución Política" -> listOf(
        ArticuloLectura("1", "Bases del Estado", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente."),
        ArticuloLectura("2", "Principios constitucionales", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente."),
        ArticuloLectura("3", "Organización institucional", "Texto de demostración para comprobar que se puede avanzar y retroceder entre artículos.")
    )
    else -> listOf(
        ArticuloLectura("1", "Disposición inicial", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente."),
        ArticuloLectura("2", "Regla general", "Texto de muestra para comprobar la paginación, el indicador de avance y los botones anterior y siguiente."),
        ArticuloLectura("3", "Aplicación de la norma", "Texto de muestra para probar la paginación. Sustitúyelo por el contenido oficial antes de publicar.")
    )
}

@Composable
fun LectorScreen(
    viewModel: LeyViewModel,
    onVolver: () -> Unit
) {
    // Reutilizamos la ley elegida en el catálogo/detalle; no creamos otra selección independiente.
    val nombreLey = viewModel.leySeleccionada?.nombre ?: "Código Civil"

    // El índice representa la página actual. Se reinicia si cambia la ley seleccionada.
    var indiceArticulo by remember(nombreLey) { mutableIntStateOf(0) }
    val articulos = remember(nombreLey) { obtenerArticulos(nombreLey) }
    val articulo = articulos[indiceArticulo]
    val progreso = (indiceArticulo + 1).toFloat() / articulos.size.toFloat()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Encabezado con botón de regreso y nombre del documento que estamos leyendo.
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al detalle", tint = colorResource(R.color.azul))
                }
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = colorResource(R.color.azul))
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Lector de leyes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(nombreLey, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Indicador numérico y barra de progreso para ubicar al usuario dentro del documento.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Artículo ${indiceArticulo + 1} de ${articulos.size}", fontWeight = FontWeight.SemiBold)
                Text("${((indiceArticulo + 1) * 100) / articulos.size}%", color = colorResource(R.color.azul))
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.azul),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cada página contiene un artículo. Si el texto es largo, solo esta tarjeta se desplaza.
            Card(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)
                ) {
                    Text("ARTÍCULO ${articulo.numero}", color = colorResource(R.color.azul), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(articulo.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(articulo.contenido, style = MaterialTheme.typography.bodyLarge, lineHeight = 28.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Texto de demostración: verifica e incorpora aquí la versión oficial vigente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Anterior queda desactivado al principio y Siguiente al llegar al último artículo.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { if (indiceArticulo > 0) indiceArticulo-- },
                    enabled = indiceArticulo > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Text(" Anterior")
                }
                Button(
                    onClick = { if (indiceArticulo < articulos.lastIndex) indiceArticulo++ },
                    enabled = indiceArticulo < articulos.lastIndex,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.azul))
                ) {
                    Text("Siguiente ")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

