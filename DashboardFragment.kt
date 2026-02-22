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
import android.widget.Toast
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
import com.example.gestaoderisco.utils.DashboardChartManager
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.util.Pair

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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
        setupCategorySpinner()
        setupConfigEmailButton()
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
        val config = ShowcaseConfig()
        config.delay = 500

        val sequence = MaterialShowcaseSequence(requireActivity(), "dashboard_tutorial_sequence")
        sequence.setConfig(config)

        sequence.addSequenceItem(binding.tvDashboardTitle, getString(R.string.tutorial_welcome_title), getString(R.string.tutorial_welcome_desc), getString(R.string.tutorial_got_it))
        sequence.addSequenceItem(binding.btnConfigEmail, getString(R.string.tutorial_config_title), getString(R.string.tutorial_config_desc), getString(R.string.tutorial_got_it))
        sequence.addSequenceItem(binding.btnExportCsv, getString(R.string.tutorial_export_title), getString(R.string.tutorial_export_desc), getString(R.string.tutorial_got_it))
        sequence.addSequenceItem(binding.spnPeriodFilter, getString(R.string.tutorial_filter_title), getString(R.string.tutorial_filter_desc), getString(R.string.tutorial_got_it))

        sequence.start()
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.product_categories, android.R.layout.simple_spinner_item)
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
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
        }
    }

    private fun setupExportButton() {
        binding.btnExportCsv.setOnClickListener {
            authenticateUser { exportDashboardData() }
        }
    }

    private fun setupEconomicExportButton() {
        binding.btnExportEconomic.setOnClickListener {
            authenticateUser { exportEconomicAnalysisData() }
        }
    }

    private fun exportDashboardData() {
        val selectedPeriod = binding.spnPeriodFilter.selectedItem?.toString() ?: "Todos"
        val selectedCategoryPosition = binding.spnCategoryFilter.selectedItemPosition
        val selectedCategory = if (selectedCategoryPosition > 0) binding.spnCategoryFilter.selectedItem.toString() else null
        
        lifecycleScope.launch {
            val filteredList = withContext(Dispatchers.Default) { filterOcorrencias(selectedPeriod, selectedCategory) }
            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "Não há dados para exportar.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            try {
                val csvFile = withContext(Dispatchers.IO) { generateCsvFile(filteredList) }
                shareCsvFile(csvFile)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), getString(R.string.export_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportEconomicAnalysisData() {
        val selectedPeriod = binding.spnPeriodFilter.selectedItem?.toString() ?: "Todos"
        val selectedCategoryPosition = binding.spnCategoryFilter.selectedItemPosition
        val selectedCategory = if (selectedCategoryPosition > 0) binding.spnCategoryFilter.selectedItem.toString() else null
        
        lifecycleScope.launch {
            val filteredList = withContext(Dispatchers.Default) { filterOcorrencias(selectedPeriod, selectedCategory) }
            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "Não há dados para exportar.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            try {
                val csvFile = withContext(Dispatchers.IO) { generateEconomicCsvFile(filteredList) }
                shareCsvFile(csvFile)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), getString(R.string.export_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateEconomicCsvFile(data: List<Ocorrencia>): File {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val currencyFormat = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        // Branding
        sb.append(getString(R.string.report_institution_name)).append("\n")
        sb.append("RELATÓRIO DE ANÁLISE ECONÔMICA").append("\n")
        sb.append("${getString(R.string.report_generated_at)} ${dateFormat.format(Date())}\n\n")
        
        sb.append("RESUMO POR LOJA (RANKING DE PREJUÍZO)\n")
        sb.append("Loja,Valor Total\n")

        val analyzer = StatisticsAnalyzer(data)
        val storeStats = analyzer.getEconomicAnalysisByStore()
        storeStats.forEach { (store, value) -> sb.append("${escapeCsv(store)},\"${currencyFormat.format(value)}\"\n") }
        sb.append("\n")
        sb.append("DETALHAMENTO DAS OCORRÊNCIAS (MAIORES PREJUÍZOS)\n")
        sb.append("Loja,Data,Valor,Categoria,Ação,Status,Relato\n")

        val sortedData = data.sortedByDescending { it.valorEstimado }
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in sortedData) {
            val dateStr = dateTimeFormat.format(Date(item.dataRegistro)) // Usando dataRegistro como exemplo
            sb.append("${escapeCsv(item.loja)},$dateStr,${item.valorEstimado},${escapeCsv(item.categoriaProduto)},${escapeCsv(item.acaoRealizada)},Resolvido,${escapeCsv(item.relato)}\n")
        }

        // Rodapé
        sb.append("\n")
        sb.append(escapeCsv(getString(R.string.report_confidentiality))).append("\n")
        sb.append(escapeCsv(getString(R.string.report_security_warning))).append("\n")

        val cachePath = File(requireContext().cacheDir, "exports")
        cachePath.mkdirs()
        val file = File(cachePath, "analise_economica.csv")
        val stream = FileOutputStream(file)
        stream.write(sb.toString().toByteArray())
        stream.close()
        return file
    }

    private fun generateCsvFile(data: List<Ocorrencia>): File {
        val csvHeader = "Loja,Data,Valor,Categoria,Ação,Status,Fundada Suspeita,Relato\n"
        val sb = StringBuilder()
        sb.append(csvHeader)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in data) {
            val dateStr = dateFormat.format(Date(item.dataRegistro))
            sb.append("${escapeCsv(item.loja)},$dateStr,${item.valorEstimado},${escapeCsv(item.categoriaProduto)},${escapeCsv(item.acaoRealizada)},Resolvido,,${escapeCsv(item.relato)}\n")
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
        val contentUri: Uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, "text/csv")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Relatório de Prevenção de Perdas")
            if (!email.isNullOrBlank()) putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }
        startActivity(Intent.createChooser(shareIntent, "Exportar CSV via"))
    }

    private fun setupShareButton() {
        binding.btnShareDashboard.setOnClickListener { shareDashboardScreenshot() }
    }

    private fun shareDashboardScreenshot() {
        val content = binding.dashboardContent
        val bitmap = getScreenShot(content)
        shareBitmap(bitmap)
    }

    private fun getScreenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.background?.draw(canvas) ?: canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun shareBitmap(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cachePath = File(requireContext().cacheDir, "images")
                cachePath.mkdirs()
                val stream = FileOutputStream("$cachePath/dashboard_screenshot.png")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val imagePath = File(requireContext().cacheDir, "images")
                val newFile = File(imagePath, "dashboard_screenshot.png")
                val contentUri: Uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", newFile)

                withContext(Dispatchers.Main) {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(contentUri, requireContext().contentResolver.getType(contentUri))
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        type = "image/png"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Compartilhar Dashboard via"))
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "Erro ao gerar imagem", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun setupPeriodSpinner() {
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.dashboard_periods, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnPeriodFilter.adapter = adapter

        binding.spnPeriodFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "Personalizado") showDatePicker() else {
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
        datePicker.addOnNegativeButtonClickListener { binding.spnPeriodFilter.setSelection(lastSelectedPosition) }
        datePicker.addOnCancelListener { binding.spnPeriodFilter.setSelection(lastSelectedPosition) }
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
        val selectedCategory = if (selectedCategoryPosition > 0) binding.spnCategoryFilter.selectedItem.toString() else null
        val filteredList = filterOcorrencias(selectedPeriod, selectedCategory)

        val totalValue = filteredList.sumOf { it.valorEstimado }
        binding.tvTotalValue.text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(totalValue)

        val analyzer = StatisticsAnalyzer(filteredList)
        setupPieChart(analyzer.getStatisticsByArea())
        setupBarChart(analyzer.getStatisticsByStore())
        setupFinancialBarChart(analyzer.getEconomicAnalysisByStore())
        setupLineChart(analyzer.getDailyFinancialEvolution())
        setupScatterChart(analyzer.getScatterData())

        val responseTimeData = filteredList.map {
            DashboardChartManager.ChartDataInput(it.loja, Date(it.dataRegistro), Date(it.dataRegistro))
        }
        DashboardChartManager.setupResponseTimeChart(requireContext(), binding.barChartResponseTime, responseTimeData)

        updateRiskPrediction(analyzer, selectedCategoryPosition)
    }

    private fun filterOcorrencias(period: String, category: String?): List<Ocorrencia> {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        val periodFilteredList = when (period) {
            "Este Mês" -> fullOcorrenciasList.filter {
                cal.time = Date(it.dataRegistro)
                cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
            }
            "Mês Passado" -> {
                val targetCal = Calendar.getInstance()
                targetCal.time = now
                targetCal.add(Calendar.MONTH, -1)
                val targetMonth = targetCal.get(Calendar.MONTH)
                val targetYear = targetCal.get(Calendar.YEAR)
                fullOcorrenciasList.filter {
                    cal.time = Date(it.dataRegistro)
                    cal.get(Calendar.MONTH) == targetMonth && cal.get(Calendar.YEAR) == targetYear
                }
            }
            "Este Ano" -> fullOcorrenciasList.filter {
                cal.time = Date(it.dataRegistro)
                cal.get(Calendar.YEAR) == currentYear
            }
            "Personalizado" -> {
                if (customStartDate != null && customEndDate != null) {
                    val endCal = Calendar.getInstance()
                    endCal.time = customEndDate!!
                    endCal.set(Calendar.HOUR_OF_DAY, 23)
                    endCal.set(Calendar.MINUTE, 59)
                    endCal.set(Calendar.SECOND, 59)
                    val start = customStartDate!!.time
                    val end = endCal.timeInMillis
                    fullOcorrenciasList.filter { it.dataRegistro in start..end }
                } else fullOcorrenciasList
            }
            else -> fullOcorrenciasList
        }

        return if (category != null) periodFilteredList.filter { it.categoriaProduto == category } else periodFilteredList
    }

    private fun updateRiskPrediction(analyzer: StatisticsAnalyzer, categoryIndex: Int) {
        // Implementação simplificada para compilação
    }
    private fun setupPieChart(dataMap: Map<String, Int>) {}
    private fun setupBarChart(dataMap: Map<String, Int>) {}
    private fun setupFinancialBarChart(dataMap: Map<String, Double>) {}
    private fun setupLineChart(dataMap: Map<Long, Double>) {}
    private fun setupScatterChart(dataList: List<Pair<Float, Double>>) {}

    override fun onDestroyView() {
        super.onDestroyView()
        riskPredictor.close()
        _binding = null
    }
}