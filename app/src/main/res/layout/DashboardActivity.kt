package com.example.project_gestoderisco.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ActivityDashboardBinding
import com.example.project_gestoderisco.viewmodel.DashboardStats
import com.example.project_gestoderisco.viewmodel.DashboardUiState
import com.example.project_gestoderisco.viewmodel.DashboardViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state is DashboardUiState.Loading) View.VISIBLE else View.GONE
                    binding.contentLayout.visibility = if (state is DashboardUiState.Success) View.VISIBLE else View.INVISIBLE

                    when (state) {
                        is DashboardUiState.Success -> {
                            val stats = state.stats
                            binding.textViewTotalRisks.text = stats.totalRisks.toString()
                            binding.textViewHighImpactCount.text = "Alto: ${stats.highImpactCount}"
                            binding.textViewMediumImpactCount.text = "Médio: ${stats.mediumImpactCount}"
                            binding.textViewLowImpactCount.text = "Baixo: ${stats.lowImpactCount}"
                            setupPieChart(stats)
                        }
                        is DashboardUiState.Error -> {
                            Toast.makeText(this@DashboardActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                        is DashboardUiState.Loading -> {
                            // O ProgressBar já é tratado fora do when
                        }
                    }
                }
            }
        }
    }

    private fun setupPieChart(stats: DashboardStats) {
        val entries = ArrayList<PieEntry>()
        val colors = mutableListOf<Int>()

        if (stats.highImpactCount > 0) {
            entries.add(PieEntry(stats.highImpactCount.toFloat(), "Alto"))
            colors.add(getColor(R.color.risk_high))
        }
        if (stats.mediumImpactCount > 0) {
            entries.add(PieEntry(stats.mediumImpactCount.toFloat(), "Médio"))
            colors.add(getColor(R.color.risk_medium))
        }
        if (stats.lowImpactCount > 0) {
            entries.add(PieEntry(stats.lowImpactCount.toFloat(), "Baixo"))
            colors.add(getColor(R.color.risk_low))
        }

        // Se não houver riscos, não configure o gráfico
        if (entries.isEmpty()) return

        val dataSet = PieDataSet(entries, "Distribuição por Impacto")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChartImpact))
        data.setValueTextSize(12f)
        data.setValueTextColor(getColor(android.R.color.white))

        binding.pieChartImpact.apply {
            this.data = data
            description.isEnabled = false
            legend.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            transparentCircleRadius = 45f
            setUsePercentValues(true)
            setEntryLabelTextSize(0f) // Esconde os labels dentro do gráfico
            animateY(1000)
            invalidate()
        }
    }
}