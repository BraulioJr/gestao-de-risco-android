package com.example.gestaoderisco.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.example.gestaoderisco.adapter.OcorrenciaAdapter
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.viewmodel.OcorrenciasUiState
import com.example.gestaoderisco.viewmodel.OcorrenciasViewModel
import com.example.gestaoderisco.viewmodel.OcorrenciasViewModelFactory
import kotlinx.coroutines.launch

class OcorrenciasSalvasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var loadingView: View
    private lateinit var searchEditText: TextInputEditText
    private lateinit var ocorrenciaAdapter: OcorrenciaAdapter

    private val viewModel: OcorrenciasViewModel by viewModels { OcorrenciasViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocorrencias_salvas)

        recyclerView = findViewById(R.id.rv_ocorrencias)
        emptyView = findViewById(R.id.tv_empty_list)
        loadingView = findViewById(R.id.progress_bar) // Adicione uma ProgressBar ao seu layout
        searchEditText = findViewById(R.id.et_search_loja)

        setupRecyclerView()
        setupSearch()
        observeUiState()
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchQueryChanged(s.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        // O adapter agora é um ListAdapter, que é mais simples e eficiente para esta arquitetura.
        ocorrenciaAdapter = OcorrenciaAdapter { ocorrencia ->
            val intent = Intent(this, OcorrenciaDetailActivity::class.java).apply {
                putExtra("OCORRENCIA_ID", ocorrencia.id)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ocorrenciaAdapter
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingView.isVisible = state is OcorrenciasUiState.Loading
                    recyclerView.isVisible = state is OcorrenciasUiState.Success

                    if (state is OcorrenciasUiState.Success) {
                        emptyView.isVisible = state.ocorrencias.isEmpty()
                        ocorrenciaAdapter.submitList(state.ocorrencias)
                    }
                }
            }
        }
    }
}