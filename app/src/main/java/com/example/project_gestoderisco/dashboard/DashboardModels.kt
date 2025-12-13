package com.example.project_gestoderisco.dashboard

data class KPIOverview(
    val totalValorRecuperado: Double,
    val totalOcorrencias: Int,
    val indiceEfetividade: Double // 0..1
)

data class ProductStat(val produto: String, val count: Int)

data class FiscalRankingItem(
    val fiscalId: String,
    val nome: String? = null, // Será preenchido depois, se necessário
    val totalRecuperado: Double,
    val qtdOcorrencias: Int,
    val eficiencia: Double // 0..1
)
