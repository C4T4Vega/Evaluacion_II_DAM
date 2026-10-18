package com.dinamite.lexlector.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
//regresa texto opcional, representa la ruta actual(si no existe regresa null)
fun currentRoute(
    navController: NavController
): String? = navController.currentBackStackEntryAsState().value?.destination?.route
//Observa el historial de forma reactiva
//value: obtine estado actual
//destination: obtiene destino actual del historial
//route: Extrae el nombre de ruta (identificador de la pantalla)