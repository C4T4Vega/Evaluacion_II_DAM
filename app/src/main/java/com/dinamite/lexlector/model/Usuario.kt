package com.dinamite.lexlector.model
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre : String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
) {

}