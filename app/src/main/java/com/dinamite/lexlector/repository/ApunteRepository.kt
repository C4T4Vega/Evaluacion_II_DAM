package com.dinamite.lexlector.repository

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.model.AppDatabase
import com.dinamite.lexlector.model.Apunte
import com.dinamite.lexlector.model.ApunteDao
import com.dinamite.lexlector.remote.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ApunteRepository {
    private var apunteDao: ApunteDao? = null

    private val apunteInicial = Apunte(
        id = 1,
        titulo = "Definición de Ley (Art. 1)",
        contenido = "La ley es una declaración de la voluntad soberana que manda, prohíbe o permite.",
        leyNombre = "Código Civil",
        conceptoPrincipal = "Ley Chilena",
        subconcepto1 = "Manda o Prohíbe",
        subconcepto2 = "Voluntad Soberana",
        usuarioCorreo = ""
    )

    private var _apuntes by mutableStateOf(listOf(apunteInicial))
    val apuntes: List<Apunte> get() = _apuntes

    var apunteSeleccionado by mutableStateOf<Apunte?>(null)
    var modoEdicion by mutableStateOf(false)

    fun iniciar(context: Context) {
        apunteDao = AppDatabase.getDatabase(context).apunteDao()

        // 1. Cargar apuntes guardados desde Room (SQLite)
        runBlocking(Dispatchers.IO) {
            val guardados = apunteDao?.getAll().orEmpty()
            if (guardados.isEmpty()) {
                apunteDao?.insert(apunteInicial)
                _apuntes = listOf(apunteInicial)
            } else {
                _apuntes = guardados
            }
        }

        // 2. Sincronizar apuntes desde Xano (GET /apuntes)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remotos = RetrofitInstance.api.obtenerApuntes()
                if (remotos.isNotEmpty()) {
                    remotos.forEach { apunteDao?.insert(it) }
                    _apuntes = (_apuntes + remotos).distinctBy { it.id }
                }
            } catch (_: Exception) { }
        }
    }

    fun agregarApunte(apunte: Apunte) {
        val nuevoId = (_apuntes.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoApunte = apunte.copy(id = nuevoId)
        _apuntes = _apuntes + nuevoApunte

        CoroutineScope(Dispatchers.IO).launch {
            apunteDao?.insert(nuevoApunte)
            try {
                RetrofitInstance.api.crearApunte(nuevoApunte)
            } catch (_: Exception) { }
        }
    }

    fun actualizarApunte(apunteActualizado: Apunte) {
        _apuntes = _apuntes.map { if (it.id == apunteActualizado.id) apunteActualizado else it }
        apunteSeleccionado = apunteActualizado

        CoroutineScope(Dispatchers.IO).launch {
            apunteDao?.update(apunteActualizado)
            try {
                RetrofitInstance.api.actualizarApunte(apunteActualizado.id, apunteActualizado)
            } catch (_: Exception) { }
        }
    }

    fun eliminarApunte(id: Int, usuarioCorreo: String? = null) {
        val correo = usuarioCorreo?.trim()?.lowercase()
        val tamanoAntes = _apuntes.size
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
        if (_apuntes.size != tamanoAntes) {
            CoroutineScope(Dispatchers.IO).launch {
                apunteDao?.deleteById(id)
                try {
                    RetrofitInstance.api.eliminarApunte(id)
                } catch (_: Exception) { }
            }
        }
    }

    fun obtenerApuntesDeUsuario(usuarioCorreo: String?): List<Apunte> {
        val correo = usuarioCorreo?.trim()?.lowercase().orEmpty()
        if (correo.isBlank()) return emptyList()
        return _apuntes.filter { it.usuarioCorreo.trim().lowercase() == correo }
    }
}