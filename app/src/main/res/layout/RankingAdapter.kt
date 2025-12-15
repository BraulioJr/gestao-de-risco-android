package com.example.gestaoderisco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.databinding.ItemRankingFiscalBinding
import com.example.gestaoderisco.viewmodel.RankingFiscal

class RankingAdapter : ListAdapter<RankingFiscal, RankingAdapter.RankingViewHolder>(RankingDiffCallback()) {

    // Guarda a posição do último item que foi animado
    private var lastPosition = -1

    /**
     * ViewHolder para o item do ranking.
     * Usa ViewBinding para acessar as views de forma segura.
     */
    inner class RankingViewHolder(private val binding: ItemRankingFiscalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rankingItem: RankingFiscal, position: Int) {
            binding.apply {
                // Adiciona 1 à posição para um ranking que começa em "1."
                tvRankingPosition.text = "${position + 1}."
                tvFiscalName.text = rankingItem.nomeFiscal
                tvFiscalLoja.text = rankingItem.loja
                // Formata o valor para o padrão monetário brasileiro
                tvFiscalValue.text = String.format("R$ %.2f", rankingItem.valorRecuperado)
            }
        }
    }

    /**
     * Callback para o ListAdapter calcular as diferenças entre as listas.
     * Isso permite animações eficientes e atualizações de performance.
     */
    class RankingDiffCallback : DiffUtil.ItemCallback<RankingFiscal>() {
        override fun areItemsTheSame(oldItem: RankingFiscal, newItem: RankingFiscal): Boolean {
            // Supondo que a combinação de nome e loja seja única.
            // Se você tiver um ID único, use-o aqui.
            return oldItem.nomeFiscal == newItem.nomeFiscal && oldItem.loja == newItem.loja
        }

        override fun areContentsTheSame(oldItem: RankingFiscal, newItem: RankingFiscal): Boolean {
            // Verifica se os dados que afetam a UI mudaram.
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingFiscalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val rankingItem = getItem(position)
        holder.bind(rankingItem, position)
        // Aplica a animação de fade-in
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // Anima apenas se o item ainda não foi exibido
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.fade_in)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    /**
     * Limpa a animação quando a view é reciclada.
     * Isso garante que a animação possa ser executada novamente se o item reaparecer na tela.
     */
    override fun onViewDetachedFromWindow(holder: RankingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }
}