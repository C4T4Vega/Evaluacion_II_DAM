package com.dinamite.lexlector.viewmodel

import androidx.compose.runtime.getValue
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

    fun seleccionarLey(ley: Ley) {
        leySeleccionada = ley
        LeyRepository.leySeleccionada = ley
    }
}