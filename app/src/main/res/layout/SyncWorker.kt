package com.example.gestaoderisco.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestaoderisco.repository.OcorrenciasRepository

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            OcorrenciasRepository().syncOcorrencias()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}