package com.example.project_gestoderisco.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Risk(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val impact: Int = 1, // 1: Baixo, 2: Médio, 3: Alto
    val probability: Int = 1, // 1: Baixa, 2: Média, 3: Alta
    val status: String = "Aberto",
    val mitigationPlan: String? = null,
    val attachmentUrl: String? = null,
    val lgpdDetails: LgpdDetails? = null,
    @ServerTimestamp
    val createdAt: Date? = null
) : Parcelable