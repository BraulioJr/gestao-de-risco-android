package com.example.project_gestoderisco.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.project_gestoderisco.data.model.TeamGoal
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GamificationRepositoryTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var repository: GamificationRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        // Mock do comportamento do SharedPreferences
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.putFloat(anyString(), anyFloat())).thenReturn(editor)
        
        // Valores padrão para UserStats
        `when`(sharedPreferences.getInt(eq("level"), anyInt())).thenReturn(1)
        `when`(sharedPreferences.getInt(eq("survived_crashes"), anyInt())).thenReturn(0)
        `when`(sharedPreferences.getFloat(eq("discipline_score"), anyFloat())).thenReturn(1.0f)

        repository = GamificationRepository(context)
    }

    @Test
    fun `distributeTeamGoalReward adds XP when goal is completed`() {
        // Arrange
        val initialXp = 100
        val rewardXp = 1000
        `when`(sharedPreferences.getInt(eq("xp"), anyInt())).thenReturn(initialXp)

        val completedGoal = TeamGoal(
            id = "goal_1",
            description = "Meta Completa",
            targetValue = 10,
            currentValue = 10, // Meta atingida
            rewardXp = rewardXp
        )

        // Act
        repository.distributeTeamGoalReward(completedGoal)

        // Assert
        // Verifica se o XP salvo é a soma do inicial + recompensa (100 + 1000 = 1100)
        verify(editor).putInt("xp", 1100)
        verify(editor).apply()
    }

    @Test
    fun `distributeTeamGoalReward does NOT add XP when goal is incomplete`() {
        // Arrange
        val incompleteGoal = TeamGoal("goal_2", "Meta Incompleta", 10, 5, 1000)

        // Act
        repository.distributeTeamGoalReward(incompleteGoal)

        // Assert
        // Garante que o editor nunca foi chamado para salvar XP
        verify(editor, never()).putInt(eq("xp"), anyInt())
    }
}