package com.dinamite.lexlector.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.dinamite.lexlector.navigation.NavScreen

sealed class Items_bottom_nav (
    val icon: ImageVector, // imagen vectorial que se usara
    val title: String,
    val ruta: String
){
    object Item_bottom_nav1: Items_bottom_nav(
        Icons.Outlined.Home, //le da un icono
        "Inicio", //nombre
        NavScreen.HomeScreen.name //ruta
    )
    object Item_bottom_nav2: Items_bottom_nav(
        Icons.Outlined.Person,
        "Perfil",
        NavScreen.PerfilScreen.name
    )
}