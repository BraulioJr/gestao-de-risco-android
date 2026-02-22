package com.example.project_gestoderisco.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.project_gestoderisco.data.local.RiskDao
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.worker.SyncRiskWorker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class RiskRepository(
    private val context: Context,
    private val riskDao: RiskDao,
    private val firestore: FirebaseFirestore
) {

    val allRisks: Flow<List<Risk>> = riskDao.getAllRisks()

    suspend fun saveRisk(risk: Risk) {
        // 1. Salva localmente no Room (Offline-First)
        val id = riskDao.insert(risk)
        
        // Cria uma cópia com o ID gerado para enviar ao Firebase
        val riskWithId = risk.copy(id = id, isSynced = false)

        try {
            // 2. Tenta sincronizar com o Firebase Firestore
            firestore.collection("risks")
                .document(id.toString())
                .set(riskWithId)
                .await()
            
            // 3. Se sucesso, atualiza status local para sincronizado
            riskDao.updateSyncStatus(id, true)
        } catch (e: Exception) {
            // Se falhar (sem internet), o dado já está salvo localmente (isSynced = false)
            // Agendamos o WorkManager para sincronizar quando a internet voltar
            e.printStackTrace()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncRiskWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "SyncRiskWork",
                ExistingWorkPolicy.KEEP, // Mantém o trabalho existente se já estiver agendado
                syncRequest
            )
        }
    }
}