package com.example.gestaoderisco.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestaoderisco.data.AppDatabase
import java.util.Calendar

class ArchiveWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.ocorrenciaDao()

            // 1. Calcular a data limite (1 ano atrás a partir de hoje)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -1)
            val oneYearAgo = calendar.timeInMillis

            // 2. Buscar ocorrências que atendem aos critérios:
            // - Status: "Resolvido"
            // - Data: Mais antiga que 1 ano
            // - Sincronizado: true (Importante para não perder dados que ainda não subiram)
            val ocorrenciasAntigas = dao.getOcorrenciasParaArquivar(
                status = "Resolvido",
                dataLimite = oneYearAgo
            )

            if (ocorrenciasAntigas.isNotEmpty()) {
                Log.i("ArchiveWorker", "Arquivando ${ocorrenciasAntigas.size} ocorrências antigas.")
                
                // 3. Deletar do banco local para liberar espaço
                dao.deleteList(ocorrenciasAntigas)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ArchiveWorker", "Erro durante o arquivamento de dados", e)
            Result.retry()
        }
    }
}