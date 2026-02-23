package com.example.project_gestoderisco.gamification

import java.io.Serializable
import com.example.project_gestoderisco.utils.ComplexityCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modelo de dados para o perfil do jogador.
 */
data class PlayerProfile(
    val userId: String,
    val currentXp: Long,
    val currentRank: String, // Ex: "Guardião Tático"
    val dailyStreak: Int,
    val honorPoints: Int
)

/**
 * Modelo de dados para o resultado de um cálculo de XP, permitindo que a UI
 * mostre um breakdown detalhado para o usuário.
 */
data class XpCalculationResult(
    val finalXp: Int,
    val basePoints: Int,
    val missionPoints: Int,
    val streakBonus: Int,
    val appliedMultipliers: Map<String, Double> // Ex: "Precisão" to 1.5
) : Serializable

/**
 * Representa uma ação ou missão dentro de um caso de investigação.
 */
data class GameMission(
    val missionId: String,
    val xpValue: Int
)

/**
 * Representa o caso de investigação completo com todos os fatores de pontuação.
 */
data class InvestigationCaseFactors(
    val baseActionXp: Int,
    val missions: List<GameMission>,
    val precisionMultiplier: Double, // 1.0 to 1.5
    val riskFactors: ComplexityCalculator.RiskFactors
)

/**
 * Motor central da gamificação.
 * Centraliza toda a lógica de cálculo de XP e progressão.
 * Projetado para ser injetado em ViewModels via Hilt.
 */
@Singleton
class GamificationEngine @Inject constructor(
    // private val userRepository: UserRepository // Para carregar/salvar perfil
) {
    // Exemplo de perfil de jogador, a ser carregado do repositório.
    private val _playerProfile = MutableStateFlow<PlayerProfile?>(
        PlayerProfile("user123", 31000, "Guardião Tático", 2, 1500)
    )
    val playerProfile: StateFlow<PlayerProfile?> = _playerProfile

    /**
     * Calcula o XP final para um caso de investigação concluído.
     *
     * @param caseFactors Os fatores do caso (XP base, missões, etc.).
     * @param dailyCount O número de ocorrências que o usuário já registrou hoje.
     * @param streakBonus O bônus de sequência a ser aplicado.
     * @return Um [XpCalculationResult] com o XP final e o breakdown.
     */
    fun calculateXpForCase(
        caseFactors: InvestigationCaseFactors,
        dailyCount: Int,
        streakBonus: Int
    ): XpCalculationResult {

        val totalMissionXp = caseFactors.missions.sumOf { it.xpValue }
        val totalBaseXp = caseFactors.baseActionXp + totalMissionXp

        // 1. Calcular a complexidade usando o cérebro da IA
        val complexityLevel = ComplexityCalculator.calculate(caseFactors.riskFactors)

        // 2. Aplicar o decaimento para evitar inflação de XP
        val adjustedBaseXp = ComplexityCalculator.applyDiminishingReturns(
            baseXp = totalBaseXp,
            dailyCount = dailyCount,
            complexity = complexityLevel
        )

        // 3. Aplicar multiplicador de precisão
        val finalXp = (adjustedBaseXp * caseFactors.precisionMultiplier).toInt() + streakBonus

        return XpCalculationResult(
            finalXp = finalXp,
            basePoints = caseFactors.baseActionXp,
            missionPoints = totalMissionXp,
            streakBonus = streakBonus,
            appliedMultipliers = mapOf(
                "Precisão" to caseFactors.precisionMultiplier,
                "Complexidade (${complexityLevel.label})" to complexityLevel.multiplier
            )
        )
    }
}