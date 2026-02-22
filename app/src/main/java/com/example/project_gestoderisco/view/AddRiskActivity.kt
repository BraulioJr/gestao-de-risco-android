package com.example.project_gestoderisco.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ActivityAddRiskBinding
import com.example.project_gestoderisco.repository.RiskRepository
import com.example.project_gestoderisco.viewmodel.AddRiskViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AddRiskActivity : AppCompatActivity() {

	private lateinit var binding: ActivityAddRiskBinding
	private lateinit var viewModel: AddRiskViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAddRiskBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setupViewModel()
		setupListeners()
		observeViewModel()
	}

	private fun setupViewModel() {
		// TODO: Substituir por Injeção de Dependência (Hilt) ou Singleton do Database
		// Exemplo simplificado de factory manual:
		val db =
			com.example.project_gestoderisco.data.local.AppDatabase.getDatabase(applicationContext)

		val repository =
			RiskRepository(applicationContext, db.riskDao(), FirebaseFirestore.getInstance())

		val factory = object : ViewModelProvider.Factory {
			override fun <T : ViewModel> create(modelClass: Class<T>): T {
				return AddRiskViewModel(repository) as T
			}
		}
		viewModel = ViewModelProvider(this, factory)[AddRiskViewModel::class.java]
	}

	private fun setupListeners() {
		binding.buttonSaveRisk.setOnClickListener {
			if (validateFields()) {
				saveRisk()
			}
		}
	}

	private fun validateFields(): Boolean {
		var isValid = true

		// Validação Nome
		if (binding.editTextRiskName.text.isNullOrBlank()) {
			binding.inputLayoutRiskName.error = getString(R.string.empty_fields)
			isValid = false
		} else {
			binding.inputLayoutRiskName.error = null
		}

		// Validação Descrição
		if (binding.editTextDescription.text.isNullOrBlank()) {
			binding.inputLayoutDescription.error = getString(R.string.empty_fields)
			isValid = false
		} else {
			binding.inputLayoutDescription.error = null
		}

		// Validação Análise de Impacto (Novo Campo)
		if (binding.editTextImpactAnalysis.text.isNullOrBlank()) {
			binding.inputLayoutImpactAnalysis.error = getString(R.string.validation_error_impact)
			isValid = false
		} else {
			binding.inputLayoutImpactAnalysis.error = null
		}

		// Validação Ações de Redução (Novo Campo)
		if (binding.editTextMitigation.text.isNullOrBlank()) {
			binding.inputLayoutMitigation.error = getString(R.string.validation_error_mitigation)
			isValid = false
		} else {
			binding.inputLayoutMitigation.error = null
		}

		return isValid
	}

	private fun saveRisk() {
		val name = binding.editTextRiskName.text.toString()
		val description = binding.editTextDescription.text.toString()
		val probability = binding.editTextProbability.text.toString().toIntOrNull() ?: 0
		val impact = binding.editTextImpact.text.toString().toIntOrNull() ?: 0
		val impactAnalysis = binding.editTextImpactAnalysis.text.toString()
		val mitigation = binding.editTextMitigation.text.toString()

		viewModel.saveRisk(name, description, probability, impact, impactAnalysis, mitigation)
	}

	private fun observeViewModel() {
		viewModel.saveResult.observe(this) { result ->
			result.onSuccess {
				Toast.makeText(this, "Risco salvo com sucesso!", Toast.LENGTH_SHORT).show()
				finish()
			}.onFailure { e ->
				Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
			}
		}

		viewModel.isLoading.observe(this) { isLoading ->
			binding.buttonSaveRisk.isEnabled = !isLoading
			binding.buttonSaveRisk.text = if (isLoading) "Salvando..." else getString(R.string.save)
		}
	}
}

class RiskAdapter(private val onClick: (Risk) -> Unit) :
	ListAdapter<Risk, RiskAdapter.RiskViewHolder>(RiskDiffCallback()) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskViewHolder {
		val binding = ItemRiskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return RiskViewHolder(binding, onClick)
	}

	override fun onBindViewHolder(holder: RiskViewHolder, position: Int) {
		holder.bind(getItem(position))
	}

	class RiskViewHolder(
		private val binding: ItemRiskBinding,
		private val onClick: (Risk) -> Unit
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(risk: Risk) {
			binding.tvRiskName.text = risk.name
			binding.tvRiskDescription.text = risk.description

			// Exibe o ícone se NÃO estiver sincronizado (isSynced == false)
			binding.ivSyncStatus.visibility = if (risk.isSynced) View.GONE else View.VISIBLE

			binding.root.setOnClickListener { onClick(risk) }
		}
	}

	class RiskDiffCallback : DiffUtil.ItemCallback<Risk>() {
		override fun areItemsTheSame(oldItem: Risk, newItem: Risk): Boolean =
			oldItem.id == newItem.id

		override fun areContentsTheSame(oldItem: Risk, newItem: Risk): Boolean = oldItem == newItem
	}
}