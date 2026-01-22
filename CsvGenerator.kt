package com.example.gestaoderisco.utils

import android.content.Context
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import java.io.OutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvGenerator(private val context: Context) {

    fun generateCsv(outputStream: OutputStream, dataList: List<Ocorrencia>) {
        // Usa UTF-8 e adiciona BOM para compatibilidade com Excel
        val writer = outputStream.bufferedWriter(Charset.forName("UTF-8"))
        writer.write("\uFEFF")

        // Cabeçalho
        val header = listOf(
            context.getString(R.string.csv_header_date),
            context.getString(R.string.csv_header_store),
            context.getString(R.string.csv_header_category),
            context.getString(R.string.csv_header_value),
            context.getString(R.string.csv_header_description)
        ).joinToString(";")
        
        writer.write(header)
        writer.newLine()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in dataList) {
            val row = listOf(
                dateFormat.format(Date(item.timestamp)),
                escapeCsv(item.loja),
                escapeCsv(item.categoriaProduto),
                String.format(Locale("pt", "BR"), "%.2f", item.valorEstimado),
                escapeCsv(item.descricao)
            ).joinToString(";")
            
            writer.write(row)
            writer.newLine()
        }
        
        writer.flush()
        writer.close()
    }

    // Trata caracteres especiais que podem quebrar o CSV
    private fun escapeCsv(value: String?): String {
        if (value == null) return ""
        var escaped = value.replace("\"", "\"\"")
        if (escaped.contains(";") || escaped.contains("\n") || escaped.contains("\"")) {
            escaped = "\"$escaped\""
        }
        return escaped
    }
}