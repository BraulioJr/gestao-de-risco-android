package com.example.project_gestoderisco.ui.gamification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_gestoderisco.data.model.RiskBadge
import com.example.project_gestoderisco.databinding.ItemBadgeBinding

class BadgesAdapter(private val badges: List<RiskBadge>) : RecyclerView.Adapter<BadgesAdapter.BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount(): Int = badges.size

    class BadgeViewHolder(private val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(badge: RiskBadge) {
            binding.tvBadgeTitle.text = badge.title
            binding.tvBadgeDesc.text = badge.description
            binding.ivBadgeIcon.setImageResource(badge.iconRes)

            if (badge.isUnlocked) {
                binding.tvBadgeTitle.setTextColor(Color.YELLOW)
                binding.ivBadgeIcon.setColorFilter(Color.YELLOW)
                binding.root.alpha = 1.0f
            } else {
                binding.tvBadgeTitle.text = "Bloqueado"
                binding.tvBadgeTitle.setTextColor(Color.GRAY)
                binding.tvBadgeDesc.text = "???"
                binding.ivBadgeIcon.setColorFilter(Color.GRAY)
                binding.root.alpha = 0.5f
            }
        }
    }
}
