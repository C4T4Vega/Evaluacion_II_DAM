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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import com.dinamite.lexlector.R
import com.dinamite.lexlector.viewmodel.LeyViewModel

private data class ArticuloLectura(
    val numero: String,
    val titulo: String,
    val contenido: String
)

//Devuelve artículos de ejemplo según la ley seleccionada
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

    //Ajustes de lectura
    var mostrarAjustes by remember { mutableStateOf(false) } //verifica si ajustes esta abierto

    val tamanoTexto = if (viewModel.letraGrande) 22.sp else 16.sp//tamaño de la letra
    val interlineado = if (viewModel.letraGrande) 34.sp else 26.sp //interlineado

    val colorFondo = if(viewModel.fondoSepia) Color(0xFFF4ECD8) else Color.White
    val colorTexto = if (viewModel.fondoSepia) Color(0xFF5B4636) else Color(0xFF1C1B1F)

    //Ajusta el brillo
    val activity = LocalContext.current.buscarActivity()
    DisposableEffect(viewModel.brillo) {
        val ventana = activity?.window
        if (ventana != null && viewModel.brillo >= 0f){
            val params = ventana.attributes
            params.screenBrightness = viewModel.brillo
            ventana.attributes = params
        }
        onDispose {
            val v = activity?.window
            if(v != null) {
                val params = v.attributes
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                v.attributes = params
            }
        }
    }


    Surface(modifier = Modifier.fillMaxSize(), color = colorFondo) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Encabezado con botón de regreso y nombre del documento que estamos leyendo.
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al detalle", tint = colorResource(R.color.azul))
                }
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = colorResource(R.color.azul))
                Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                    Text("Lector de leyes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colorTexto)
                    Text(nombreLey, style = MaterialTheme.typography.bodyMedium, color = colorTexto.copy(alpha = 0.7f))
                }
                // Engranaje: abre la ventana de ajustes de lectura
                IconButton(onClick = { mostrarAjustes = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Ajustes de lectura", tint = colorResource(R.color.azul))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Indicador numerico y barra de progreso para ubicar al usuario dentro del documento.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Artículo ${indiceArticulo + 1} de ${articulos.size}", fontWeight = FontWeight.SemiBold, color = colorTexto)
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

            // Cada pagina contiene un articulo. Si el texto es largo, solo esta tarjeta se desplaza.
            Card(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)
                ) {
                    Text("ARTÍCULO ${articulo.numero}", color = colorResource(R.color.azul), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(articulo.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorTexto)
                    Spacer(modifier = Modifier.height(18.dp))
                    // Aqui se aplican el tamaño y color elegidos en los ajustes
                    Text(articulo.contenido, fontSize = tamanoTexto, lineHeight = interlineado, color = colorTexto)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Texto de demostración: verifica e incorpora aquí la versión oficial vigente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorTexto.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Anterior queda desactivado al principio y Siguiente al llegar al ultimo articulo.
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

    //Ajustes para lectura
    if (mostrarAjustes) {
        AlertDialog(
            onDismissRequest = { mostrarAjustes = false },
            title = { Text("Ajustes de lectura", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    //Tamaño de letra: dos opciones
                    Text("Tamaño de letra", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OpcionAjuste("Normal", !viewModel.letraGrande, Modifier.weight(1f)) { viewModel.letraGrande = false }
                        OpcionAjuste("Grande", viewModel.letraGrande, Modifier.weight(1f)) { viewModel.letraGrande = true }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Color de fondo: dos opciones
                    Text("Color", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OpcionAjuste("Claro", !viewModel.fondoSepia, Modifier.weight(1f)) { viewModel.fondoSepia = false }
                        OpcionAjuste("Sepia", viewModel.fondoSepia, Modifier.weight(1f)) { viewModel.fondoSepia = true }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    //Brillo: un slider para subir y bajar (mínimo 10% para no dejar la pantalla negra).
                    // Si aún no se ha tocado (-1f) el slider arranca en la mitad.
                    Text("Brillo", fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = if (viewModel.brillo < 0f) 0.5f else viewModel.brillo,
                        onValueChange = { viewModel.brillo = it },
                        valueRange = 0.1f..1f
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarAjustes = false }) { Text("Listo") }
            }
        )
    }

}

//Botón de opción: relleno azul si está elegido, con borde si no. Se usa para tamaño y color.
@Composable
private fun OpcionAjuste(texto: String, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    if (seleccionado) {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.azul))
        ) { Text(texto) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(texto) }
    }
}

//Un Context de Compose puede venir "envuelto"; vamos desenvolviéndolo hasta encontrar la Activity.
private fun Context.buscarActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

