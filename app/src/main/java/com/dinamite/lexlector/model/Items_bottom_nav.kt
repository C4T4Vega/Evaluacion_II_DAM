package com.dinamite.lexlector.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.dinamite.lexlector.ui.navigation.NavScreen

sealed class Items_bottom_nav(
    val icon: ImageVector, // imagen vectorial que se usara
    val title: String,
    val ruta: String
) {
    object Item_bottom_nav1 : Items_bottom_nav(
        Icons.Outlined.Home, //le da un icono
        "Inicio", //nombre
        NavScreen.HomeScreen.name //ruta
    )
    // BOTÓN PARA EL CATÁLOGO DE LEYES
    object Item_bottom_nav_leyes : Items_bottom_nav(
        Icons.Outlined.Book,
        "Leyes",
        NavScreen.CatalogoScreen.name
    )
    // BOTÓN PARA LOS APUNTES DEL USUARIO
    object Item_bottom_nav_apuntes : Items_bottom_nav(
        Icons.Outlined.NoteAlt,
        "Apuntes",
        NavScreen.ApuntesScreen.name
    )
    object Item_bottom_nav2 : Items_bottom_nav(
        Icons.Outlined.Person,
        "Perfil",
        NavScreen.PerfilScreen.name
    )
}