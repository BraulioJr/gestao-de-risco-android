package com.example.project_gestoderisco.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa a estrutura de um documento de risco no Firestore.
 */
data class Risk(
    @DocumentId // Mapeia automaticamente o ID do documento do Firestore para este campo.
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val impact: Long = 0, // Usar Long para números é uma boa prática com Firestore.
    val probability: Long = 0,
    val ownerId: String = "", // ID do usuário que criou o risco.
    @ServerTimestamp // Pede ao Firestore para preencher este campo com a data/hora do servidor.
    val createdAt: Date? = null,
    val attachmentUrl: String? = null // URL do anexo no Firebase Storage
)