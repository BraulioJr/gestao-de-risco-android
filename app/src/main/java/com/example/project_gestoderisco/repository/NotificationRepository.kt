package com.example.project_gestoderisco.repository

import android.util.Log
import com.example.project_gestoderisco.network.ApiService
import com.example.project_gestoderisco.network.NotificationRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repositório responsável por lidar com as operações de notificação.
 */
class NotificationRepository @Inject constructor(
    private val apiService: ApiService // ApiService agora é injetado!
) {

    /**
     * Aciona a Cloud Function para enviar uma notificação para o tópico 'risks'.
     * @return Um objeto Result que encapsula o sucesso (Unit) ou a falha (Exception).
     */
    suspend fun sendRiskNotification(title: String, body: String): Result<Unit> {
        val user = Firebase.auth.currentUser
            ?: return Result.failure(Exception("Usuário não autenticado para enviar notificação."))

        return try {
            val idToken = user.getIdToken(true).await().token
                ?: return Result.failure(Exception("Não foi possível obter o token de autenticação."))

            val authorizationHeader = "Bearer $idToken"
            val notificationRequest = NotificationRequest(title = title, body = body)
            val response = apiService.sendNotification(authorizationHeader, notificationRequest)

            if (response.isSuccessful) {
                Log.d("NotificationRepository", "Notificação acionada com sucesso via API.")
                Result.success(Unit)
            } else {
                Log.e("NotificationRepository", "Erro da API ao acionar notificação: ${response.code()}")
                Result.failure(Exception("Erro da API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Falha de rede ao acionar notificação.", e)
            Result.failure(e)
        }
    }
}