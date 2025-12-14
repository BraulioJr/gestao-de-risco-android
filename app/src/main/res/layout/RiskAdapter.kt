package com.example.project_gestoderisco.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ItemRiskBinding
import com.example.project_gestoderisco.model.Risk
import com.google.android.material.chip.Chip

class RiskAdapter(
    private val onItemClicked: (Risk) -> Unit,
    private val onItemLongClicked: (Risk) -> Unit
) :
    ListAdapter<Risk, RiskAdapter.RiskViewHolder>(RiskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskViewHolder {
        val binding = ItemRiskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiskViewHolder, position: Int) {
        val risk = getItem(position)
        holder.bind(risk)
        holder.itemView.setOnClickListener {
            onItemClicked(risk)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClicked(risk)
            true // Indica que o evento foi consumido
        }
    }

    class RiskViewHolder(private val binding: ItemRiskBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula os dados de um objeto Risk às views do layout do item.
         */
        fun bind(risk: Risk) {
            binding.textViewRiskName.text = risk.name
            binding.textViewRiskDescription.text = risk.description

            val context = binding.root.context

            // Configura o texto e a cor do nível de risco dinamicamente
            val impactString = when (risk.impact) {
                3 -> "ALTO"
                2 -> "MÉDIO"
                1 -> "BAIXO"
                else -> "DESCONHECIDO"
            }
            val impactText = "IMPACTO $impactString"
            binding.textViewRiskLevel.text = impactText

            val impactColor = when (risk.impact) {
                3 -> R.color.risk_high
                2 -> R.color.risk_medium
                1 -> R.color.risk_low
                else -> R.color.grey_500 // Cor padrão
            }
            binding.textViewRiskLevel.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, impactColor)
            )

            // --- INÍCIO DO CÓDIGO PARA POPULAR O CHIPGROUP ---

            val chipGroup = binding.chipGroupLgpdCategories

            // 1. Limpa chips antigos para evitar duplicatas ao reciclar a view
            chipGroup.removeAllViews()

            // 2. Verifica se há categorias de dados LGPD para exibir
            val categories = risk.lgpdDetails?.dataCategories
            if (!categories.isNullOrEmpty()) {
                chipGroup.visibility = View.VISIBLE // Garante que o grupo esteja visível

                // 3. Cria e adiciona um Chip para cada categoria
                categories.forEach { categoryName ->
                    val chip = Chip(chipGroup.context).apply {
                        text = categoryName
                        // Opcional: Você pode aplicar um estilo customizado aqui
                        // setChipStyle(R.style.Widget_App_Chip_Suggestion)
                    }
                    chipGroup.addView(chip)
                }
            } else {
                // 4. Oculta o ChipGroup se não houver categorias
                chipGroup.visibility = View.GONE
            }

            // --- FIM DO CÓDIGO PARA POPULAR O CHIPGROUP ---
        }
    }

    private class RiskDiffCallback : DiffUtil.ItemCallback<Risk>() {
        override fun areItemsTheSame(oldItem: Risk, newItem: Risk): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Risk, newItem: Risk): Boolean {
            return oldItem == newItem
        }
    }
}