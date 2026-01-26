package com.example.gestaoderisco.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.repository.OcorrenciasRepository
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeeklyReportWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = OcorrenciasRepository(applicationContext)

    override suspend fun doWork(): Result {
        return try {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val oneWeekAgo = cal.time.time

            val allOcorrencias = repository.getOcorrencias("").first()
            val filteredList = allOcorrencias.filter { it.dataRegistro > oneWeekAgo }

            if (filteredList.isNotEmpty()) {
                val csvFile = generateCsvFile(filteredList)
                sendNotification(csvFile)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun generateCsvFile(data: List<Ocorrencia>): File {
        val csvHeader = "Loja,Data,Valor,Categoria,Ação,Relato\n"
        val sb = StringBuilder()
        sb.append(csvHeader)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in data) {
            val dateStr = dateFormat.format(Date(item.dataRegistro))
            val loja = escapeCsv(item.loja)
            val produtos = escapeCsv(item.categoriaProduto)
            val acao = escapeCsv(item.acaoRealizada)
            val relato = escapeCsv(item.relato)

            sb.append("$loja,$dateStr,${item.valorEstimado},$produtos,$acao,$relato\n")
        }

        val cachePath = File(applicationContext.cacheDir, "exports")
        cachePath.mkdirs()
        val fileName = "relatorio_semanal_${System.currentTimeMillis()}.csv"
        val file = File(cachePath, fileName)
        val stream = FileOutputStream(file)
        stream.write(sb.toString().toByteArray())
        stream.close()
        return file
    }

    private fun escapeCsv(value: String): String {
        var escaped = value.replace("\"", "\"\"")
        if (escaped.contains(",") || escaped.contains("\n")) {
            escaped = "\"$escaped\""
        }
        return escaped
    }

    private fun sendNotification(file: File) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weekly_report_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Relatórios Semanais", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val contentUri: Uri = FileProvider.getUriForFile(applicationContext, "${applicationContext.packageName}.provider", file)

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Relatório Semanal")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, Intent.createChooser(emailIntent, "Enviar"), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Relatório Semanal Pronto")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}