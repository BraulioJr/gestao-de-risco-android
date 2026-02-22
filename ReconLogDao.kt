package com.example.gestaoderisco.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestaoderisco.models.ReconLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ReconLogDao {
    @Insert
    suspend fun insert(log: ReconLog)

    @Query("SELECT * FROM recon_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ReconLog>>
}