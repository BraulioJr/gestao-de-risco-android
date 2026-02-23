package com.example.project_gestoderisco.utils

import kotlin.math.log10

/**
 * Cérebro do "Protocolo de Foco Tático".
 * Calcula automaticamente a complexidade de uma ocorrência baseada em vetores de risco.
 * Substitui a subjetividade do operador por uma métrica matemática fria.
 */
object ComplexityCalculator {

    enum class ComplexityLevel(val multiplier: Double, val label: String) {
        LOW(1.0, "Nível 1 (Oportunidade)"),
        MEDIUM(1.5, "Nível 2 (Tático)"),
        HIGH(2.0, "Nível 3 (Estratégico)"),
        CRITICAL(3.0, "Nível 4 (Crime Organizado)")
    }

    data class RiskFactors(
        val value: Double,
        val modusOperandiWeight: Int, // 1 (Simples) a 5 (Tech/Violento)
        val suspectsCount: Int,
        val isHotspotTime: Boolean,
        val hasPriorHistory: Boolean
    )

    /**
     * Calcula o Score Vetorial de Complexidade.
     * Fórmula: (LogValor * 0.3) + (MO * 0.4) + (Articulação * 0.1) + (Contexto * 0.2)
     */
    fun calculate(factors: RiskFactors): ComplexityLevel {
        // 1. Vetor Financeiro (Logarítmico para evitar distorção por valores extremos)
        // Ex: R$ 100 -> 2.0 | R$ 1000 -> 3.0 | R$ 10.000 -> 4.0
        val valueScore = if (factors.value > 0) log10(factors.value) else 0.0

        // 2. Vetor de Modus Operandi (O peso da técnica)
        // Sacola de alumínio/Desacoplador = Peso alto
        val moScore = factors.modusOperandiWeight.toDouble()

        // 3. Vetor de Articulação (Quadrilha vs Lobo Solitário)
        val articulationScore = if (factors.suspectsCount > 1) 2.0 + (factors.suspectsCount * 0.5) else 1.0

        // 4. Vetor de Contexto (Histórico + Horário)
        var contextScore = 1.0
        if (factors.isHotspotTime) contextScore += 1.0
        if (factors.hasPriorHistory) contextScore += 1.5

        // CÁLCULO PONDERADO (A Lógica do Foco Tático)
        // Priorizamos o Modus Operandi (Técnica) sobre o Valor puro.
        val finalScore = (valueScore * 0.25) +
                         (moScore * 0.45) +
                         (articulationScore * 0.15) +
                         (contextScore * 0.15)

        return when {
            finalScore >= 5.5 -> ComplexityLevel.CRITICAL
            finalScore >= 3.5 -> ComplexityLevel.HIGH
            finalScore >= 2.0 -> ComplexityLevel.MEDIUM
            else -> ComplexityLevel.LOW
        }
    }

    /**
     * Retorna o peso padrão para Modus Operandi conhecidos.
     * Isso alimenta o vetor MO do cálculo acima.
     */
    fun getModusOperandiWeight(moType: String): Int {
        return when (moType) {
            "MO_ARMED_ROBBERY" -> 5 // Crítico
            "MO_JAMMER_DEVICE" -> 5 // Alta Tech
            "MO_LINED_BAG" -> 4     // Profissional (Sacola Alumínio)
            "MO_DETACHER_USE" -> 4  // Profissional
            "MO_TICKET_SWITCH" -> 3 // Intermediário
            "MO_BODY_CONCEAL" -> 2  // Comum
            "MO_BAG_CONCEAL" -> 2   // Comum
            else -> 1               // Oportunista
        }
    }

    /**
     * Aplica o "Fator de Cansaço" (Diminishing Returns) para evitar inflação de XP.
     * @param dailyCount Número de ocorrências já registradas hoje pelo usuário.
     */
    fun applyDiminishingReturns(baseXp: Int, dailyCount: Int, complexity: ComplexityLevel): Int {
        // Ocorrências Críticas/Altas NUNCA sofrem penalidade (incentivo à qualidade)
        if (complexity == ComplexityLevel.HIGH || complexity == ComplexityLevel.CRITICAL) {
            return (baseXp * complexity.multiplier).toInt()
        }

        // Ocorrências comuns sofrem decaimento após a 5ª do dia
        val decayFactor = if (dailyCount > 5) 1.0 / (1.0 + ((dailyCount - 5) * 0.1)) else 1.0
        return (baseXp * complexity.multiplier * decayFactor).toInt()
    }
}