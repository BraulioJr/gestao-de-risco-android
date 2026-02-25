package com.example.project_gestoderisco.ui.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.data.model.ForensicReport
import com.example.project_gestoderisco.data.model.StressEvent
import com.example.project_gestoderisco.data.model.UserStats
import com.example.project_gestoderisco.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val repository: GamificationRepository
) : ViewModel() {

    private val _userStats = MutableStateFlow(repository.getUserStats())
    val userStats = _userStats.asStateFlow()

    // Provedor de tempo para facilitar testes unitários (pode ser mockado)
    var timeProvider: () -> Long = { System.currentTimeMillis() }

    private val _isStressActive = MutableStateFlow(false)
    val isStressActive = _isStressActive.asStateFlow()

    private val _forensicReport = MutableStateFlow<ForensicReport?>(null)
    val forensicReport = _forensicReport.asStateFlow()

    private var stressStartTime: Long = 0
    private var stressJob: Job? = null

    // Simula a chegada de um evento de mercado (Cisne Negro)
    fun triggerMarketCrash() {
        if (_isStressActive.value) return
        
        _isStressActive.value = true
        stressStartTime = timeProvider()
        
        // Auto-fail se não reagir em 30 segundos
        stressJob = viewModelScope.launch {
            delay(30_000)
            if (_isStressActive.value) {
                _isStressActive.value = false
                // Lógica de falha poderia ser implementada aqui
            }
        }
    }

    fun onMitigateAction() {
        if (_isStressActive.value) {
            val reactionTimeMs = timeProvider() - stressStartTime
            val reactionTimeSeconds = (reactionTimeMs / 1000).toInt()
            
            _isStressActive.value = false
            stressJob?.cancel()

            // Simulação de evento e posição para o cálculo
            val currentEvent = StressEvent("Flash Crash", 0.8)
            val report = generateForensicReport(currentEvent, reactionTimeSeconds, 10000.0)
            _forensicReport.value = report

            // Atualiza o XP baseado na nota do relatório
            val xpGained = when (report.disciplineGrade) {
                "S" -> 1000
                "B" -> 500
                else -> 100
            }

            val current = _userStats.value
            val newXp = current.xp + xpGained
            val newLevel = (newXp / 1000) + 1 // Sobe de nível a cada 1000 XP
            
            _userStats.value = current.copy(
                xp = newXp,
                level = newLevel,
                survivedCrashes = current.survivedCrashes + 1
            )
            
            // Persistir dados
            repository.saveUserStats(_userStats.value)
        }
    }

    fun dismissReport() {
        _forensicReport.value = null
    }

    private fun generateForensicReport(
        event: StressEvent,
        responseTime: Int,
        positionSize: Double
    ): ForensicReport {
        val maxLossPercent = event.severity * 0.5 // Ex: Severidade 0.8 = 40% de queda
        val potentialLoss = positionSize * maxLossPercent

        // Penalidade por lentidão: após 10s, a perda é total dentro do cenário
        val mitigationFactor = (10 - responseTime.coerceIn(0, 10)) / 10.0
        val savedCapital = potentialLoss * mitigationFactor

        val grade = when {
            responseTime <= 2 -> "S" // Ultra-rápido
            responseTime <= 5 -> "B" // Eficiente
            else -> "D" // Lento, mas sobreviveu
        }

        return ForensicReport(
            eventName = event.name,
            actionTaken = "Redução Automática de Exposição",
            responseTimeSeconds = responseTime,
            savedCapital = savedCapital,
            potentialLoss = potentialLoss,
            disciplineGrade = grade,
            insight = if (grade == "S") "Excelente! Reação instintiva preserva o patrimônio."
                      else "Cuidado: a hesitação no mercado custa caro. Pratique a execução."
        )
    }
}