package com.example.gestaoderisco.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.gestaoderisco.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.Date

object DashboardChartManager {

    data class ChartDataInput(
        val storeName: String,
        val occurrenceDate: Date,
        val registrationDate: Date
    )

    fun setupResponseTimeChart(context: Context, barChart: BarChart, dataList: List<ChartDataInput>) {
        // 1. Calcular Média por Loja
        val avgMap = dataList.groupBy { it.storeName }
            .mapValues { entry ->
                entry.value.map { (it.registrationDate.time - it.occurrenceDate.time) / 60000f }.average()
            }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        avgMap.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)
        }

        // 2. Configurar Dataset
        val dataSet = BarDataSet(entries, context.getString(R.string.response_time_chart_title))
        dataSet.color = ContextCompat.getColor(context, R.color.md_theme_primary)
        dataSet.valueTextColor = ContextCompat.getColor(context, R.color.md_theme_onSurface)
        dataSet.valueTextSize = 12f

        // 3. Configurar Linha de Alerta (30 min)
        val limitLine = LimitLine(30f, context.getString(R.string.limit_alert_label))
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.lineColor = ContextCompat.getColor(context, R.color.risk_level_high)
        limitLine.textColor = ContextCompat.getColor(context, R.color.risk_level_high)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP

        // 4. Aplicar ao Gráfico
        barChart.axisLeft.apply {
            removeAllLimitLines()
            addLimitLine(limitLine)
            axisMinimum = 0f
            if (axisMaximum < 40f) axisMaximum = 40f // Margem superior
        }
        barChart.axisRight.isEnabled = false
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.description.isEnabled = false
        barChart.data = BarData(dataSet)
        barChart.invalidate()
    }
}