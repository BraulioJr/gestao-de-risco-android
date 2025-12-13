package com.example.project_gestoderisco.model

// Estrutura de dados recebida da API após um registro bem-sucedido
data class OcorrenciaResponse(
    val status: String, // Ex: "SUCESSO"
    val mensagem: String, // Ex: "Ocorrência registrada com ID 456."
    val id: Int? // O ID gerado pelo banco de dados

)