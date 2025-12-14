package com.example.project_gestoderisco.view // O pacote foi corrigido para corresponder à pasta 'view'

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ActivityOcorrenciaDetailBinding
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.viewmodel.RiskDetailUiState
import com.example.project_gestoderisco.viewmodel.RiskDetailViewModel
import com.example.project_gestoderisco.viewmodel.SaveResultUiState
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class OcorrenciaDetailActivity : AppCompatActivity() {

    // Companion object para a TAG de logging
    private companion object {
        private const val TAG = "OcorrenciaDetail"
    }
    // 1. Binding atualizado para o novo layout
    private lateinit var binding: ActivityOcorrenciaDetailBinding
    private val viewModel: RiskDetailViewModel by viewModels()
    private var riskId: String? = null
    private var selectedAttachmentUri: Uri? = null
    private var currentAttachmentUrl: String? = null

    // ActivityResultLauncher para o seletor de fotos (ainda relevante)
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            handleSelectedImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usa o binding do novo layout
        binding = ActivityOcorrenciaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        riskId = intent.getStringExtra("RISK_ID")

        setupToolbar()
        setupDropdownMenus()
        setupListeners()
        observeStates()

        if (riskId != null) {
            viewModel.loadRisk(riskId!!)
        } else {
            // Modo de criação, ajusta o título da toolbar
            binding.toolbarDetail.title = "Nova Ocorrência"
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Habilita o botão de voltar
    }

    private fun setupDropdownMenus() {
        val riskLevels = resources.getStringArray(R.array.risk_levels)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, riskLevels)

        (binding.menuProbability.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.menuImpact.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.buttonAddAttachment.setOnClickListener {
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    // 2. Infla o menu de opções na Toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_ocorrencia_detail, menu)
        return true
    }

    // 3. Lida com os cliques nos itens do menu (Salvar, Excluir, Voltar)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Ação do botão de voltar
                true
            }
            R.id.action_save -> {
                saveRiskData()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveRiskData() {
        // 4. Coleta os dados dos novos componentes de UI
        val name = binding.textFieldRiskName.editText?.text.toString()
        val description = binding.textFieldDescription.editText?.text.toString()
        val probability = binding.menuProbability.editText?.text.toString()
        val impact = binding.menuImpact.editText?.text.toString()

        Log.d(TAG, "Salvando dados: Nome='$name', Probabilidade='$probability', Impacto='$impact'")

        // TODO: Mapear os textos ("Baixa", "Média", "Alta") para valores numéricos se necessário pelo ViewModel
        // Exemplo simples:
        val probabilityValue = mapRiskLevelToLong(probability)
        val impactValue = mapRiskLevelToLong(impact)

        viewModel.saveRisk(
            id = riskId,
            name = name,
            description = description,
            impact = impactValue,
            probability = probabilityValue,
            attachmentUri = selectedAttachmentUri,
            currentAttachmentUrl = currentAttachmentUrl
        )
    }

    private fun observeStates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.riskState.collect { state ->
                        binding.progressBar.isVisible = state is RiskDetailUiState.Loading
                        if (state is RiskDetailUiState.Success) {
                            populateUi(state.risk)
                        } else if (state is RiskDetailUiState.Error) {
                            Toast.makeText(this@RiskDetailActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                launch {
                    viewModel.saveState.collect { state ->
                        binding.progressBar.isVisible = state is SaveResultUiState.Loading
                        binding.toolbarDetail.menu.findItem(R.id.action_save)?.isEnabled = state !is SaveResultUiState.Loading
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
        binding.toolbarDetail.title = "Editar Ocorrência"
        binding.textFieldRiskName.editText?.setText(risk.name)
        binding.textFieldDescription.editText?.setText(risk.description)

        // TODO: Mapear os valores numéricos de volta para os textos ("Baixa", "Média", "Alta")
        (binding.menuProbability.editText as? AutoCompleteTextView)?.setText(mapLongToRiskLevel(risk.probability), false)
        (binding.menuImpact.editText as? AutoCompleteTextView)?.setText(mapLongToRiskLevel(risk.impact), false)

        currentAttachmentUrl = risk.attachmentUrl
        risk.attachmentUrl?.let { url ->
            if (url.isNotEmpty()) {
                binding.imageViewAttachment.isVisible = true
                Glide.with(this).load(url).into(binding.imageViewAttachment)
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(R.string.delete_confirmation_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                // TODO: Chamar a função de exclusão no ViewModel
                riskId?.let {
                    // viewModel.deleteRisk(it) // Esta linha será implementada no próximo passo
                    Toast.makeText(this, "Excluindo...", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun handleSelectedImage(uri: Uri) {
        selectedAttachmentUri = uri
        currentAttachmentUrl = null // Limpa a URL antiga, pois uma nova imagem foi selecionada
        binding.imageViewAttachment.isVisible = true
        Glide.with(this)
            .load(uri)
            .into(binding.imageViewAttachment)
    }
    // Funções auxiliares para mapeamento (exemplo)
    private fun mapRiskLevelToLong(level: String): Long = when (level) {
        "Baixa" -> 1L
        "Média" -> 2L
        "Alta" -> 3L
        else -> 0L
    }

    private fun mapLongToRiskLevel(value: Long): String = when (value) {
        1L -> "Baixa"
        2L -> "Média"
        3L -> "Alta"
        else -> ""
    }
}