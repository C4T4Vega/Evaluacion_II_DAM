package com.dinamite.lexlector.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinamite.lexlector.R
import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.viewmodel.LeyViewModel

@Composable
fun CatalogoScreen(
    viewModel: LeyViewModel = viewModel(),
    onLeyClick: (Ley) -> Unit
) {
    val todasLasLeyes = viewModel.todasLasLeyes
    val leyesRecientes = viewModel.leyesRecientes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Documentos",
            fontFamily = FontFamily(Font(R.font.clashgrotesk)),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Actualizados recientemente",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(8.dp))

        leyesRecientes.forEach { ley ->
            TarjetaLeySimple(ley = ley, onClick = {
                viewModel.seleccionarLey(ley)
                onLeyClick(ley)
            })
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Todos los documentos",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.azul)
        )

        Spacer(modifier = Modifier.height(8.dp))

        todasLasLeyes.forEach { ley ->
            TarjetaLeySimple(ley = ley, onClick = {
                viewModel.seleccionarLey(ley)
                onLeyClick(ley)
            })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TarjetaLeySimple(ley: Ley, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.cian_azul)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ley.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = "${ley.autor} • Actualizado: ${ley.anioActual}",
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}