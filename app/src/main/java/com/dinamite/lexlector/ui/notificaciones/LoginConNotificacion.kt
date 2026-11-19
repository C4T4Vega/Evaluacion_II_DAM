package com.dinamite.lexlector.ui.notificaciones

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.dinamite.lexlector.ui.LoginScreen
import com.dinamite.lexlector.viewmodel.AuthViewModel

//solo se ejecuta en login la pregunta del permiso
@Composable
fun LoginConNotificacion(
    viewModel: AuthViewModel,
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    var esperandoPermiso by remember { mutableStateOf(false) }

    // Se mantiene el login detrás del diálogo para que no quede pantalla en blanco.
    LoginScreen(
        viewModel = viewModel,
        onLoginExitoso = { esperandoPermiso = true },
        onIrARegistro = onIrARegistro
    )

    if (esperandoPermiso) {
        val nombre = viewModel.usuarioActual?.nombre?.ifBlank { "usuario" } ?: "usuario"

        PermisoNotificacionesScreen(
            nombreUsuario = nombre,
            onContinuar = onLoginExitoso
        )
    }
}
