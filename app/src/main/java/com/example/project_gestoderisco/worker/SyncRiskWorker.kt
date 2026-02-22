package com.example.project_gestoderisco.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.project_gestoderisco.data.local.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncRiskWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.riskDao()
        val firestore = FirebaseFirestore.getInstance()

        return try {
            val unsyncedRisks = dao.getUnsyncedRisks()

            for (risk in unsyncedRisks) {
                // Envia para o Firestore
                firestore.collection("risks")
                    .document(risk.id.toString())
                    .set(risk.copy(isSynced = false)) // Envia o objeto
                    .await()

                // Se sucesso, marca como sincronizado localmente
                dao.updateSyncStatus(risk.id, true)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry() // Tenta novamente se falhar
        }
    }
}