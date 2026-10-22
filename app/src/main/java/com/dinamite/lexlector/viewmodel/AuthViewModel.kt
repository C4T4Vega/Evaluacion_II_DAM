package com.dinamite.lexlector.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dinamite.lexlector.repository.UsuarioRepository

class AuthViewModel : ViewModel() {

    // --- VARIABLES DE LOGIN ---
    var correoLogin by mutableStateOf("")
    var claveLogin by mutableStateOf("")
    var mensajeErrorLogin by mutableStateOf("")

    // Función simple de Login
    fun iniciarSesion(): Boolean {
        // 1. Validamos que no estén vacíos
        if (correoLogin.isEmpty() || claveLogin.isEmpty()) {
            mensajeErrorLogin = "Debes llenar todos los campos"
            return false
        }

        // 2. Le preguntamos al Repositorio si existen
        val existe = UsuarioRepository.autenticar(correoLogin, claveLogin)
        if (existe) {
            mensajeErrorLogin = ""
            return true
        } else {
            mensajeErrorLogin = "Usuario o contraseña incorrectos"
            return false
        }
    }

    // --- VARIABLES DE REGISTRO ---
    var nombreRegistro by mutableStateOf("")
    var apellidoRegistro by mutableStateOf("")
    var correoRegistro by mutableStateOf("")
    var claveRegistro by mutableStateOf("")
    var mensajeRegistro by mutableStateOf("")

    // Función simple de Registro
    fun registrar(): Boolean {
        if (nombreRegistro.isEmpty() || correoRegistro.isEmpty() || claveRegistro.isEmpty()) {
            mensajeRegistro = "Debes llenar todos los campos"
            return false
        }

        // Guardamos en la lista de usuarios del repositorio
        val guardado = UsuarioRepository.registrarUsuario(
            nombre = nombreRegistro,
            apellido = apellidoRegistro,
            correo = correoRegistro,
            contrasena = claveRegistro,
            rol = "Estudiante"
        )

        if (guardado) {
            nombreRegistro = ""
            apellidoRegistro = ""
            correoRegistro = ""
            claveRegistro = ""
            mensajeRegistro = ""
            return true
        } else {
            mensajeRegistro = "El correo ya existe"
            return false
        }
    }

    //Variables y funciones de perfil del usuario para taera 1.6
    var modoOscuro by mutableStateOf(false)
    var usarFuenteSans by mutableStateOf(true)
    var fotoPerfil by mutableStateOf<Bitmap?>(null)
    fun actualizarFotoPerfil(bitmap: Bitmap) {
        fotoPerfil = bitmap
    }

    //obtenemos el usuario que inicio sesion desde el repositorio
    val usuarioActual get() = UsuarioRepository.usuarioLogueado

    fun cerrarSesion() {
        UsuarioRepository.cerrarSesion()
        correoLogin = ""
        claveLogin = ""
        mensajeErrorLogin= ""
    }
}