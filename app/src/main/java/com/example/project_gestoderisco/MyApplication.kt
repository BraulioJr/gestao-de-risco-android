package com.example.project_gestoderisco

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.project_gestoderisco.service.MyFirebaseMessagingService
import com.example.project_gestoderisco.util.ThemeManager

class MyApplication : Application() { // A anotação @HiltAndroidApp foi removida pois o Hilt não está configurado.
    override fun onCreate() {
        super.onCreate()
        // Inicializa o ThemeManager e aplica o tema salvo.
        ThemeManager.init(this)
        ThemeManager.applyThemeOnAppStart()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Cria o NotificationChannel, mas apenas em API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MyFirebaseMessagingService.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Registra o canal com o sistema
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}