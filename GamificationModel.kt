package com.example.project_gestoderisco.data.model

data class RiskBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val isUnlocked: Boolean = false
)

data class UserStats(
    val level: Int = 1,
    val xp: Int = 0,
    val survivedCrashes: Int = 0,
    val disciplineScore: Float = 1.0f // 1.0 é perfeito, diminui se ele hesita no stress
)

// Pilar: Propósito & Narrativa
data class MissionContext(
    val id: String,
    val title: String,
    val corporateImpact: String, // Ex: "Protege o fluxo de caixa da loja"
    val heroNarrative: String    // Ex: "Como um Sentinela, você antecipou..."
)

// Pilar: Colaboração Coletiva
data class TeamGoal(
    val id: String,
    val description: String,
    val targetValue: Int,
    val currentValue: Int,
    val rewardXp: Int // XP compartilhado para todos da equipe
)

// Pilar: Recompensa por Impacto
data class IntelContribution(
    val id: String,
    val authorId: String,
    val content: String,
    val helpfulCount: Int, // Quantos operadores acharam útil
    val preventionValue: Double // Valor estimado que a dica ajudou a economizar
)