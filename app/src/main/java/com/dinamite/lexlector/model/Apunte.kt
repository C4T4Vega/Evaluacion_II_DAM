package com.dinamite.lexlector.model
import androidx.room.Entity
import androidx.room.PrimaryKey

// Molde para las notas y apuntes de estudio
@Entity(tableName = "apuntes")
data class Apunte(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val leyNombre: String = "General",
    val conceptoPrincipal: String = "",
    val subconcepto1: String = "",
    val subconcepto2: String = "",
    val usuarioCorreo: String = ""
)