package com.dinamite.lexlector.ui.notificaciones

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dinamite.lexlector.MainActivity
import com.dinamite.lexlector.R

object NotificacionLogin {

    private const val ID_CANAL = "lexlector_inicio_sesion"
    private const val ID_NOTIFICACION = 1001
    //Crea el canal para enviarla
    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                ID_CANAL,
                "Inicio de sesión",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificación enviada al iniciar sesión en LexLector"
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(canal)
        }
    }
    //true solo cuando el usuaio acepto el permiso
    fun tienePermiso(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

     //Envía a la bandeja la notificación sobre el inicio o no hace nada si el permiso no fue concedido
    fun mostrar(context: Context, nombreUsuario: String) {
        if (!tienePermiso(context)) return

        crearCanal(context)

        val intent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = Notification.Builder(context, ID_CANAL)
            .setSmallIcon(R.drawable.ic_notificaciones_activas)
            .setColor(ContextCompat.getColor(context, R.color.azul))
            .setContentTitle("Sesión iniciada")
            .setContentText("Hola $nombreUsuario, iniciaste sesión en LexLector.")
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(ID_NOTIFICACION, notificacion)
    }
}
