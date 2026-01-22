package com.example.gestaoderisco.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.gestaoderisco.R
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Gerenciador responsável por criar o layout e gerar o arquivo CSV de ocorrências.
 * Implementa as regras de negócio de "Alto Valor" e "Tempo de Resposta".
 */
object CsvExportManager {

    // DTO para transporte de dados na exportação
    data class RiskExportData(
        val occurrenceDate: Date,
        val registrationDate: Date,
        val storeName: String,
        val category: String,
        val estimatedValue: Double,
        val description: String
    )

    fun generateAndShareCsv(context: Context, dataList: List<RiskExportData>) {
        val file = generateCsvFile(context, dataList)
        if (file != null) {
            shareCsvByEmail(context, file)
        }
    }

    private fun generateCsvFile(context: Context, dataList: List<RiskExportData>): File? {
        val fileName = "relatorio_riscos_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)

        return try {
            val writer = FileWriter(file)

            // 1. Cabeçalho
            val headers = listOf(
                context.getString(R.string.csv_header_date),
                context.getString(R.string.csv_header_store),
                context.getString(R.string.csv_header_category),
                context.getString(R.string.csv_header_is_high_value).uppercase(),
                context.getString(R.string.csv_header_value),
                context.getString(R.string.csv_header_response_time), // Nova Coluna
                context.getString(R.string.csv_header_description)
            )
            writer.append(headers.joinToString(",") { escapeCsv(it) }).append("\n")

            // 2. Dados
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            for (item in dataList) {
                val isHighValue = item.category.contains("Alto Risco", ignoreCase = true) ||
                                  item.category == "Eletrônicos e Eletroportáteis"
                
                val responseTime = calculateResponseTime(item.occurrenceDate, item.registrationDate)

                val row = listOf(
                    dateFormat.format(item.occurrenceDate),
                    item.storeName,
                    item.category,
                    if (isHighValue) "SIM" else "NÃO",
                    String.format(Locale("pt", "BR"), "%.2f", item.estimatedValue),
                    responseTime,
                    item.description
                )
                writer.append(row.joinToString(",") { escapeCsv(it) }).append("\n")
            }

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateResponseTime(occurrence: Date, registration: Date): String {
        val diff = registration.time - occurrence.time
        if (diff < 0) return "N/A"
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        return String.format("%02dh %02dmin", hours, minutes)
    }

    private fun shareCsvByEmail(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Relatório de Riscos e Ação Tática")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Enviar Relatório via:"))
    }

    private fun escapeCsv(value: String): String =
        if (value.contains(",") || value.contains("\"")) "\"${value.replace("\"", "\"\"")}\"" else value
}