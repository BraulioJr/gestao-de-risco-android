package com.example.gestaoderisco.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestaoderisco.models.Ocorrencia
import kotlinx.coroutines.flow.Flow

@Dao
interface OcorrenciaDao {
    @Query("SELECT * FROM ocorrencias WHERE loja LIKE :searchQuery || '%' ORDER BY data DESC")
    fun getOcorrencias(searchQuery: String): Flow<List<Ocorrencia>>

    @Query("SELECT * FROM ocorrencias WHERE isSynced = 0")
    suspend fun getUnsyncedOcorrencias(): List<Ocorrencia>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ocorrencia: Ocorrencia)

    @Update
    suspend fun update(ocorrencia: Ocorrencia)
}