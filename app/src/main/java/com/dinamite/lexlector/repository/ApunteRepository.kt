package com.dinamite.lexlector.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.model.Apunte

object ApunteRepository {
    // Lista en memoria con un apunte de ejemplo
    private var _apuntes by mutableStateOf(
        listOf(
            Apunte(
                id = 1,
                titulo = "Definición de Ley (Art. 1)",
                contenido = "La ley es una declaración de la voluntad soberana que manda, prohíbe o permite.",
                leyNombre = "Código Civil",
                conceptoPrincipal = "Ley Chilena",
                subconcepto1 = "Manda o Prohíbe",
                subconcepto2 = "Voluntad Soberana"
            )
        )
    )
    val apuntes: List<Apunte> get() = _apuntes

    // Función para guardar un apunte nuevo
    fun agregarApunte(apunte: Apunte) {
        val nuevoId = (_apuntes.maxOfOrNull { it.id } ?: 0) + 1
        _apuntes = _apuntes + apunte.copy(id = nuevoId)
    }
}