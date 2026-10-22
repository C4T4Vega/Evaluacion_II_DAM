package com.dinamite.lexlector.model

data class CambioLey(
    val anio: String,
    val detalle: String
)

data class Ley(
    val id: Int,
    val nombre: String,
    val autor : String,
    val anioPublicacion: String,
    val anioActual: String,
    val esReciente: Boolean,
    val descripcion: String,
    val historial: List<CambioLey>
)