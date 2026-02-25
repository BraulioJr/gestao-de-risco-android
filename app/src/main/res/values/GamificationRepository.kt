package com.example.project_gestoderisco.data.repository

import android.content.Context
import android.content.SharedPreferences
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
}