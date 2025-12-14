package com.example.project_gestoderisco.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.adapter.RiskAdapter
import com.example.project_gestoderisco.databinding.FragmentRiskListBinding
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.viewmodel.RiskUiState
import com.example.project_gestoderisco.viewmodel.SortOrder
import com.example.project_gestoderisco.viewmodel.RiskViewModel
import com.example.project_gestoderisco.viewmodel.RiskViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RiskListFragment : Fragment() {

    private var _binding: FragmentRiskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RiskViewModel by viewModels { RiskViewModelFactory() }
    private lateinit var riskAdapter: RiskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupMenu()
        setupFab()
        observeRiskUpdates()
    }

    private fun setupRecyclerView() {
        riskAdapter = RiskAdapter(
            onItemClicked = { risk ->
                // Abre a AddRiskActivity em modo de edição, passando o risco clicado
                val intent = Intent(requireContext(), AddRiskActivity::class.java).apply {
                    putExtra(AddRiskActivity.EXTRA_RISK, risk)
                }
                startActivity(intent)
            },
            onItemLongClicked = { risk ->
                showDeleteConfirmationDialog(risk)
            }
        )
        binding.recyclerViewRisks.apply {
            adapter = riskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showDeleteConfirmationDialog(risk: Risk) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(getString(R.string.delete_confirmation_message, risk.name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteRisk(risk.id)
            }
            .show()
    }

    private fun setupFab() {
        binding.fabAddRisk.setOnClickListener {
            val intent = Intent(requireContext(), AddRiskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.sort_menu, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        // Não precisamos fazer nada aqui, a busca é em tempo real
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.fetchRisks(newSearchQuery = newText.orEmpty())
                        return true
                    }
                })
            }

            override fun onPrepareMenu(menu: Menu) {
                // Marca o item de menu correspondente à ordenação atual
                when (viewModel.sortOrder.value) {
                    SortOrder.BY_DATE_DESC -> menu.findItem(R.id.sort_by_date)?.isChecked = true
                    SortOrder.BY_IMPACT_DESC -> menu.findItem(R.id.sort_by_impact_desc)?.isChecked = true
                    SortOrder.BY_IMPACT_ASC -> menu.findItem(R.id.sort_by_impact_asc)?.isChecked = true
                    SortOrder.BY_PROBABILITY_DESC -> menu.findItem(R.id.sort_by_probability_desc)?.isChecked = true
                    SortOrder.BY_PROBABILITY_ASC -> menu.findItem(R.id.sort_by_probability_asc)?.isChecked = true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val newSortOrder = when (menuItem.itemId) {
                    R.id.sort_by_date -> SortOrder.BY_DATE_DESC
                    R.id.sort_by_impact_desc -> SortOrder.BY_IMPACT_DESC
                    R.id.sort_by_impact_asc -> SortOrder.BY_IMPACT_ASC
                    R.id.sort_by_probability_desc -> SortOrder.BY_PROBABILITY_DESC
                    R.id.sort_by_probability_asc -> SortOrder.BY_PROBABILITY_ASC
                    else -> return false
                }
                viewModel.fetchRisks(newSortOrder)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeRiskUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state is RiskUiState.Loading
                    binding.recyclerViewRisks.isVisible = state is RiskUiState.Success
                    
                    val isListEmpty = (state is RiskUiState.Success && state.risks.isEmpty())
                    binding.textViewEmptyList.isVisible = isListEmpty

                    when (state) {
                        is RiskUiState.Success -> {
                            riskAdapter.submitList(state.risks)
                        }
                        is RiskUiState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        is RiskUiState.Loading -> { /* ProgressBar já está visível */ }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Previne vazamentos de memória
    }
}