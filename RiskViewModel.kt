package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.gamification.GamificationEngine
import com.example.project_gestoderisco.gamification.InvestigationCaseFactors
import com.example.project_gestoderisco.gamification.XpCalculationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiskViewModel @Inject constructor(
    private val gamificationEngine: GamificationEngine
    // private val userRepository: UserRepository // Para buscar dailyCount e streak reais
) : ViewModel() {

    // Estado observável pela UI (Activity/Fragment) para exibir o feedback de XP
    private val _xpResult = MutableStateFlow<XpCalculationResult?>(null)
    val xpResult: StateFlow<XpCalculationResult?> = _xpResult.asStateFlow()

    // Estado para o preview de XP em tempo real
    private val _xpPreview = MutableStateFlow<XpCalculationResult?>(null)
    val xpPreview: StateFlow<XpCalculationResult?> = _xpPreview.asStateFlow()

    /**
     * Chamado quando o usuário resolve um caso ou conclui uma investigação.
     * Calcula o XP ganho com base nos fatores do caso e no perfil do usuário.
     */
    fun resolveInvestigation(caseFactors: InvestigationCaseFactors) {
        viewModelScope.launch {
            // 1. Obter dados de contexto do usuário (Simulado para este exemplo)
            // Em produção: val profile = userRepository.getProfile()
            val dailyCount = 3 // Ex: 3ª ocorrência do dia
            val currentStreak = 5 // Ex: 5 dias seguidos
            
            val streakBonus = if (currentStreak >= 3) 50 else 0

            // 2. Calcular XP usando a Engine
            val result = gamificationEngine.calculateXpForCase(
                caseFactors = caseFactors,
                dailyCount = dailyCount,
                streakBonus = streakBonus
            )

            // 3. Emitir resultado para a UI (exibir Dialog de Level Up/XP)
            _xpResult.value = result
        }
    }

    /**
     * Calcula uma estimativa de XP em tempo real com base nos dados atuais do formulário.
     * Isso incentiva o usuário a preencher mais campos para aumentar a precisão e o XP.
     */
    fun updateXpPreview(caseFactors: InvestigationCaseFactors) {
        viewModelScope.launch {
            // Para o preview, usamos a mesma lógica de contexto do usuário
            val dailyCount = 3
            val currentStreak = 5
            val streakBonus = if (currentStreak >= 3) 50 else 0

            val result = gamificationEngine.calculateXpForCase(
                caseFactors = caseFactors,
                dailyCount = dailyCount,
                streakBonus = streakBonus
            )
            _xpPreview.value = result
        }
    }

    fun onXpResultDisplayed() {
        _xpResult.value = null
    }
}