package com.example.gestaoderisco.gamification

import com.example.gestaoderisco.models.Achievement
import com.example.gestaoderisco.models.UserProfile
import com.example.gestaoderisco.R

/**
 * Motor de regras para processamento de XP e Conquistas.
 * Segue o princípio de "Single Source of Truth" para lógica de jogo.
 */
class GamificationEngine {

    companion object {
        const val XP_ACTION_REGISTER_RISK = 50
        const val XP_ACTION_FULL_REPORT = 100
        const val XP_ACTION_FAST_RESPONSE = 150
    }

    /**
     * Calcula o novo estado do perfil após uma ação.
     */
    fun awardXp(currentProfile: UserProfile, xpAmount: Int): UserProfile {
        return currentProfile.copy(
            currentXp = currentProfile.currentXp + xpAmount
        )
    }

    /**
     * Verifica se alguma medalha foi desbloqueada com base no estado atual.
     */
    fun checkAchievements(profile: UserProfile): List<Achievement> {
        val allAchievements = getAllAchievements()
        
        return allAchievements.map { achievement ->
            val isUnlocked = when (achievement.id) {
                "FIRST_PREVENTION" -> profile.totalRisksMitigated >= 1
                "PRECISION_MASTER" -> profile.perfectReports >= 10
                else -> false
            }
            // Mantém desbloqueado se já estava, ou desbloqueia se atingiu a meta
            achievement.copy(isUnlocked = isUnlocked || profile.unlockedAchievements.contains(achievement.id))
        }
    }

    private fun getAllAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "FIRST_PREVENTION",
                titleResId = R.string.medal_first_prevention_title,
                descriptionResId = R.string.medal_first_prevention_desc,
                xpReward = 200
            ),
            Achievement(
                id = "PRECISION_MASTER",
                titleResId = R.string.medal_full_report_title,
                descriptionResId = R.string.medal_full_report_desc,
                xpReward = 500
            )
        )
    }
}