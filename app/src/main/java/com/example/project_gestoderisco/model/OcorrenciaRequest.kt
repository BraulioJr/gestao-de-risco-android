package com.example.project_gestoderisco.model

import com.google.gson.annotations.SerializedName

// Estrutura de dados enviada para a API
data class OcorrenciaRequest(
    @SerializedName("loja_id") // Nome da chave JSON no backend
    val lojaId: Int,
    @SerializedName("fiscal_id")
    val fiscalId: Int,
    @SerializedName("valor_recuperado")
    val valorRecuperado: Double,
    val produtos: String,
    @SerializedName("detalhe_monitoramento")
    val detalheMonitoramento: String,
    @SerializedName("tipo_acao")
    val tipoAcao: String,
    val relato: String
    // DataHora será geralmente gerada pelo backend
)