package com.example.gestaoderisco.repository

import com.example.gestaoderisco.data.local.AppDatabase
import com.example.gestaoderisco.models.Ocorrencia
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OcorrenciasRepository {

    private val dao = AppDatabase.getInstance().ocorrenciaDao()
    private val firestore = FirebaseFirestore.getInstance()

    // Leitura agora vem do banco local (Room), permitindo acesso offline
    fun getOcorrencias(searchText: String): Flow<List<Ocorrencia>> {
        return dao.getOcorrencias(searchText)
    }

    fun getOcorrenciasForStore(storeName: String): Flow<List<Ocorrencia>> {
        return dao.getOcorrenciasForStore(storeName)
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
        val ocorrencia = dao.getOcorrencias("").first().find { it.id == ocorrenciaId }
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