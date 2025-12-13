package com.example.project_gestoderisco

import android.app.Application
import androidx.work.*
import com.example.project_gestoderisco.worker.SyncWorker
import java.util.concurrent.TimeUnit

class GestaoDeRiscoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupSyncWorker()
    }

    private fun setupSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            1, TimeUnit.HOURS // Repete a cada 1 hora
        ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_ocorrencias",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}