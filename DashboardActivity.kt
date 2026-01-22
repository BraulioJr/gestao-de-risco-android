package com.example.gestaoderisco

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.gestaoderisco.data.AppDatabase
import com.example.gestaoderisco.model.Ocorrencia
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart
    private lateinit var scatterChart: ScatterChart
    private lateinit var bubbleChart: BubbleChart
    private lateinit var spinnerPeriod: AutoCompleteTextView
    private lateinit var spinnerStore: AutoCompleteTextView
    private lateinit var btnExportPdf: Button
    private lateinit var btnViewMap: Button
    private var currentPeriod: Periodo = Periodo.TODOS
    private var selectedStore: String = "Todas"
    private var currentFilteredList: List<Ocorrencia> = emptyList()
    private val currentTenantId = "c7b3851e-28ee-4262-bd6b-f917d5c47ec2" // TODO: Recuperar do Token de Autenticação/Sessão do Usuário (ex: Firebase Auth)

    private enum class Periodo {
        TODOS, ESTE_MES, MES_PASSADO, ESTE_ANO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        pieChart = findViewById(R.id.pieChartShoplifter)
        barChart = findViewById(R.id.barChartFinancial)
        lineChart = findViewById(R.id.lineChartEvolution)
        scatterChart = findViewById(R.id.scatterChartRisk)
        bubbleChart = findViewById(R.id.bubbleChartCriminalSpot)
        spinnerPeriod = findViewById(R.id.spinnerPeriod)
        spinnerStore = findViewById(R.id.spinnerStore)
        btnExportPdf = findViewById(R.id.btnExportDashboardPdf)
        btnViewMap = findViewById(R.id.btnViewMap)

        setupPeriodFilter()
        setupStoreFilter()

        btnExportPdf.setOnClickListener {
            exportFilteredDataToPdf()
        }

        btnViewMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        
        // Carrega dados iniciais (Todos)
        loadDashboardData(Periodo.TODOS)
    }

    private fun setupPeriodFilter() {
        spinnerPeriod.setOnItemClickListener { _, _, position, _ ->
            currentPeriod = when (position) {
                0 -> Periodo.TODOS
                1 -> Periodo.ESTE_MES
                2 -> Periodo.MES_PASSADO
                3 -> Periodo.ESTE_ANO
                else -> Periodo.TODOS
            }
            loadDashboardData(currentPeriod)
        }
    }

    private fun setupStoreFilter() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            // Busca todas as ocorrências para extrair as lojas únicas
            val allOcorrencias = db.ocorrenciaDao().getAll(currentTenantId)
            val stores = allOcorrencias.map { it.loja }.distinct().sorted()
            
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@DashboardActivity, android.R.layout.simple_dropdown_item_1line, listOf("Todas") + stores)
                spinnerStore.setAdapter(adapter)
                spinnerStore.setOnItemClickListener { _, _, _, _ ->
                    selectedStore = spinnerStore.text.toString()
                    loadDashboardData(currentPeriod)
                }
            }
        }
    }

    private fun loadDashboardData(periodo: Periodo) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val (start, end) = getDatesForPeriod(periodo)

            val ocorrencias = if (start != null && end != null) {
                db.ocorrenciaDao().getByDateRange(currentTenantId, start, end)
            } else {
                db.ocorrenciaDao().getAll(currentTenantId)
            }

            // Aplica o filtro de loja em memória
            val filteredOcorrencias = if (selectedStore != "Todas") {
                ocorrencias.filter { it.loja == selectedStore }
            } else {
                ocorrencias
            }

            withContext(Dispatchers.Main) {
                currentFilteredList = filteredOcorrencias
                setupPieChart(filteredOcorrencias)
                setupFinancialChart(filteredOcorrencias)
                setupLineChart(filteredOcorrencias)
                setupScatterChart(filteredOcorrencias)
                setupBubbleChart(filteredOcorrencias)
            }
        }
    }

    private fun getDatesForPeriod(periodo: Periodo): Pair<Long?, Long?> {
        val calendar = Calendar.getInstance()
        // Zera horas para comparação precisa
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return when (periodo) {
            Periodo.TODOS -> Pair(null, null)
            Periodo.ESTE_MES -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
            Periodo.MES_PASSADO -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
            Periodo.ESTE_ANO -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val start = calendar.timeInMillis
                calendar.add(Calendar.YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.timeInMillis
                Pair(start, end)
            }
        }
    }

    private fun setupPieChart(ocorrencias: List<Ocorrencia>) {
        // Agora usando o campo correto 'perfilFurtante'
        val map = ocorrencias
            .filter { it.perfilFurtante.isNotEmpty() } // Ignora vazios
            .groupingBy { it.perfilFurtante }.eachCount()

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        for ((key, count) in map) {
            if (count > 0) {
                entries.add(PieEntry(count.toFloat(), key))
                colors.add(ColorTemplate.MATERIAL_COLORS[colors.size % ColorTemplate.MATERIAL_COLORS.size])
            }
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueFormatter = PercentFormatter(pieChart)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.animateY(1400)
        pieChart.invalidate()
    }

    private fun setupFinancialChart(ocorrencias: List<Ocorrencia>) {
        // Agrupa por Loja e soma Valor
        val dadosAgrupados = ocorrencias
            .groupBy { it.loja }
            .mapValues { entry -> entry.value.sumOf { it.valorEstimado } }
            .toList()
            .sortedByDescending { it.second }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        dadosAgrupados.forEachIndexed { index, (loja, valorTotal) ->
            entries.add(BarEntry(index.toFloat(), valorTotal.toFloat()))
            labels.add(loja)
        }

        val dataSet = BarDataSet(entries, "Prejuízo Total")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        
        // Formata valor como Moeda (R$)
        val currencyFormatter = object : ValueFormatter() {
            private val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            override fun getFormattedValue(value: Float): String = format.format(value.toDouble())
        }
        dataSet.valueFormatter = currencyFormatter

        val data = BarData(dataSet)
        barChart.data = data
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisLeft.valueFormatter = currencyFormatter
        barChart.animateY(1500)
        barChart.invalidate()
    }

    private fun setupLineChart(ocorrencias: List<Ocorrencia>) {
        // Agrupa por Data (Dia) e soma o Valor
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        
        val dadosPorData = ocorrencias
            .sortedBy { it.dataRegistro }
            .groupBy { dateFormat.format(Date(it.dataRegistro)) }
            .mapValues { entry -> entry.value.sumOf { it.valorEstimado } }

        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        dadosPorData.entries.forEachIndexed { index, entry ->
            entries.add(Entry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)
        }

        val dataSet = LineDataSet(entries, "Evolução do Prejuízo (R$)")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setCircleColor(Color.BLUE)
        dataSet.setDrawValues(false) // Oculta valores em cada ponto para não poluir
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Linha curva suave
        dataSet.setDrawFilled(true) // Preenchimento abaixo da linha
        dataSet.fillColor = Color.CYAN
        dataSet.fillAlpha = 50

        val data = LineData(dataSet)
        lineChart.data = data
        
        // Configura Eixo X com as Datas
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        
        // Configura o Marcador (Tooltip)
        val marker = CustomMarkerView(this, R.layout.custom_marker_view)
        marker.chartView = lineChart
        lineChart.marker = marker

        lineChart.description.isEnabled = false
        lineChart.animateX(1500)
        lineChart.invalidate()
    }

    private fun setupScatterChart(ocorrencias: List<Ocorrencia>) {
        val entries = ArrayList<Entry>()
        val cal = Calendar.getInstance()

        ocorrencias.forEach {
            cal.timeInMillis = it.dataRegistro
            // Converte hora e minuto para decimal (ex: 14:30 -> 14.5)
            val hour = cal.get(Calendar.HOUR_OF_DAY) + (cal.get(Calendar.MINUTE) / 60f)
            entries.add(Entry(hour, it.valorEstimado.toFloat()))
        }

        val dataSet = ScatterDataSet(entries, "Horário x Valor")
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        dataSet.color = Color.MAGENTA
        dataSet.scatterShapeSize = 15f
        dataSet.setDrawValues(false)

        val data = ScatterData(dataSet)
        scatterChart.data = data

        // Configura Eixo X (Horas)
        scatterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        scatterChart.xAxis.axisMinimum = 0f
        scatterChart.xAxis.axisMaximum = 24f
        scatterChart.xAxis.granularity = 1f

        scatterChart.description.isEnabled = false
        scatterChart.animateXY(1500, 1500)
        scatterChart.invalidate()
    }

    private fun setupBubbleChart(ocorrencias: List<Ocorrencia>) {
        val entries = ArrayList<BubbleEntry>()
        val cal = Calendar.getInstance()

        // Agrupa por (Dia da Semana, Hora) -> Contagem
        // Dia da Semana: 1 (Dom) a 7 (Sab) -> Mapeamos para 0 a 6
        val groupedData = ocorrencias.groupingBy {
            cal.timeInMillis = it.dataRegistro
            val day = cal.get(Calendar.DAY_OF_WEEK) - 1 
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            Pair(day, hour)
        }.eachCount()

        groupedData.forEach { (key, count) ->
            // BubbleEntry(x, y, size)
            // Size representa a intensidade (número de ocorrências naquele horário)
            entries.add(BubbleEntry(key.first.toFloat(), key.second.toFloat(), count.toFloat()))
        }

        val dataSet = BubbleDataSet(entries, "Intensidade (Dia x Hora)")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = Color.WHITE
        dataSet.highlightCircleWidth = 1.5f

        val data = BubbleData(dataSet)
        bubbleChart.data = data

        // Configura Eixo X (Dias da Semana)
        val daysOfWeek = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
        bubbleChart.xAxis.valueFormatter = IndexAxisValueFormatter(daysOfWeek)
        bubbleChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        bubbleChart.xAxis.granularity = 1f
        bubbleChart.xAxis.axisMinimum = -0.5f
        bubbleChart.xAxis.axisMaximum = 6.5f

        bubbleChart.description.isEnabled = false
        bubbleChart.animateXY(1500, 1500)
        bubbleChart.invalidate()
    }

    private fun exportFilteredDataToPdf() {
        if (currentFilteredList.isEmpty()) {
            Toast.makeText(this, "Sem dados para exportar.", Toast.LENGTH_SHORT).show()
            return
        }

        // Configuração ABNT
        // Margens: 3cm (Esq/Sup) e 2cm (Dir/Inf)
        // 1 cm ~= 28.35 points
        val marginLeft = 85f // ~3cm
        val marginTop = 85f // ~3cm
        val marginRight = 57f // ~2cm
        val marginBottom = 57f // ~2cm
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points
        
        val pdfDocument = PdfDocument()
        val paint = Paint()
        
        // Fonte: Times New Roman (Serif no Android)
        val fontTitle = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        val fontBody = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = marginTop

        // Título
        paint.textSize = 12f // ABNT Tamanho 12
        paint.typeface = fontTitle
        paint.color = Color.BLACK
        
        // Centralizar Título
        val title = "RELATÓRIO DE OCORRÊNCIAS"
        val titleWidth = paint.measureText(title)
        canvas.drawText(title, (pageWidth - titleWidth) / 2, y, paint)
        y += 30f

        // Subtítulo com filtros
        paint.textSize = 12f
        paint.typeface = fontBody
        canvas.drawText("Filtro: Loja: $selectedStore | Período: $currentPeriod", marginLeft, y, paint)
        y += 30f

        // Cabeçalho da Tabela
        paint.typeface = fontTitle
        canvas.drawText("Data", marginLeft, y, paint)
        canvas.drawText("Loja", marginLeft + 100, y, paint)
        canvas.drawText("Categoria", marginLeft + 220, y, paint)
        canvas.drawText("Valor (R$)", marginLeft + 380, y, paint)
        y += 20f

        // Dados
        paint.typeface = fontBody
        val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        for (item in currentFilteredList) {
            // Paginação Automática
            if (y > pageHeight - marginBottom) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = marginTop
                // Repete cabeçalho na nova página (opcional, mas recomendado)
            }

            canvas.drawText(dateFormat.format(Date(item.dataRegistro)), marginLeft, y, paint)
            canvas.drawText(item.loja.take(15), marginLeft + 100, y, paint)
            canvas.drawText(item.categoriaProduto.take(15), marginLeft + 220, y, paint)
            canvas.drawText(currencyFormat.format(item.valorEstimado), marginLeft + 380, y, paint)
            y += 20f // Espaçamento entre linhas
        }

        pdfDocument.finishPage(page)

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Dashboard_Report_${System.currentTimeMillis()}.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF salvo com sucesso!", Toast.LENGTH_SHORT).show()
            sharePdf(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao salvar PDF.", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
    }
}