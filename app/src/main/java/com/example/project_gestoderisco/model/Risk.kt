package com.example.project_gestoderisco.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "risks")
data class Risk(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val probability: Int,
    val impact: Int,
    val impactAnalysis: String, // Novo campo
    val mitigationActions: String, // Novo campo
    val isSynced: Boolean = false
)