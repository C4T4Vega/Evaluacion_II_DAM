package com.dinamite.lexlector.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinamite.lexlector.model.Ley
import com.dinamite.lexlector.repository.LeyRepository
import com.dinamite.lexlector.repository.LeyesDataSource
import kotlinx.coroutines.launch

class LeyViewModel(
    private val repository: LeyesDataSource = LeyRepository
) : ViewModel() {

    // Constructor vacío para que Navegacion.kt siga creando el ViewModel con viewModel()
    constructor() : this(LeyRepository)

    var todasLasLeyes by mutableStateOf(LeyRepository.listaLeyes)
        private set

    val leyesRecientes: List<Ley>
        get() = todasLasLeyes.filter { it.esReciente }

    var leySeleccionada by mutableStateOf<Ley?>(todasLasLeyes.firstOrNull())
        private set

    // Documentos que el usuario ha abierto durante la sesión, del más reciente al mas antiguo.
    val vistosRecientemente = mutableStateListOf<Ley>()

    // Ajustes de lectura
    var letraGrande by mutableStateOf(false)
    var fondoSepia by mutableStateOf(false)
    var brillo by mutableFloatStateOf(-1f)

    init {
        cargarLeyesDesdeApi()
    }

    // Consulta el endpoint GET /leyes en Xano sin bloquear la interfaz
    fun cargarLeyesDesdeApi() {
        viewModelScope.launch {
            try {
                val leyesRemotas = repository.obtenerLeyesApi()
                if (leyesRemotas.isNotEmpty()) {
                    todasLasLeyes = leyesRemotas
                }
            } catch (_: Exception) {
                // Si no hay internet o falla la API, mantiene las leyes locales sin caer
            }
        }
    }

    fun seleccionarLey(ley: Ley) {
        leySeleccionada = ley
        LeyRepository.leySeleccionada = ley
        registrarVisto(ley)
    }

    // Filtra las leyes por nombre, autor, descripcion o año de actualizacion.
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