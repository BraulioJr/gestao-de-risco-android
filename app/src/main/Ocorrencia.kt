package com.example.gestaoderisco.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ocorrencias")
data class Ocorrencia(
    @PrimaryKey var id: String = "",
    val loja: String,
    val data: Date, // Data do Fato (Ocorrência)
    val dataRegistro: Date = Date(), // Data do Registro no Sistema (Novo campo para KPI)
    val valor: Double,
    val produtos: String,
    val acao: String,
    val status: String,
    val fundadaSuspeita: String,
    val relato: String,
    var isSynced: Boolean = false
)