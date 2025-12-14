package com.example.project_gestoderisco.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.project_gestoderisco.MainActivity
import com.example.project_gestoderisco.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        const val CHANNEL_ID = "risk_channel"
    }

    /**
     * Chamado quando uma nova mensagem é recebida.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        var title: String? = null
        var body: String? = null

        // Prioriza o payload de notificação, que é o que o Console do Firebase envia.
        remoteMessage.notification?.also {
            title = it.title
            body = it.body
        }

        // Se não houver payload de notificação, verifica se há um payload de dados.
        if (remoteMessage.data.isNotEmpty()) {
            title = title ?: remoteMessage.data["title"]
            body = body ?: remoteMessage.data["body"]
        }
        sendNotification(title, body)
    }

    /**
     * Chamado quando um novo token FCM é gerado.
     * Salve este token no seu servidor para poder enviar notificações para este dispositivo.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null && token != null) {
            // Supondo que você tenha uma coleção 'users' para armazenar dados do usuário.
            val userDocRef = Firebase.firestore.collection("users").document(userId)
            userDocRef.update("fcmToken", token)
                .addOnSuccessListener { Log.d(TAG, "FCM token updated for user: $userId") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating FCM token", e) }
        }
    }

    /**
     * Cria e exibe uma notificação simples.
     */
    private fun sendNotification(title: String?, messageBody: String?) {
        // Cria um Intent para abrir a MainActivity quando a notificação for tocada.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification) // Você precisará criar este ícone
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Define a ação de clique
            .setAutoCancel(true)

        // ID único para a notificação
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }
}