package com.example.gestaoderisco.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recon_logs")
data class ReconLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val message: String,
    val type: String, // MOVEMENT, TARGET, RISK, ASSET, PHOTO
    val attachmentPath: String? = null
)