package com.dinamite.lexlector.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.repository.LeyRepository

class LeyViewModel : ViewModel() {

    val todasLasLeyes = LeyRepository.listaLeyes
    val leyesRecientes = todasLasLeyes.filter { it.esReciente }

    var leySeleccionada by mutableStateOf<Ley?>(todasLasLeyes.firstOrNull())
        private set

    // Documentos que el usuario ha abierto durante la sesión, del más reciente al más antiguo.
    val vistosRecientemente = mutableStateListOf<Ley>()

    // Ajustes de lectura

    var letraGrande by mutableStateOf(false)// false = letra normal, true = letra grande
    var fondoSepia by mutableStateOf(false)// false = fondo claro, true = fondo sepia
    var brillo by mutableFloatStateOf(-1f)// Brillo de la ventana entre 0.1 y 1.0. -1f significa "no tocar, usar el brillo del sistema".

    fun seleccionarLey(ley: Ley) {
        leySeleccionada = ley
        LeyRepository.leySeleccionada = ley
        registrarVisto(ley)
    }

    // Filtra las leyes por nombre, autor, descripción o año de actualización.
    fun buscarLeyes(consulta: String): List<Ley> {
        val texto = consulta.trim()
        if (texto.isEmpty()) return emptyList()

        return todasLasLeyes.filter { ley ->
            ley.nombre.contains(texto, ignoreCase = true) ||
                ley.autor.contains(texto, ignoreCase = true) ||
                ley.descripcion.contains(texto, ignoreCase = true) ||
                ley.anioActual.contains(texto, ignoreCase = true)
        }
    }

    private fun registrarVisto(ley: Ley) {
        vistosRecientemente.removeAll { it.id == ley.id }
        vistosRecientemente.add(0, ley)
        if (vistosRecientemente.size > MAX_VISTOS) {
            vistosRecientemente.removeAt(vistosRecientemente.lastIndex)
        }
    }

    private companion object {
        const val MAX_VISTOS = 10
    }
}