package com.example.project_gestoderisco.view // O pacote foi corrigido para corresponder à pasta 'view'

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.project_gestoderisco.databinding.ActivityRiskDetailBinding
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.viewmodel.RiskDetailUiState
import com.example.project_gestoderisco.viewmodel.RiskDetailViewModel
import com.example.project_gestoderisco.viewmodel.SaveResultUiState
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class RiskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiskDetailBinding
    private val viewModel: RiskDetailViewModel by viewModels()
    private var riskId: String? = null
    private var selectedAttachmentUri: Uri? = null
    private var currentAttachmentUrl: String? = null

    // ActivityResultLauncher para o seletor de fotos
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            handleSelectedImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        riskId = intent.getStringExtra("RISK_ID")

        setupToolbar()
        setupListeners()
        observeStates()

        if (riskId != null) {
            viewModel.loadRisk(riskId!!)
        } else {
            // Modo de criação, esconde o loading
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish() // Fecha a activity ao clicar no ícone de fechar
        }
    }

    private fun setupListeners() {
        binding.fabSave.setOnClickListener {
            viewModel.saveRisk(
                id = riskId,
                name = binding.editTextName.text.toString(),
                description = binding.editTextDescription.text.toString(),
                impact = binding.sliderImpact.value.toLong(),
                probability = binding.sliderProbability.value.toLong(),
                attachmentUri = selectedAttachmentUri,
                currentAttachmentUrl = currentAttachmentUrl
            )
        }

        binding.buttonAddAttachment.setOnClickListener {
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Atualiza o texto do label do slider de impacto
        binding.sliderImpact.addOnChangeListener { slider, value, _ ->
            binding.textViewImpactLabel.text = "Impacto: ${value.toInt()}"
        }

        // Atualiza o texto do label do slider de probabilidade
        binding.sliderProbability.addOnChangeListener { slider, value, _ ->
            binding.textViewProbabilityLabel.text = "Probabilidade: ${value.toInt()}"
        }
    }

    private fun observeStates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observa o estado de carregamento do risco
                launch {
                    viewModel.riskState.collect { state ->
                        binding.progressBar.visibility = if (state is RiskDetailUiState.Loading) View.VISIBLE else View.GONE
                        if (state is RiskDetailUiState.Success) {
                            populateUi(state.risk)
                        } else if (state is RiskDetailUiState.Error) {
                            Toast.makeText(this@RiskDetailActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                // Observa o resultado da operação de salvar
                launch {
                    viewModel.saveState.collect { state ->
                        binding.progressBar.visibility = if (state is SaveResultUiState.Loading) View.VISIBLE else View.GONE
                        binding.fabSave.isEnabled = state !is SaveResultUiState.Loading

                        if (state is SaveResultUiState.Success) {
                            Toast.makeText(this@RiskDetailActivity, "Risco salvo com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else if (state is SaveResultUiState.Error) {
                            Toast.makeText(this@RiskDetailActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun populateUi(risk: Risk) {
        binding.toolbar.title = "Editar Risco"
        binding.editTextName.setText(risk.name)
        binding.editTextDescription.setText(risk.description)
        binding.sliderImpact.value = risk.impact.toFloat()
        binding.sliderProbability.value = risk.probability.toFloat()
        currentAttachmentUrl = risk.attachmentUrl

        // Dispara o listener manualmente para atualizar os labels
        binding.textViewImpactLabel.text = "Impacto: ${risk.impact.toInt()}"
        binding.textViewProbabilityLabel.text = "Probabilidade: ${risk.probability.toInt()}"

        // Mostra o anexo existente
        risk.attachmentUrl?.let { url ->
            binding.imageViewAttachment.isVisible = true
            Glide.with(this).load(url).into(binding.imageViewAttachment)
        }
    }

    private fun handleSelectedImage(uri: Uri) {
        selectedAttachmentUri = uri
        binding.imageViewAttachment.isVisible = true
        Glide.with(this)
            .load(uri)
            .into(binding.imageViewAttachment)
    }
}