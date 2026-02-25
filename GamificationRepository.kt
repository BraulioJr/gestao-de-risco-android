package com.example.project_gestoderisco.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.project_gestoderisco.data.model.IntelContribution
import com.example.project_gestoderisco.data.model.TeamGoal
import com.example.project_gestoderisco.data.model.UserStats
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gamification_prefs", Context.MODE_PRIVATE)

    fun getUserStats(): UserStats {
        return UserStats(
            level = prefs.getInt("level", 1),
            xp = prefs.getInt("xp", 0),
            survivedCrashes = prefs.getInt("survived_crashes", 0),
            disciplineScore = prefs.getFloat("discipline_score", 1.0f)
        )
    }

    fun saveUserStats(stats: UserStats) {
        prefs.edit().apply {
            putInt("level", stats.level)
            putInt("xp", stats.xp)
            putInt("survived_crashes", stats.survivedCrashes)
            putFloat("discipline_score", stats.disciplineScore)
            apply()
        }
    }

    // Simulação de Metas Coletivas (Pilar: Colaboração)
    fun getTeamGoals(): List<TeamGoal> {
        return listOf(
            TeamGoal(
                id = "zero_loss_week",
                description = "Loja sem incidentes críticos por 7 dias",
                targetValue = 7,
                currentValue = 5,
                rewardXp = 1000
            )
        )
    }

    // Simulação de Contribuições de Inteligência (Pilar: Impacto)
    fun getIntelContributions(): List<IntelContribution> {
        // Em um cenário real, isso viria do Firestore
        return emptyList()
    }

    // Distribui a recompensa de uma meta de equipe se ela estiver completa
    fun distributeTeamGoalReward(goal: TeamGoal) {
        if (goal.currentValue >= goal.targetValue) {
            val currentStats = getUserStats()
            val newStats = currentStats.copy(xp = currentStats.xp + goal.rewardXp)
            saveUserStats(newStats)
        }
    }
}