package com.example.gestaoderisco.repository

import com.example.gestaoderisco.data.local.AppDatabase
import com.example.gestaoderisco.models.Loja
import com.example.gestaoderisco.models.Ocorrencia
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OcorrenciasRepository {

    private val dao = AppDatabase.getInstance().ocorrenciaDao()
    private val firestore = FirebaseFirestore.getInstance()

    private val currentTenantId = "c7b3851e-28ee-4262-bd6b-f917d5c47ec2"

    // Leitura agora vem do banco local (Room), permitindo acesso offline
    fun getOcorrencias(searchText: String): Flow<List<Ocorrencia>> {
        return dao.getOcorrencias(e

    fun getOcorrenciasForStore(storeName: String): Flow<List<Ocorrencia>> {
        return dao.getOcorrenciasForStore(currentTenantId, storeName)

    // Busca todas as lojas do Firestore. Em um app real, isso poderia ser cacheado.
    suspend fun getLojas(): List<Loja> {
        return try {
            firestore.collection("lojas").get().await().toObjects(Loja::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Salva localmente primeiro
    suspend fun salvarOcorrencia(ocorrencia: Ocorrencia) {
        if (ocorrencia.id.isEmpty()) {
            ocorrencia.id = UUID.randomUUID().toString()
        }
        ocorrencia.isSynced = false // Marca como pendente de sincronização
        dao.insert(ocorrencia)
        
        // Tenta sincronizar imediatamente (se houver rede, o Worker ou chamada direta resolverá)
        // Aqui deixamos para o Worker ou chamada explícita de sync
    }

    suspend fun updateStatus(ocorrenciaId: String, newStatus: String) {
        // Refatorado para ser mais eficiente, buscando apenas o item necessário.
        val ocorrencia = dao.getOcorrenciaById(ocorrenciaId)
        ocorrencia?.let {
            val updatedOcorrencia = it.copy(status = newStatus, isSynced = false)
            dao.update(updatedOcorrencia)
        }
    }

    // Função chamada pelo Worker para enviar dados pendentes
    suspend fun syncOcorrencias() {
        val unsynced = dao.getUnsyncedOcorrencias()
        val collection = firestore.collection("ocorrencias")

        for (ocorrencia in unsynced) {
            try {
                // Envia para o Firestore
                collection.document(ocorrencia.id)
                    .set(ocorrencia, SetOptions.merge()) // Merge para não sobrescrever campos se existirem
                    .await()

                // Se sucesso, marca como sincronizado localmente
                val syncedOcorrencia = ocorrencia.copy(isSynced = true)
                dao.update(syncedOcorrencia)
            } catch (e: Exception) {
                e.printStackTrace()
                // Mantém isSynced = false para tentar novamente depois
            }
        }
    }
}