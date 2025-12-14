package com.example.project_gestoderisco.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Risk(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val impact: String = "Baixo", // e.g., "Baixo", "Médio", "Alto"
    val probability: String = "Baixa", // e.g., "Baixa", "Média", "Alta"
    val status: String = "Aberto",
    val mitigationPlan: String? = null,
    val attachmentUrl: String? = null,
    val lgpdDetails: LgpdDetails? = null
) : Parcelable