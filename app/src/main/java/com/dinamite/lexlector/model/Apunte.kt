package com.dinamite.lexlector.model

// Molde para las notas y apuntes de estudio
data class Apunte(
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val leyNombre: String = "General",
    val conceptoPrincipal: String = "",
    val subconcepto1: String = "",
    val subconcepto2: String = "",
    val usuarioCorreo: String = ""
)