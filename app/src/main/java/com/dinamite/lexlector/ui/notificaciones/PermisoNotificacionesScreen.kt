package com.dinamite.lexlector.ui.notificaciones

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

//lanza la pregunta para conseder el permiso de la notificaciones
@Composable
fun PermisoNotificacionesScreen(
    nombreUsuario: String,
    onContinuar: () -> Unit
) {
    val context = LocalContext.current

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            NotificacionLogin.mostrar(context, nombreUsuario)
        }
        onContinuar()
    }

    LaunchedEffect(Unit) {
        if (NotificacionLogin.tienePermiso(context)) {
            NotificacionLogin.mostrar(context, nombreUsuario)
            onContinuar()
        } else {
            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
