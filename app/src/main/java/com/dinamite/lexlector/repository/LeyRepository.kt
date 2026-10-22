package com.dinamite.lexlector.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.model.CambioLey
import com.dinamite.lexlector.model.Ley

object LeyRepository {

    // Lista de leyes de ejemplo para mostrar en el catálogo
    val listaLeyes = listOf(
        Ley(
            id = 1,
            nombre = "Código Civil",
            autor = "Ministerio de Justicia",
            anioPublicacion = "1855",
            anioActual = "2024",
            esReciente = true,
            descripcion = "Regula las relaciones civiles entre las personas, contratos, familia y bienes.",
            historial = listOf(
                CambioLey("2024", "Actualización de normas sobre filiación y bienes familiares."),
                CambioLey("2022", "Reforma sobre matrimonio igualitario y acuerdo de unión civil.")
            )
        ),
        Ley(
            id = 2,
            nombre = "Código del Trabajo",
            autor = "Ministerio del Trabajo",
            anioPublicacion = "1994",
            anioActual = "2024",
            esReciente = true,
            descripcion = "Normas que regulan los contratos laborales, jornada de 40 horas y derechos sindicales.",
            historial = listOf(
                CambioLey("2024", "Entrada en vigencia gradual de la Ley de 40 horas semanales."),
                CambioLey("2023", "Ley Karin sobre prevención del acoso laboral.")
            )
        ),
        Ley(
            id = 3,
            nombre = "Constitución Política",
            autor = "Estado de Chile",
            anioPublicacion = "1980",
            anioActual = "2023",
            esReciente = false,
            descripcion = "Ley fundamental que establece la organización del Estado y los derechos constitucionales.",
            historial = listOf(
                CambioLey("2023", "Modificación de quórums para reformas constitucionales."),
                CambioLey("2005", "Reforma estructural al sistema constitucional.")
            )
        )
    )

    // Guarda cuál ley tocó el usuario para mostrarla en la pantalla de Detalle
    var leySeleccionada by mutableStateOf<Ley?>(listaLeyes.first())
}