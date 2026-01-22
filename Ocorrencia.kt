package com.example.gestaoderisco.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ocorrencias")
data class Ocorrencia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loja: String,
    val categoriaProduto: String,
    val valorEstimado: Double,
    val acaoRealizada: String,
    val relato: String,
    val dataRegistro: Long = System.currentTimeMillis(),
    val perfilFurtante: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val tenantId: String = "c7b3851e-28ee-4262-bd6b-f917d5c47ec2",
    val status: String = "Aberto", // Aberto, Em Investigação, Resolvido
    val isSynced: Boolean = false
)