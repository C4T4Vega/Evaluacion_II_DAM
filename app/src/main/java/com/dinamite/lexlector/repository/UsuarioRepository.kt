package com.dinamite.lexlector.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dinamite.lexlector.model.Usuario


object UsuarioRepository {
    private lateinit var prefs: SharedPreferences //preferencias del telefono

    private var _usuarios by mutableStateOf(  // Lista de usuarios inicializada con usuarios sintéticos/ficticios de prueba
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

    //ejecutado al arrancar la app, revisa si estaba conectado
    fun iniciar(context: Context){
        prefs = context.getSharedPreferences("lexlector_sesion", Context.MODE_PRIVATE)

        //Verifica si hay un correo guardado
        val correoGuardado = prefs.getString("correo_sesion", null)
        if (correoGuardado != null){
            //si hay un correo, mantiene la sesion del usuario
            usuarioLogueado = _usuarios.find{it.correo.equals(correoGuardado, ignoreCase = true)}
        }
    }

    //al iniciar sesion lo guarda en el telefono
    fun autenticar(correo: String, contrasena: String): Boolean {
        val encontrado = _usuarios.find {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena
        }
        if (encontrado != null) {
            usuarioLogueado = encontrado
            if (::prefs.isInitialized) {
                prefs.edit().putString("correo_sesion", encontrado.correo).apply()
            }//guarda la sesion
            return true
        }
        return false
    }

    //al registrar un nuevo usuario
    fun registrarUsuario(nombre: String, apellido: String, correo: String, contrasena: String, rol: String): Boolean{
        if (_usuarios.any {it.correo.equals(correo.trim(), ignoreCase = true)}){
            return false
        }
        val nuevoId = (_usuarios.maxOfOrNull {it.id} ?: 0) +1
        val nuevoUsuario = Usuario(nuevoId, nombre, apellido, correo, contrasena, rol)
        _usuarios = _usuarios + nuevoUsuario
        return true
    }

    //al cerrar sesion se borra el correo del telefono
    fun cerrarSesion(){
        usuarioLogueado = null
            if(::prefs.isInitialized){
            prefs.edit().remove("correo_sesion").apply()
        }
    }
}