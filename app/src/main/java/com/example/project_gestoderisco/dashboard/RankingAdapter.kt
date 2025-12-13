package com.example.project_gestoderisco.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project_gestoderisco.R
import java.text.NumberFormat
import java.util.*

class RankingAdapter : ListAdapter<FiscalRankingItem, RankingAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FiscalRankingItem>() {
            override fun areItemsTheSame(oldItem: FiscalRankingItem, newItem: FiscalRankingItem) = oldItem.fiscalId == newItem.fiscalId
            override fun areContentsTheSame(oldItem: FiscalRankingfItem, newItem: FiscalRankingItem) = oldItem == newItem
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNome: TextView = view.findViewById(R.id.tvNomeFiscal)
        val tvTotal: TextView = view.findViewById(R.id.tvTotalFiscal)
        val tvQtd: TextView = view.findViewById(R.id.tvQtdFiscal)
        val tvEfic: TextView = view.findViewById(R.id.tvEficFiscal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ranking_fiscal, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.tvNome.text = item.nome ?: "ID: ${item.fiscalId}"
        val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        holder.tvTotal.text = nf.format(item.totalRecuperado)
        holder.tvQtd.text = "${item.qtdOcorrencias} oc."
        holder.tvEfic.text = String.format(Locale.getDefault(), "%.0f%%", item.eficiencia * 100.0)
    }
}