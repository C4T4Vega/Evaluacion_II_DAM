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
                subconcepto2 = "Voluntad Soberana",
                usuarioCorreo = ""
            )
        )
    )
    val apuntes: List<Apunte> get() = _apuntes

    // Apunte seleccionado para visualizar/editar
    var apunteSeleccionado by mutableStateOf<Apunte?>(null)
    // Indica si se está editando el apunte seleccionado
    var modoEdicion by mutableStateOf(false)

    // Función para guardar un apunte nuevo
    fun agregarApunte(apunte: Apunte) {
        val nuevoId = (_apuntes.maxOfOrNull { it.id } ?: 0) + 1
        _apuntes = _apuntes + apunte.copy(id = nuevoId)
    }

    // Actualiza un apunte existente
    fun actualizarApunte(apunteActualizado: Apunte) {
        _apuntes = _apuntes.map { if (it.id == apunteActualizado.id) apunteActualizado else it }
        apunteSeleccionado = apunteActualizado
    }

    // Función para eliminar un apunte (solo para el usuario que lo creó)
    fun eliminarApunte(id: Int, usuarioCorreo: String? = null) {
        val correo = usuarioCorreo?.trim()?.lowercase()
        _apuntes = _apuntes.filter { apunte ->
            if (apunte.id != id) {
                true
            } else {
                if (correo.isNullOrBlank()) {
                    false
                } else {
                    apunte.usuarioCorreo.trim().lowercase() != correo
                }
            }
        }
    }

    fun obtenerApuntesDeUsuario(usuarioCorreo: String?): List<Apunte> {
        val correo = usuarioCorreo?.trim()?.lowercase().orEmpty()
        if (correo.isBlank()) return emptyList()
        return _apuntes.filter { it.usuarioCorreo.trim().lowercase() == correo }
    }
}
