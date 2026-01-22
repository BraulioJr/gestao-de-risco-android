package com.example.gestaoderisco.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gestaoderisco.R
import com.example.gestaoderisco.analysis.StatisticsAnalyzer
import com.example.gestaoderisco.ml.RiskPredictor
import com.example.gestaoderisco.databinding.FragmentDashboardBinding
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.viewmodel.OcorrenciasUiState
import com.example.gestaoderisco.viewmodel.OcorrenciasViewModel
import com.example.gestaoderisco.viewmodel.OcorrenciasViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Utiliza o mesmo ViewModel que já busca as ocorrências
    private val viewModel: OcorrenciasViewModel by viewModels { OcorrenciasViewModelFactory() }

    private var fullOcorrenciasList: List<Ocorrencia> = emptyList()
    private lateinit var riskPredictor: RiskPredictor
    
    private var customStartDate: Date? = null
    private var customEndDate: Date? = null
    private var lastSelectedPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        riskPredictor = RiskPredictor(requireContext())
        setupPeriodSpinner()
        setupStatusSpinner()
        setupCategorySpinner()
        setupConfigEmailButton()
        setupMapViewButton()
        setupExportButton()
        setupEconomicExportButton()
        setupShareButton()
        observeData()
        startTutorial()
    }

    private fun authenticateUser(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), "Autenticação falhou: $errString", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_negative_button))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun startTutorial() {
        // Configuração do Showcase
        val config = ShowcaseConfig()
        config.delay = 500 // Meio segundo de atraso entre cada item

        val sequence = MaterialShowcaseSequence(requireActivity(), "dashboard_tutorial_sequence")
        sequence.setConfig(config)

        // Passo 1: Boas-vindas (Foco no Título)
        sequence.addSequenceItem(
            binding.tvDashboardTitle,
            getString(R.string.tutorial_welcome_title),
            getString(R.string.tutorial_welcome_desc),
            getString(R.string.tutorial_got_it)
        )

        // Passo 2: Configurações
        sequence.addSequenceItem(
            binding.btnConfigEmail,
            getString(R.string.tutorial_config_title),
            getString(R.string.tutorial_config_desc),
            getString(R.string.tutorial_got_it)
        )

        // Passo 3: Exportação
        sequence.addSequenceItem(
            binding.btnExportCsv,
            getString(R.string.tutorial_export_title),
            getString(R.string.tutorial_export_desc),
            getString(R.string.tutorial_got_it)
        )

        // Passo 4: Filtros
        sequence.addSequenceItem(
            binding.spnPeriodFilter,
            getString(R.string.tutorial_filter_title),
            getString(R.string.tutorial_filter_desc),
            getString(R.string.tutorial_got_it)
        )

        sequence.start()
    }

    private fun setupStatusSpinner() {
        // Adiciona "Todos" ao array de status
        val statusOptions = listOf("Todos Status") + resources.getStringArray(R.array.occurrence_status)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnStatusFilter.adapter = adapter

        binding.spnStatusFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCharts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_categories,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnCategoryFilter.adapter = adapter

        binding.spnCategoryFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCharts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupConfigEmailButton() {
        binding.btnConfigEmail.setOnClickListener {
            authenticateUser {
                // Abre a tela de configurações (Notificações e Relatórios)
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
        }
    }

    private fun setupMapViewButton() {
        binding.btnViewMap.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }
    }

    private fun setupExportButton() {
        binding.btnExportCsv.setOnClickListener {
            authenticateUser {
                exportDashboardData()
            }
        }
    }

    private fun setupEconomicExportButton() {
        binding.btnExportEconomic.setOnClickListener {
            authenticateUser {
                exportEconomicAnalysisData()
            }
        }
    }

    private fun exportDashboardData() {
        val selectedPeriod = binding.spnPeriodFilter.selectedItem?.toString() ?: "Todos"
        val selectedCategoryPosition = binding.spnCategoryFilter.selectedItemPosition
        val selectedStatusPosition = binding.spnStatusFilter.selectedItemPosition
        val selectedCategory = if (selectedCategoryPosition > 0) binding.spnCategoryFilter.selectedItem.toString() else null
        val selectedStatus = if (selectedStatusPosition > 0) binding.spnStatusFilter.selectedItem.toString() else null
        val filteredList = filterOcorrencias(selectedPeriod, selectedCategory, selectedStatus)

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Não há dados para exportar.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val csvFile = generateCsvFile(filteredList)
            shareCsvFile(csvFile)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), getString(R.string.export_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportEconomicAnalysisData() {
        val selectedPeriod = binding.spnPeriodFilter.selectedItem?.toString() ?: "Todos"
        val selectedCategoryPosition = binding.spnCategoryFilter.selectedItemPosition
        val selectedStatusPosition = binding.spnStatusFilter.selectedItemPosition
        val selectedCategory = if (selectedCategoryPosition > 0) binding.spnCategoryFilter.selectedItem.toString() else null
        val selectedStatus = if (selectedStatusPosition > 0) binding.spnStatusFilter.selectedItem.toString() else null
        val filteredList = filterOcorrencias(selectedPeriod, selectedCategory, selectedStatus)

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Não há dados para exportar.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val csvFile = generateEconomicCsvFile(filteredList)
            shareCsvFile(csvFile)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), getString(R.string.export_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateEconomicCsvFile(data: List<Ocorrencia>): File {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val currencyFormat = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        // Cabeçalho do Relatório
        sb.append("RELATÓRIO DE ANÁLISE ECONÔMICA\n")
        sb.append("Gerado em: ${dateFormat.format(Date())}\n\n")

        // Seção 1: Resumo por Loja (Ranking)
        sb.append("RESUMO POR LOJA (RANKING DE PREJUÍZO)\n")
        sb.append("Loja,Valor Total\n")

        val analyzer = StatisticsAnalyzer(data)
        val storeStats = analyzer.getEconomicAnalysisByStore()

        storeStats.forEach { (store, value) ->
            sb.append("${escapeCsv(store)},\"${currencyFormat.format(value)}\"\n")
        }
        sb.append("\n")

        // Seção 2: Detalhamento (Ordenado por Valor)
        sb.append("DETALHAMENTO DAS OCORRÊNCIAS (MAIORES PREJUÍZOS)\n")
        sb.append("Loja,Data,Valor,Categoria,Ação,Relato\n")

        val sortedData = data.sortedByDescending { it.valor }
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in sortedData) {
            val dateStr = dateTimeFormat.format(item.data)
            val loja = escapeCsv(item.loja)
            val produtos = escapeCsv(item.produtos)
            val acao = escapeCsv(item.acao)
            val relato = escapeCsv(item.relato)
            sb.append("$loja,$dateStr,${item.valor},$produtos,$acao,$relato\n")
        }

        val cachePath = File(requireContext().cacheDir, "exports")
        cachePath.mkdirs()
        val file = File(cachePath, "analise_economica.csv")
        val stream = FileOutputStream(file)
        stream.write(sb.toString().toByteArray())
        stream.close()
        return file
    }

    private fun generateCsvFile(data: List<Ocorrencia>): File {
        val csvHeader = "Loja,Data,Valor,Categoria,Ação,Fundada Suspeita,Relato\n"
        val sb = StringBuilder()
        sb.append(csvHeader)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in data) {
            val dateStr = dateFormat.format(item.data)
            // Escapa aspas duplas e envolve campos com vírgula em aspas
            val loja = escapeCsv(item.loja)
            val produtos = escapeCsv(item.produtos)
            val acao = escapeCsv(item.acao)
            val suspeita = escapeCsv(item.fundadaSuspeita)
            val relato = escapeCsv(item.relato)
            
            sb.append("$loja,$dateStr,${item.valor},$produtos,$acao,$suspeita,$relato\n")
        }

        val cachePath = File(requireContext().cacheDir, "exports")
        cachePath.mkdirs()
        val file = File(cachePath, "relatorio_prevencao_perdas.csv")
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

    private fun shareCsvFile(file: File) {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("pp_coordinator_email", null)

        val contentUri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.gestaoderisco.fileprovider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, "text/csv")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Relatório de Prevenção de Perdas")
            if (!email.isNullOrBlank()) {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
        }
        startActivity(Intent.createChooser(shareIntent, "Exportar CSV via"))
    }

    private fun setupShareButton() {
        binding.btnShareDashboard.setOnClickListener {
            shareDashboardScreenshot()
        }
    }

    private fun shareDashboardScreenshot() {
        val content = binding.dashboardContent
        val bitmap = getScreenShot(content)
        shareBitmap(bitmap)
    }

    private fun getScreenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Desenha o fundo (branco ou cor do tema) antes do conteúdo, senão fica preto/transparente
        view.background?.draw(canvas) ?: canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun shareBitmap(bitmap: Bitmap) {
        try {
            val cachePath = File(requireContext().cacheDir, "images")
            cachePath.mkdirs() // Cria o diretório se não existir
            val stream = FileOutputStream("$cachePath/dashboard_screenshot.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val imagePath = File(requireContext().cacheDir, "images")
            val newFile = File(imagePath, "dashboard_screenshot.png")
            
            // Substitua "com.example.gestaoderisco.fileprovider" pela autoridade definida no AndroidManifest
            val contentUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.gestaoderisco.fileprovider",
                newFile
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, requireContext().contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar Dashboard via"))

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Erro ao gerar imagem para compartilhamento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPeriodSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dashboard_periods,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnPeriodFilter.adapter = adapter

        binding.spnPeriodFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "Personalizado") {
                    showDatePicker()
                } else {
                    lastSelectedPosition = position
                    updateCharts()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Selecione o período")
            .setSelection(Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            customStartDate = Date(selection.first)
            customEndDate = Date(selection.second)
            updateCharts()
        }

        datePicker.addOnNegativeButtonClickListener {
            binding.spnPeriodFilter.setSelection(lastSelectedPosition)
        }

        datePicker.addOnCancelListener {
            binding.spnPeriodFilter.setSelection(lastSelectedPosition)
        }

        datePicker.show(parentFragmentManager, "date_picker")
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state is OcorrenciasUiState.Success) {
                        fullOcorrenciasList = state.ocorrencias
                        updateCharts()
                    }
                }
            }
        }
    }

    private fun updateCharts() {
        val selectedPeriod = binding.spnPeriodFilter.selectedItem?.toString() ?: "Todos"
        val selectedCategoryPosition = binding.spnCategoryFilter.selectedItemPosition
        val selectedStatusPosition = binding.spnStatusFilter.selectedItemPosition
        val selectedCategory = if (selectedCategoryPosition > 0) binding.spnCategoryFilter.selectedItem.toString() else null
        val selectedStatus = if (selectedStatusPosition > 0) binding.spnStatusFilter.selectedItem.toString() else null
        val filteredList = filterOcorrencias(selectedPeriod, selectedCategory, selectedStatus)

        // Atualiza o Valor Total Recuperado
        val totalValue = filteredList.sumOf { it.valor }
        binding.tvTotalValue.text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(totalValue)

        val analyzer = StatisticsAnalyzer(filteredList)
        setupPieChart(analyzer.getStatisticsByArea())
        setupBarChart(analyzer.getStatisticsByStore())
        setupFinancialBarChart(analyzer.getEconomicAnalysisByStore())
        setupLineChart(analyzer.getDailyFinancialEvolution())
        setupScatterChart(analyzer.getScatterData())
        updateRiskPrediction(analyzer, selectedCategoryPosition)
    }

    private fun filterOcorrencias(period: String, category: String?, status: String?): List<Ocorrencia> {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        val periodFilteredList = when (period) {
            "Este Mês" -> fullOcorrenciasList.filter {
                cal.time = it.data
                cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
            }
            "Mês Passado" -> {
                val targetCal = Calendar.getInstance()
                targetCal.time = now
                targetCal.add(Calendar.MONTH, -1)
                val targetMonth = targetCal.get(Calendar.MONTH)
                val targetYear = targetCal.get(Calendar.YEAR)
                fullOcorrenciasList.filter {
                    cal.time = it.data
                    cal.get(Calendar.MONTH) == targetMonth && cal.get(Calendar.YEAR) == targetYear
                }
            }
            "Este Ano" -> fullOcorrenciasList.filter {
                cal.time = it.data
                cal.get(Calendar.YEAR) == currentYear
            }
            "Personalizado" -> {
                if (customStartDate != null && customEndDate != null) {
                    // Ajusta a data final para o final do dia (23:59:59)
                    val endCal = Calendar.getInstance()
                    endCal.time = customEndDate
                    endCal.set(Calendar.HOUR_OF_DAY, 23)
                    endCal.set(Calendar.MINUTE, 59)
                    endCal.set(Calendar.SECOND, 59)
                    
                    val start = customStartDate!!.time
                    val end = endCal.timeInMillis
                    
                    fullOcorrenciasList.filter { it.data.time in start..end }
                } else {
                    fullOcorrenciasList
                }
            }
            else -> fullOcorrenciasList
        }

        val categoryFilteredList = if (category != null) {
            periodFilteredList.filter { it.produtos == category }
        } else {
            periodFilteredList
        }

        return if (status != null) {
            categoryFilteredList.filter { it.status == status }
        } else {
            categoryFilteredList
        }
    }

    private fun updateRiskPrediction(analyzer: StatisticsAnalyzer, categoryIndex: Int) {
        val categories = resources.getStringArray(R.array.product_categories)
        
        // Tenta usar o modelo TFLite
        var probability = riskPredictor.predict(categoryIndex, categories.size)

        if (probability == -1f) {
            // Fallback: Cálculo Estatístico Simples se o modelo não existir
            // Risco = (Ocorrências na hora atual / Média de ocorrências por hora) normalizado
            val cal = Calendar.getInstance()
            val currentHour = cal.get(Calendar.HOUR_OF_DAY)
            
            // Pega dados da "Mancha Criminal"
            val spotData = analyzer.getCriminalSpotData(categories)
            val currentHourCount = spotData.filter { it.first == currentHour }.sumOf { it.third }
            val totalCount = spotData.sumOf { it.third }
            
            probability = if (totalCount > 0) {
                // Normalização simples para exemplo: se a hora atual tem 10% das ocorrências, risco é 0.1
                // Multiplicamos por um fator para escala visual (ex: 24h, média seria 1/24 = 0.04)
                (currentHourCount.toFloat() / totalCount.toFloat()) * 5f 
            } else {
                0f
            }
            // Cap em 1.0
            if (probability > 1f) probability = 1f
            
            binding.tvRiskDesc.text = getString(R.string.risk_prediction_fallback)
        } else {
            binding.tvRiskDesc.text = getString(R.string.risk_prediction_desc)
        }

        val percentage = (probability * 100).toInt()
        binding.tvRiskProbability.text = "$percentage%"
        binding.tvRiskProbability.setTextColor(if (percentage > 50) Color.RED else Color.parseColor("#4CAF50"))
    }

    private fun setupPieChart(dataMap: Map<String, Int>) {
        val entries = dataMap.map { (category, count) ->
            PieEntry(count.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Categorias").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        val pieData = PieData(dataSet)

        binding.pieChartCategories.apply {
            data = pieData
            description.isEnabled = false
            centerText = "Categorias"
            setEntryLabelColor(Color.BLACK)
            animateY(1000)
            invalidate() // Refresh
        }
    }

    private fun setupBarChart(dataMap: Map<String, Int>) {
        // Prepara as entradas e os rótulos (nomes das lojas)
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        dataMap.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key) // Nome da loja
        }

        val dataSet = BarDataSet(entries, "Ocorrências").apply {
            colors = ColorTemplate.JOYFUL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)

        binding.barChartStores.apply {
            data = barData
            description.isEnabled = false
            
            // Configuração do Eixo X (Lojas)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                labelRotationAngle = -45f // Rotaciona se houver muitas lojas
            }

            // Configuração do Eixo Y
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false // Desabilita eixo direito

            animateY(1000)
            invalidate()
        }
    }

    private fun setupFinancialBarChart(dataMap: Map<String, Double>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        dataMap.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)
        }

        val dataSet = BarDataSet(entries, "Prejuízo (R$)").apply {
            colors = listOf(Color.RED) // Vermelho para indicar prejuízo financeiro
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(value)
                }
            }
        }

        val barData = BarData(dataSet)

        binding.barChartFinancial.apply {
            data = barData
            description.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                labelRotationAngle = -45f
            }

            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            animateY(1000)
            invalidate()
        }
    }

    private fun setupLineChart(dataMap: Map<Long, Double>) {
        val entries = ArrayList<Entry>()
        val sortedKeys = dataMap.keys.toList()

        sortedKeys.forEachIndexed { index, timestamp ->
            entries.add(Entry(index.toFloat(), dataMap[timestamp]!!.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Evolução Diária").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            valueTextSize = 10f
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.BLUE)
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(value.toDouble())
                }
            }
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.BLUE
            fillAlpha = 30
        }

        val lineData = LineData(dataSet)

        binding.lineChartEvolution.apply {
            data = lineData
            description.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        if (index >= 0 && index < sortedKeys.size) {
                            return dateFormat.format(Date(sortedKeys[index]))
                        }
                        return ""
                    }
                }
                granularity = 1f
                isGranularityEnabled = true
                labelRotationAngle = -45f
            }

            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(value.toDouble())
                }
            }

            axisRight.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }

    private fun setupScatterChart(dataList: List<Pair<Float, Double>>) {
        val entries = ArrayList<Entry>()
        dataList.forEach { (time, value) ->
            entries.add(Entry(time, value.toFloat()))
        }

        val dataSet = ScatterDataSet(entries, "Ocorrências").apply {
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            color = ColorTemplate.COLORFUL_COLORS[0]
            valueTextColor = Color.BLACK
            valueTextSize = 10f
            scatterShapeSize = 12f
        }

        val scatterData = ScatterData(dataSet)

        binding.scatterChartRisk.apply {
            data = scatterData
            description.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                axisMinimum = 0f
                axisMaximum = 24f
                granularity = 1f
            }

            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(value.toDouble())
                    }
                }
            }
            axisRight.isEnabled = false

            animateXY(1000, 1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        riskPredictor.close()
        _binding = null
    }
}
