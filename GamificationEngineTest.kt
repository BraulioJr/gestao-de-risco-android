package com.example.project_gestoderisco.gamification

import com.example.project_gestoderisco.utils.ComplexityCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GamificationEngineTest {

    private lateinit var gamificationEngine: GamificationEngine

    @Before
    fun setUp() {
        // Instancia o GamificationEngine para cada teste
        gamificationEngine = GamificationEngine()
    }

    @Test
    fun `calculateXpForCase com complexidade MEDIA deve retornar o XP correto`() {
        // 1. CENÁRIO (Arrange)
        // Fatores de risco que resultam em complexidade MÉDIA (score entre 2.0 e 3.5)
        val mediumComplexityFactors = ComplexityCalculator.RiskFactors(
            value = 150.0, // log10(150) ≈ 2.17
            modusOperandiWeight = 3, // "Troca de Etiquetas"
            suspectsCount = 1,
            isHotspotTime = false,
            hasPriorHistory = false
        )
        // Score: (2.17*0.25) + (3*0.45) + (1*0.15) + (1*0.15) ≈ 2.19 -> MÉDIO

        val caseFactors = InvestigationCaseFactors(
            baseActionXp = 25, // Abrir o caso
            missions = listOf(
                GameMission("intel_visual", 50),
                GameMission("rastreio_rota", 40)
            ), // Total missões = 90 XP
            precisionMultiplier = 1.2, // Relatório com >50% de campos opcionais
            riskFactors = mediumComplexityFactors
        )

        val dailyCount = 3 // Menos de 5, sem decaimento de XP
        val streakBonus = 50 // Bônus por 3 dias de sequência

        // 2. AÇÃO (Act)
        val result = gamificationEngine.calculateXpForCase(
            caseFactors = caseFactors,
            dailyCount = dailyCount,
            streakBonus = streakBonus
        )

        // 3. VERIFICAÇÃO (Assert)

        // Cálculo esperado:
        // XP Base Total = 25 (caso) + 90 (missões) = 115
        // Nível de Complexidade = MÉDIO (multiplicador 1.5x)
        // Fator de Decaimento (dailyCount < 5) = 1.0x
        // XP Ajustado = 115 * 1.5 * 1.0 = 172.5 -> 172 (Int)
        // XP com Precisão = 172 * 1.2 (precisão) = 206.4 -> 206 (Int)
        // XP Final = 206 + 50 (streak) = 256
        val expectedXp = 256

        assertEquals(expectedXp, result.finalXp)

        // Validações adicionais para garantir a transparência do cálculo
        assertEquals(25, result.basePoints)
        assertEquals(90, result.missionPoints)
        assertEquals(50, result.streakBonus)
        assertEquals(1.2, result.appliedMultipliers["Precisão"])
        assertEquals(1.5, result.appliedMultipliers["Complexidade (Nível 2 (Tático))"])
    }
}