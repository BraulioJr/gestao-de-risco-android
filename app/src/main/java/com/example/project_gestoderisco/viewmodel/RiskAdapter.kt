package com.example.project_gestoderisco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ItemRiskBinding
import com.example.project_gestoderisco.model.Risk

class RiskAdapter(private val onItemClicked: (Risk) -> Unit) :
    ListAdapter<Risk, RiskAdapter.RiskViewHolder>(RiskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskViewHolder {
        val binding = ItemRiskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiskViewHolder, position: Int) {
        val risk = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(risk)
        }
        holder.bind(risk)
    }

    class RiskViewHolder(private val binding: ItemRiskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(risk: Risk) {
            binding.textViewRiskName.text = risk.name
            binding.textViewRiskDescription.text = risk.description

            // Lógica para definir o nível do risco (texto e cor)
            // Esta é uma implementação simples, você pode torná-la mais complexa.
            val context = binding.root.context
            when {
                risk.impact >= 7 -> {
                    binding.textViewRiskLevel.text = "IMPACTO ALTO"
                    binding.textViewRiskLevel.background = ContextCompat.getDrawable(context, R.drawable.bg_risk_level_high)
                }
                risk.impact >= 4 -> {
                    binding.textViewRiskLevel.text = "IMPACTO MÉDIO"
                    binding.textViewRiskLevel.background = ContextCompat.getDrawable(context, R.drawable.bg_risk_level_medium)
                }
                else -> {
                    binding.textViewRiskLevel.text = "IMPACTO BAIXO"
                    binding.textViewRiskLevel.background = ContextCompat.getDrawable(context, R.drawable.bg_risk_level_low)
                }
            }
        }
    }
}

class RiskDiffCallback : DiffUtil.ItemCallback<Risk>() {
    override fun areItemsTheSame(oldItem: Risk, newItem: Risk): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Risk, newItem: Risk): Boolean {
        return oldItem == newItem
    }
}