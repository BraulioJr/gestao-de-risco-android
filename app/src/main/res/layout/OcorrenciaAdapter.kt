package com.example.gestaoderisco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.data.local.Ocorrencia
import com.example.gestaoderisco.databinding.ItemOcorrenciaBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OcorrenciaAdapter(
    private val onItemClicked: (Ocorrencia) -> Unit
) : ListAdapter<Ocorrencia, OcorrenciaAdapter.OcorrenciaViewHolder>(OcorrenciaDiffCallback()) {

    // Mova o SimpleDateFormat para fora do ViewHolder para ser reutilizado
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class OcorrenciaViewHolder(private val binding: ItemOcorrenciaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ocorrencia: Ocorrencia) {
            binding.apply {
                tvLoja.text = ocorrencia.loja
                tvData.text = dateFormat.format(ocorrencia.data)
                tvValor.text = String.format(Locale.getDefault(), "R$ %.2f", ocorrencia.valor)

                if (ocorrencia.isSynced) {
                    ivSyncStatus.setImageResource(R.drawable.ic_cloud_done)
                    ivSyncStatus.contentDescription = "Sincronizado"
                } else {
                    ivSyncStatus.setImageResource(R.drawable.ic_cloud_off)
                    ivSyncStatus.contentDescription = "Pendente de sincronização"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OcorrenciaViewHolder {
        val binding = ItemOcorrenciaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = OcorrenciaViewHolder(binding)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: OcorrenciaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OcorrenciaDiffCallback : DiffUtil.ItemCallback<Ocorrencia>() {
        override fun areItemsTheSame(oldItem: Ocorrencia, newItem: Ocorrencia): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ocorrencia, newItem: Ocorrencia): Boolean {
            return oldItem == newItem
        }
    }
}