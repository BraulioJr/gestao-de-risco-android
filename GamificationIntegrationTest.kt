package com.example.project_gestoderisco.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.project_gestoderisco.data.model.TeamGoal
import com.example.project_gestoderisco.data.model.UserStats
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamificationIntegrationTest {

    private lateinit var repository: GamificationRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        
        // Limpa os dados de SharedPreferences antes do teste para garantir um estado limpo.
        // Nota: Atualmente UserStats é persistido em SharedPreferences.
        context.getSharedPreferences("gamification_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        
        repository = GamificationRepository(context)
    }

    @Test
    fun verifyUserStatsPersistenceAfterMission() {
        // 1. Configuração Inicial (Arrange)
        val initialStats = UserStats(level = 1, xp = 0)
        repository.saveUserStats(initialStats)

        val missionGoal = TeamGoal(
            id = "mission_integration_test",
            description = "Teste de Integração",
            targetValue = 1,
            currentValue = 1, // Meta atingida
            rewardXp = 500
        )

        // 2. Ação (Act)
        repository.distributeTeamGoalReward(missionGoal)

        // 3. Verificação (Assert)
        // Instanciamos um novo repositório para garantir que estamos lendo do disco (persistência)
        // e não apenas de um cache em memória.
        val newRepositoryInstance = GamificationRepository(context)
        val updatedStats = newRepositoryInstance.getUserStats()

        assertEquals("O XP deve ser atualizado para 500 após completar a missão", 500, updatedStats.xp)
    }
}