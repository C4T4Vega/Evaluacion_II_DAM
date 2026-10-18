package com.dinamite.lexlector.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dinamite.lexlector.R
import com.dinamite.lexlector.model.Items_bottom_nav.*
@Composable
fun NavInferior(
    navController : NavHostController
){
    val menuItems = listOf( //define una lista con los elementos que aparecen en la barra
        Item_bottom_nav1,
        Item_bottom_nav2
    )
    //Mira el historial de navegacion para obtener la ruta actual de la pantalla
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    //dibuja el contenedor del menu
    NavigationBar(
        containerColor = colorResource(id = R.color.azul) // le da color
    ) {
        menuItems.forEach { item -> // genera un boton configurado con lo siguiente:
            val selected = currentRoute == item.ruta // evalua la ruta determinando si esta activa

            NavigationBarItem(
                selected = selected,
                onClick = { //define la accion, hacia que pantalla irá
                    navController.navigate(item.ruta)
                },
                icon = {
                    Icon(//le asigna un icono
                        imageVector = item.icon,
                        contentDescription = item.title
                    )

                },
                colors = NavigationBarItemDefaults.colors( //le da color
                    selectedIconColor = Color.White, //si esta seleccionado
                    unselectedIconColor = Color.White, //si No lo está
                    indicatorColor = colorResource(id = R.color.cian_azul) //color del indicador
                )
            )
        }
    }
}