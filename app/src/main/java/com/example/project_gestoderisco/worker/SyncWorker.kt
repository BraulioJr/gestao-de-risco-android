package com.example.project_gestoderisco.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.project_gestoderisco.data.local.AppDatabase
import com.example.project_gestoderisco.repository.OcorrenciaRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).ocorrenciaDao()
        val repository = OcorrenciaRepository(dao)

        return try {
            val unsyncedOcorrencias = repository.getUnsyncedOcorrencias()
            if (unsyncedOcorrencias.isEmpty()) {
                return Result.success()
            }

            val syncedIds = mutableListOf<String>()
            for (ocorrencia in unsyncedOcorrencias) {
                var evidenciaUrl: String? = null

                // 1. Upload da imagem, se existir
                if (!ocorrencia.evidenciaLocalPath.isNullOrEmpty()) {
                    try {
                        val fileUri = Uri.parse(ocorrencia.evidenciaLocalPath)
                        val storageRef = storage.reference.child("evidencias/${ocorrencia.id}.jpg")
                        storageRef.putFile(fileUri).await()
                        evidenciaUrl = storageRef.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        // Falha no upload da imagem, continuamos para sincronizar os dados de texto
                    }
                }

                // 2. Preparar o objeto para o Firestore
                val firestoreOcorrencia = hashMapOf(
                    "localId" to ocorrencia.id,
                    "lojaId" to ocorrencia.lojaId,
                    "fiscalId" to ocorrencia.fiscalId,
                    "valorRecuperado" to ocorrencia.valorRecuperado,
                    "produtos" to ocorrencia.produtos,
                    "detalheMonitoramento" to ocorrencia.detalheMonitoramento,
                    "tipoAcao" to ocorrencia.tipoAcao,
                    "relato" to ocorrencia.relato,
                    "dataHora" to ocorrencia.dataHora,
                    "evidenciaUrl" to evidenciaUrl
                )

                // 3. Salvar no Firestore
                firestore.collection("ocorrencias").add(firestoreOcorrencia).await()

                // 4. Marcar como sincronizado
                syncedIds.add(ocorrencia.id)
            }

            if (syncedIds.isNotEmpty()) {
                repository.markAsSynced(syncedIds)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}