package com.dinamite.lexlector.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.model.AppDatabase
import com.dinamite.lexlector.model.Usuario
import com.dinamite.lexlector.model.UsuarioDao
import com.dinamite.lexlector.remote.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object UsuarioRepository {
    private lateinit var prefs: SharedPreferences
    private var usuarioDao: UsuarioDao? = null

    private val usuariosPorDefecto = listOf(
        Usuario(1, "Vicente", "Estudiante", "estudiante@lexlector.cl", "123456", "Estudiante"),
        Usuario(2, "Camila", "Abogada", "abogada@lexlector.cl", "123456", "Abogado")
    )

    private var _usuarios by mutableStateOf(usuariosPorDefecto)
    val usuarios: List<Usuario> get() = _usuarios

    var usuarioLogueado by mutableStateOf<Usuario?>(null)
        private set

    fun iniciar(context: Context) {
        prefs = context.getSharedPreferences("lexlector_sesion", Context.MODE_PRIVATE)
        usuarioDao = AppDatabase.getDatabase(context).usuarioDao()

        // 1. Cargar usuarios desde la base de datos
        runBlocking(Dispatchers.IO) {
            val guardados = usuarioDao?.getAll().orEmpty()
            if (guardados.isEmpty()) {
                usuariosPorDefecto.forEach { usuarioDao?.insert(it) }
                _usuarios = usuariosPorDefecto
            } else {
                _usuarios = guardados
            }
        }

        // 2. Restaurar la sesion activa si habia un correo guardado
        val correoGuardado = prefs.getString("correo_sesion", null)
        if (!correoGuardado.isNullOrBlank()) {
            usuarioLogueado = _usuarios.find { it.correo.equals(correoGuardado, ignoreCase = true) }
        }

        // 3. Sincronizar con GET /usuarios de Xano en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remotos = RetrofitInstance.api.obtenerUsuarios()
                if (remotos.isNotEmpty()) {
                    remotos.forEach { usuarioDao?.insert(it) }
                    _usuarios = (_usuarios + remotos).distinctBy { it.correo.trim().lowercase() }
                    if (!correoGuardado.isNullOrBlank() && usuarioLogueado == null) {
                        usuarioLogueado = _usuarios.find { it.correo.equals(correoGuardado, ignoreCase = true) }
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun autenticar(correo: String, contrasena: String): Boolean {
        val encontrado = _usuarios.find {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena
        }
        if (encontrado != null) {
            usuarioLogueado = encontrado
            if (::prefs.isInitialized) {
                prefs.edit().putString("correo_sesion", encontrado.correo).apply()
            }
            return true
        }
        return false
    }

    fun registrarUsuario(nombre: String, apellido: String, correo: String, contrasena: String, rol: String): Boolean {
        if (_usuarios.any { it.correo.equals(correo.trim(), ignoreCase = true) }) {
            return false
        }
        val nuevoId = (_usuarios.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoUsuario = Usuario(nuevoId, nombre.trim(), apellido.trim(), correo.trim(), contrasena, rol)
        _usuarios = _usuarios + nuevoUsuario

        // Guardar en Room y enviar a Xano (POST /usuarios)
        CoroutineScope(Dispatchers.IO).launch {
            usuarioDao?.insert(nuevoUsuario)
            try {
                RetrofitInstance.api.registrarUsuario(nuevoUsuario)
            } catch (_: Exception) { }
        }
        return true
    }

    fun guardarModoOscuro(activo: Boolean) {
        if (::prefs.isInitialized) {
            prefs.edit().putBoolean("modo_oscuro", activo).apply()
        }
    }

    fun leerModoOscuro(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean("modo_oscuro", false) else false
    }

    fun cerrarSesion() {
        usuarioLogueado = null
        if (::prefs.isInitialized) {
            prefs.edit().remove("correo_sesion").apply()
        }
    }
}