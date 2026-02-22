package com.example.project_gestoderisco.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project_gestoderisco.model.Risk
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(risk: Risk): Long

    @Query("UPDATE risks SET isSynced = :synced WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, synced: Boolean)

    @Query("SELECT * FROM risks WHERE isSynced = 0")
    suspend fun getUnsyncedRisks(): List<Risk>

    @Query("SELECT * FROM risks ORDER BY id DESC")
    fun getAllRisks(): Flow<List<Risk>>
}