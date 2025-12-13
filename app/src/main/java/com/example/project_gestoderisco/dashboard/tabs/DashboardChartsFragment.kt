package com.example.project_gestoderisco.dashboard.tabs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.project_gestoderisco.dashboard.DashboardViewModel
import com.example.project_gestoderisco.dashboard.ProductStat
import com.example.project_gestoderisco.databinding.FragmentDashboardChartsBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class DashboardChartsFragment : Fragment() {

    private var _binding: FragmentDashboardChartsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.productStats.observe(viewLifecycleOwner) {
            setupPieChart(it)
        }
    }

    private fun setupPieChart(stats: List<ProductStat>) {
        val entries = stats.take(5).map { PieEntry(it.count.toFloat(), it.produto) }

        val dataSet = PieDataSet(entries, "Produtos").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        binding.pieChartProducts.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            centerText = "Produtos"
            setEntryLabelColor(Color.BLACK)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}