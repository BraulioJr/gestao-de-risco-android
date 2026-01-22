package com.example.gestaoderisco.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.gestaoderisco.MainActivity
import com.example.gestaoderisco.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        val highValueOnly = prefs.getBoolean("notifications_high_value_only", false)

        if (!notificationsEnabled) return

        // Verifica se é um alerta de alto valor (baseado no título ou payload)
        val titleCheck = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val isHighValue = remoteMessage.data["type"] == "high_value" || 
                          (titleCheck?.contains("Alto Valor", true) == true)

        if (highValueOnly && !isHighValue) return

        // Verifica se a mensagem contém uma carga de notificação.
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)
        }

        // Também verifica se a mensagem contém uma carga de dados (para processamento em segundo plano)
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            // Se houver dados específicos, você pode tratar aqui. 
            // Ex: Se vier apenas dados e não notificação, forçamos a exibição.
            if (remoteMessage.notification == null && !title.isNullOrBlank()) {
                sendNotification(title, body)
            }
        }
    }

    override fun onNewToken(token: String) {
        // Aqui você enviaria o token para o seu servidor backend para associá-lo ao usuário (Coordenador)
        super.onNewToken(token)
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.channel_high_value_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name) // Certifique-se de ter um ícone de notificação válido ou use android.R.drawable.stat_notify_error
            .setContentTitle(title ?: "Alerta de Gestão de Risco")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridade para alertas de alto valor
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Cria o canal de notificação (necessário para Android Oreo e superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel_high_value_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_high_value_description)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}