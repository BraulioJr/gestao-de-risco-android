package com.example.gestaoderisco.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gestaoderisco.models.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val currentXp: Int,
    val totalRisksMitigated: Int,
    val perfectReports: Int,
    val unlockedAchievementsIds: String // Armazenado como CSV simples (ex: "ID1,ID2")
) {
    fun toDomain(): UserProfile {
        val achievementsList = if (unlockedAchievementsIds.isBlank()) {
            emptyList()
        } else {
            unlockedAchievementsIds.split(",")
        }
        
        return UserProfile(
            userId = userId,
            currentXp = currentXp,
            totalRisksMitigated = totalRisksMitigated,
            perfectReports = perfectReports,
            unlockedAchievements = achievementsList
        )
    }
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        userId = userId,
        currentXp = currentXp,
        totalRisksMitigated = totalRisksMitigated,
        perfectReports = perfectReports,
        unlockedAchievementsIds = unlockedAchievements.joinToString(",")
    )
}