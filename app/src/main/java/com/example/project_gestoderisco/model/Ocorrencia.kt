package com.example.project_gestoderisco.model

// Este é o 'M' do MVVM (Model)
data class Ocorrencia(
    val lojaId: Int,
    val fiscalId: Int, // Será obtido do usuário logado
    val valorRecuperado: Double,
    val produtos: String,
    val detalheMonitoramento: String,
    val tipoAcao: String, // "ABORDAGEM_REALIZADA" ou "APENAS_MONITORADA"
    val relato: String,
    val dataHora: Long = System.currentTimeMillis()
)