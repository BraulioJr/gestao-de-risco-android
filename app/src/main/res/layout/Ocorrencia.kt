package com.example.gestaoderisco.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Modelo de dados para uma Ocorrência de Risco/Perda.
 * Mapeado para o Firestore.
 */
@Entity(tableName = "ocorrencias")
data class Ocorrencia(
    @PrimaryKey var id: String = "",
    val loja: String = "",
    val data: Date = Date(),
    val valor: Double = 0.0,
    val produtos: String = "",
    val acao: String = "",
    val status: String = "Aberto", // Campo para case management
    val fundadaSuspeita: String = "", // Campo adicionado conforme Art. 244 CPP
    val relato: String = "",
    val urlEvidencia: String? = null,
    val isSynced: Boolean = false
)