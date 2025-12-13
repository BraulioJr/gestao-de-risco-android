package com.example.project_gestoderisco.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_gestoderisco.MainActivity
import com.example.project_gestoderisco.databinding.FragmentDashboardBinding
import com.example.project_gestoderisco.model.UserProfile
import java.text.NumberFormat
import java.util.*

// ATENÇÃO: Esta é uma versão revertida e simplificada.
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var rankingAdapter: RankingAdapter
    private var userProfile: UserProfile? = null

    private var lastStartDate: Long = 0
    private var lastEndDate: Long = 0
    private var lastLojaId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfile = (activity as? MainActivity)?.getUserProfile()

        setupRecyclerView()
        setupFilters()
        observeViewModel()
        setupExportButton()

        binding.btnAplicarFiltro.performClick()
    }

    private fun setupRecyclerView() {
        rankingAdapter = RankingAdapter()
        binding.rvRanking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rankingAdapter
        }
    }

    private fun setupFilters() {
        val lojas = listOf("Todas", "Loja 01 - Centro", "Loja 08 - Shopping", "Loja 15 - Via Norte")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, lojas)
        binding.actvLojaFiltro.setAdapter(adapter)

        binding.btnAplicarFiltro.setOnClickListener {
            loadDataWithFilters()
        }
    }

    private fun setupExportButton() {
        if (userProfile?.perfilAcesso == "GESTOR") {
            binding.btnExportarCsv.visibility = View.VISIBLE
            binding.btnExportarCsv.setOnClickListener {
                viewModel.exportarOcorrenciasCsv(lastStartDate, lastEndDate, lastLojaId)
            }
        } else {
            binding.btnExportarCsv.visibility = View.GONE
        }
    }

    private fun loadDataWithFilters() {
        val cal = Calendar.getInstance()
        lastEndDate = cal.timeInMillis
        cal.add(Calendar.MONTH, -1)
        lastStartDate = cal.timeInMillis

        lastLojaId = when {
            binding.actvLojaFiltro.text.toString().startsWith("Loja 01") -> 1
            binding.actvLojaFiltro.text.toString().startsWith("Loja 08") -> 8
            binding.actvLojaFiltro.text.toString().startsWith("Loja 15") -> 15
            else -> null
        }

        viewModel.loadDashboard(lastStartDate, lastEndDate, lastLojaId)
    }

    private fun observeViewModel() {
        viewModel.kpi.observe(viewLifecycleOwner) { kpi ->
            val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            binding.tvValorTotal.text = nf.format(kpi.totalValorRecuperado)
            binding.tvTotalOcorrencias.text = kpi.totalOcorrencias.toString()
            binding.tvIndice.text = String.format(Locale.getDefault(), "%.0f%%", kpi.indiceEfetividade * 100.0)
        }

        viewModel.ranking.observe(viewLifecycleOwner) { ranking ->
            rankingAdapter.submitList(ranking)
        }

        viewModel.exportUrl.observe(viewLifecycleOwner) { url ->
            url?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnExportarCsv.isEnabled = !isLoading
            binding.btnAplicarFiltro.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}