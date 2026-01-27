package com.example.project_gestoderisco.repository

data class NotificationRequest(
    val title: String,
    val body: String,
    val topic: String = "risks"
)
