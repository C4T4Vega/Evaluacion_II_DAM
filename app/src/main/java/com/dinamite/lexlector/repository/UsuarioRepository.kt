package com.dinamite.lexlector.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.model.Usuario


object UsuarioRepository {
    // Lista de usuarios inicializada con usuarios sintéticos/ficticios de prueba
    private var _usuarios by mutableStateOf(
        listOf(
            Usuario(
                id = 1,
                nombre = "Vicente",
                apellido = "Estudiante",
                correo = "estudiante@lexlector.cl",
                contrasena = "123456",
                rol = "Estudiante"
            ),
            Usuario(
                id = 2,
                nombre = "Camila",
                apellido = "Abogada",
                correo = "abogada@lexlector.cl",
                contrasena = "123456",
                rol = "Abogado"
            )
        )
    )
    val usuarios: List<Usuario> get() =_usuarios

    var usuarioLogueado by mutableStateOf<Usuario?>(null)
        private set

    fun autenticar(correo: String, contrasena: String): Boolean {
        val encontrado = _usuarios.find {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena
        }

        if (encontrado != null) {
            usuarioLogueado = encontrado
            return true
        }
        return false
    }

    fun registrarUsuario(nombre: String, apellido: String, correo: String, contrasena: String, rol: String): Boolean {
        if (_usuarios.any { it.correo.equals(correo.trim(), ignoreCase = true) }) {
            return false
        }
        val nuevoId = (_usuarios.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoUsuario = Usuario(nuevoId, nombre, apellido, correo, contrasena, rol)
        _usuarios = _usuarios + nuevoUsuario
        return true
    }

    fun cerrarSesion() {
        usuarioLogueado = null
    }
}