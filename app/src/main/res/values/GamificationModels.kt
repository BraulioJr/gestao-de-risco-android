package com.example.gestaoderisco.models

import androidx.annotation.StringRes
import com.example.gestaoderisco.R

/**
 * Representa a patente militar do usuário baseada em XP.
 * Inspirado na hierarquia tática.
 */
enum class MilitaryRank(
    @StringRes val titleResId: Int,
    val minXp: Int,
    val iconResId: Int // Placeholder para ícone drawable
) {
    RECRUIT(R.string.rank_recruit, 0, R.drawable.ic_launcher_foreground),
    SENTINEL(R.string.rank_sentinel, 1000, R.drawable.ic_launcher_foreground),
    GUARDIAN(R.string.rank_guardian, 3000, R.drawable.ic_shield_graph),
    MASTER(R.string.rank_master, 6000, R.drawable.ic_stat_name),
    GENERAL(R.string.rank_general, 10000, R.drawable.ic_splash_logo);

    companion object {
        fun getRankForXp(xp: Int): MilitaryRank {
            return entries.filter { it.minXp <= xp }.maxByOrNull { it.minXp } ?: RECRUIT
        }

        fun getNextRank(currentRank: MilitaryRank): MilitaryRank? {
            val nextIndex = currentRank.ordinal + 1
            return if (nextIndex < entries.size) entries[nextIndex] else null
        }
    }
}

/**
 * Perfil do Estrategista persistido no Room/Firestore.
 */
data class UserProfile(
    val userId: String,
    val currentXp: Int = 0,
    val totalRisksMitigated: Int = 0,
    val perfectReports: Int = 0,
    val unlockedAchievements: List<String> = emptyList()
) {
    val rank: MilitaryRank
        get() = MilitaryRank.getRankForXp(currentXp)

    val progressToNextRank: Float
        get() {
            val nextRank = MilitaryRank.getNextRank(rank) ?: return 100f
            val range = nextRank.minXp - rank.minXp
            val progress = currentXp - rank.minXp
            return (progress.toFloat() / range.toFloat()) * 100
        }
}

/**
 * Representa uma Medalha de Honra (Conquista).
 */
data class Achievement(
    val id: String,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val iconResId: Int = R.drawable.ic_attach_money // Placeholder
)

/**
 * Representa uma Missão baseada nos capítulos de Sun Tzu.
 */
data class CampaignMission(
    val id: String,
    val chapter: Int, // 1 a 13
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    val targetValue: Int,
    val currentValue: Int,
    val xpReward: Int
)