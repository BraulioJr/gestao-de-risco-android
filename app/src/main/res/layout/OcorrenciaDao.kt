package com.example.gestaoderisco.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OcorrenciaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOcorrencia(ocorrencia: Ocorrencia)

    @Query("SELECT * FROM ocorrencias WHERE isSynced = 0 ORDER BY data DESC")
    fun getUnsyncedOcorrencias(): Flow<List<Ocorrencia>>

    @Query("SELECT * FROM ocorrencias ORDER BY data DESC")
    fun getAllOcorrencias(): Flow<List<Ocorrencia>>

    @Query("SELECT * FROM ocorrencias WHERE id = :ocorrenciaId")
    fun getOcorrenciaById(ocorrenciaId: String): Flow<Ocorrencia>

}