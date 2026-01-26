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

        // ... seus métodos existentes ...

    // Adicione esta Query para buscar os candidatos ao arquivamento
    // Correção: Nome da tabela 'ocorrencias' e campo 'dataRegistro' conforme a entidade
    @Query("SELECT * FROM ocorrencias WHERE status = :status AND dataRegistro < :dataLimite AND isSynced = 1")
    suspend fun getOcorrenciasParaArquivar(status: String, dataLimite: Long): List<Ocorrencia>

    // Adicione este método para deletar a lista
    @Delete
    suspend fun deleteList(ocorrencias: List<Ocorrencia>)
    
    @Query("SELECT * FROM ocorrencias WHERE tenantId = :tenantId")
    suspend fun getAll(tenantId: String): List<Ocorrencia>

    @Query("SELECT * FROM ocorrencias WHERE tenantId = :tenantId AND dataRegistro BETWEEN :startDate AND :endDate")
    suspend fun getByDateRange(tenantId: String, startDate: Long, endDate: Long): List<Ocorrencia>

    @Query("SELECT * FROM ocorrencias WHERE tenantId = :tenantId AND loja LIKE :searchQuery || '%' ORDER BY dataRegistro DESC")
    fun getOcorrencias(tenantId: String, searchQuery: String): Flow<List<Ocorrencia>>

    @Query("SELECT * FROM ocorrencias WHERE tenantId = :tenantId AND loja = :storeName ORDER BY dataRegistro DESC")
    fun getOcorrenciasForStore(tenantId: String, storeName: String): Flow<List<Ocorrencia>>

    @Query("SELECT * FROM ocorrencias WHERE id = :id LIMIT 1")
    suspend fun getOcorrenciaById(id: String): Ocorrencia?

    @Query("SELECT * FROM ocorrencias WHERE isSynced = 0")
    suspend fun getUnsyncedOcorrencias(): List<Ocorrencia>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ocorrencia: Ocorrencia)

    @Update
    suspend fun update(ocorrencia: Ocorrencia)
}