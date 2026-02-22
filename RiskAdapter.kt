package com.example.gestaoderisco.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import com.google.android.material.chip.Chip
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiskAdapter(
    private val onClick: (Ocorrencia) -> Unit,
    private val onGoeClick: (Ocorrencia) -> Unit = {}
) :
    ListAdapter<Ocorrencia, RiskAdapter.RiskViewHolder>(RiskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_risk, parent, false)
        return RiskViewHolder(view, onClick, onGoeClick)
    }

    override fun onBindViewHolder(holder: RiskViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class RiskViewHolder(
        itemView: View,
        val onClick: (Ocorrencia) -> Unit,
        val onGoeClick: (Ocorrencia) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {

        private val tvStore: TextView = itemView.findViewById(R.id.tvStore)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvValue: TextView = itemView.findViewById(R.id.tvValue)
        private val chipGoe: Chip = itemView.findViewById(R.id.chipGoe)
        private val chipCategory: Chip = itemView.findViewById(R.id.chipCategory)

        fun bind(item: Ocorrencia) {
            tvStore.text = item.loja
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale("pt", "BR"))
            tvDate.text = dateFormat.format(Date(item.dataRegistro))

            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            tvValue.text = currencyFormat.format(item.valorEstimado)

            chipCategory.text = item.categoriaProduto

            // --- LÓGICA TÁTICA (GOE) ---
            // Define o que é um "Risco Alto" para acionar o GOE
            // Regra: Valor > R$ 1.000,00 OU Categorias Críticas (ex: Eletrônicos)
            val isHighValue = item.valorEstimado >= 1000.0
            val isCriticalCategory = item.categoriaProduto.equals("Eletrônicos", ignoreCase = true) || 
                                     item.categoriaProduto.equals("Bebidas", ignoreCase = true)

            if (isHighValue || isCriticalCategory) {
                chipGoe.visibility = View.VISIBLE
                chipGoe.isEnabled = true
                chipGoe.setOnClickListener { onGoeClick(item) }
            } else {
                chipGoe.visibility = View.GONE
            }

            itemView.setOnClickListener { onClick(item) }
        }
    }

    class RiskDiffCallback : DiffUtil.ItemCallback<Ocorrencia>() {
        override fun areItemsTheSame(oldItem: Ocorrencia, newItem: Ocorrencia): Boolean {
            return oldItem.dataRegistro == newItem.dataRegistro && oldItem.loja == newItem.loja
        }

        override fun areContentsTheSame(oldItem: Ocorrencia, newItem: Ocorrencia): Boolean {
            return oldItem == newItem
        }
    }
}