package com.example.project_gestoderisco.data.model

import java.io.Serializable

data class StressEvent(val name: String, val severity: Double)

data class ForensicReport(
    val eventName: String,
    val actionTaken: String,
    val responseTimeSeconds: Int,
    val savedCapital: Double,      // Quanto ele deixou de perder
    val potentialLoss: Double,     // O prejuízo se não tivesse feito nada
    val disciplineGrade: String,   // S, A, B, C, D
    val insight: String            // Uma dica educativa
) : Serializable