package com.example.project_gestoderisco.repository

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("sendNotification")
    suspend fun sendNotification(@Body request: NotificationRequest): Unit
}
