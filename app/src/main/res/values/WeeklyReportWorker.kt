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

    private val repository = OcorrenciasRepository()

    override suspend fun doWork(): Result {
        return try {
            // 1. Define o período (Última semana)
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val oneWeekAgo = cal.time

            // 2. Busca dados (Simulando busca de todos e filtrando, pois o repo atual é limitado)
            // Nota: Em produção, idealmente o repositório teria um método getOcorrenciasByDateRange
            val allOcorrencias = repository.getOcorrencias("").first()
            val filteredList = allOcorrencias.filter { it.data.after(oneWeekAgo) }

            if (filteredList.isNotEmpty()) {
                // 3. Gera o CSV
                val csvFile = generateCsvFile(filteredList)

                // 4. Notifica o usuário para enviar o e-mail
                sendNotification(csvFile)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun generateCsvFile(data: List<Ocorrencia>): File {
        val csvHeader = "Loja,Data,Valor,Categoria,Ação,Status,Fundada Suspeita,Relato\n"
        val sb = StringBuilder()
        sb.append(csvHeader)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in data) {
            val dateStr = dateFormat.format(item.data)
            val loja = escapeCsv(item.loja)
            val produtos = escapeCsv(item.produtos)
            val acao = escapeCsv(item.acao)
            val status = escapeCsv(item.status)
            val suspeita = escapeCsv(item.fundadaSuspeita)
            val relato = escapeCsv(item.relato)

            sb.append("$loja,$dateStr,${item.valor},$produtos,$acao,$status,$suspeita,$relato\n")
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

        val contentUri: Uri = FileProvider.getUriForFile(
            applicationContext,
            "${applicationContext.packageName}.provider",
            file
        )

        val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("pp_coordinator_email", null)

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Relatório Semanal de Prevenção de Perdas")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            if (!email.isNullOrBlank()) {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Cria um PendingIntent que abre o chooser de e-mail
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent.createChooser(emailIntent, "Enviar Relatório"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_name) // Ícone personalizado para consistência
            .setContentTitle("Relatório Semanal Pronto")
            .setContentText("Toque para enviar o relatório CSV por e-mail.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}