package com.example.gestaoderisco.view

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestaoderisco.R
import com.example.gestaoderisco.analysis.StatisticsAnalyzer
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.databinding.ActivityStoreDetailsBinding
import com.example.gestaoderisco.viewmodel.StoreDetailsViewModel
import com.example.gestaoderisco.viewmodel.StoreDetailsViewModelFactory
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoreDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreDetailsBinding
    private lateinit var storeName: String
    private val viewModel: StoreDetailsViewModel by viewModels {
        StoreDetailsViewModelFactory(storeName)
    }
    private lateinit var ocorrenciaAdapter: OcorrenciaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeName = intent.getStringExtra(EXTRA_STORE_NAME) ?: "Loja Desconhecida"

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = storeName
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Assumindo que OcorrenciaAdapter já existe no projeto
        ocorrenciaAdapter = OcorrenciaAdapter { ocorrencia ->
            showStatusChangeDialog(ocorrencia)
        }
        binding.rvStoreHistory.apply {
            adapter = ocorrenciaAdapter
            layoutManager = LinearLayoutManager(this@StoreDetailsActivity)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.ocorrencias.collectLatest { ocorrencias ->
                ocorrenciaAdapter.submitList(ocorrencias)

                if (ocorrencias.isNotEmpty()) {
                    val analyzer = StatisticsAnalyzer(ocorrencias)
                    setupPieChart(analyzer.getStatisticsByArea())
                }
            }
        }
    }

    private fun setupPieChart(dataMap: Map<String, Int>) {
        val entries = dataMap.map { (category, count) ->
            PieEntry(count.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Categorias").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val pieData = PieData(dataSet)

        binding.pieChartStoreCategories.apply {
            data = pieData
            description.isEnabled = false
            centerText = "Categorias"
            setEntryLabelColor(Color.BLACK)
            animateY(1000)
            invalidate()
        }
    }

    private fun showStatusChangeDialog(ocorrencia: Ocorrencia) {
        val statusOptions = resources.getStringArray(R.array.occurrence_status)
        val currentStatusIndex = statusOptions.indexOf(ocorrencia.status)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_status_title))
            .setSingleChoiceItems(statusOptions, currentStatusIndex) { dialog, which ->
                val newStatus = statusOptions[which]
                if (newStatus != ocorrencia.status) {
                    viewModel.updateOcorrenciaStatus(ocorrencia.id, newStatus)
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    companion object {
        const val EXTRA_STORE_NAME = "EXTRA_STORE_NAME"
    }
}