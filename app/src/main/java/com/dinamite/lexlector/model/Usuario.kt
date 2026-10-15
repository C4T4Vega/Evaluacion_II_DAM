package com.dinamite.lexlector.data

data class Usuario(
    val id: Int,
    val nombre : String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
) {

}