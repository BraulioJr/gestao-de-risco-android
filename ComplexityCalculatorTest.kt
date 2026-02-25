package com.example.project_gestoderisco.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ComplexityCalculatorTest {

    @Test
    fun `calculate returns CRITICAL level for high risk factors`() {
        // Arrange
        // Cenário de Extremo Risco:
        // Valor: R$ 10.000.000 (Log10 = 7.0) -> Score: 7.0 * 0.25 = 1.75
        // MO: 5 (Assalto Armado/Tech) -> Score: 5.0 * 0.45 = 2.25
        // Suspeitos: 10 (Quadrilha) -> Score: 2.0 + (10 * 0.5) = 7.0 -> 7.0 * 0.15 = 1.05
        // Contexto: Hotspot + Histórico -> Score: 1.0 + 1.0 + 1.5 = 3.5 -> 3.5 * 0.15 = 0.525
        // Total: 1.75 + 2.25 + 1.05 + 0.525 = 5.575
        // Limite CRÍTICO >= 5.5
        
        val criticalFactors = ComplexityCalculator.RiskFactors(
            value = 10_000_000.0,
            modusOperandiWeight = 5,
            suspectsCount = 10,
            isHotspotTime = true,
            hasPriorHistory = true
        )

        // Act
        val result = ComplexityCalculator.calculate(criticalFactors)

        // Assert
        assertEquals(ComplexityCalculator.ComplexityLevel.CRITICAL, result)
    }
}