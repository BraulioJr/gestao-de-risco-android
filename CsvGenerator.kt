package com.example.gestaoderisco.utils

import android.content.Context
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import java.io.OutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MarketInfo(
    val context: String,
    val inflationIndex: String,
    val unemploymentRate: String
)

class CsvGenerator(private val context: Context) {

    fun generateCsv(outputStream: OutputStream, dataList: List<Ocorrencia>, marketDataMap: Map<String, MarketInfo> = emptyMap()) {
        // Usa UTF-8 e adiciona BOM para compatibilidade com Excel
        val writer = outputStream.bufferedWriter(Charset.forName("UTF-8"))
        writer.write("\uFEFF")

        // --- BRANDING INSTITUCIONAL ---
        val institution = context.getString(R.string.report_institution_name)
        val title = context.getString(R.string.report_title)
        val subtitle = context.getString(R.string.report_subtitle)
        val cityYear = context.getString(R.string.report_city_year)
        val generatedLabel = context.getString(R.string.report_generated_at)
        val dateGen = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())

        writer.write(escapeCsv(institution))
        writer.newLine()
        writer.write(escapeCsv(title))
        writer.newLine()
        writer.write(escapeCsv(subtitle))
        writer.newLine()
        writer.write(escapeCsv(cityYear))
        writer.newLine()
        writer.write("$generatedLabel $dateGen")
        writer.newLine()
        writer.newLine() // Linha em branco para separar do cabeçalho da tabela

        // Cabeçalho
        val header = listOf(
            context.getString(R.string.csv_header_date),
            context.getString(R.string.csv_header_store),
            context.getString(R.string.csv_header_category),
            context.getString(R.string.csv_header_value),
            context.getString(R.string.csv_header_description),
            context.getString(R.string.csv_header_market_context),
            context.getString(R.string.csv_header_inflation_index),
            context.getString(R.string.csv_header_unemployment_rate)
        ).joinToString(";")
        
        writer.write(header)
        writer.newLine()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
        val marketKeyFormat = SimpleDateFormat("MM/yyyy", Locale("pt", "BR"))

        for (item in dataList) {
            // Busca dados de mercado correspondentes ao mês da ocorrência
            val marketKey = marketKeyFormat.format(Date(item.timestamp))
            val marketInfo = marketDataMap[marketKey]

            val row = listOf(
                dateFormat.format(Date(item.timestamp)),
                escapeCsv(item.loja),
                escapeCsv(item.categoriaProduto),
                String.format(Locale("pt", "BR"), "%.2f", item.valorEstimado),
                escapeCsv(item.descricao),
                escapeCsv(marketInfo?.context ?: "N/A"),
                escapeCsv(marketInfo?.inflationIndex ?: "-"),
                escapeCsv(marketInfo?.unemploymentRate ?: "-")
            ).joinToString(";")
            
            writer.write(row)
            writer.newLine()
        }

        // --- RODAPÉ DE SEGURANÇA (Personalidade: Confiável e Vigilante) ---
        writer.newLine()
        writer.newLine()
        writer.write(escapeCsv(context.getString(R.string.report_confidentiality)))
        writer.newLine()
        writer.write(escapeCsv(context.getString(R.string.report_security_warning)))
        
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