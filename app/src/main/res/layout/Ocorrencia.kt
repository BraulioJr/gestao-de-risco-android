package com.example.gestaoderisco.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ocorrencias")
data class Ocorrencia(
    @PrimaryKey val id: String,
    val loja: String,
    val monitoramento: String,
    val produtos: String,
    val valor: Double,
    val acao: String,
    val relato: String,
    val data: Date,
    val urlEvidencia: String?,
    val usuarioId: String,
    // Adiciona um campo para controlar o status da sincronização
    val isSynced: Boolean = false
)