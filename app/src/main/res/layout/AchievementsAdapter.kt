package com.example.gestaoderisco.view.gamification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Achievement
import com.google.android.material.card.MaterialCardView

class AchievementsAdapter : ListAdapter<Achievement, AchievementsAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.card_achievement)
        private val icon: ImageView = itemView.findViewById(R.id.img_achievement_icon)
        private val title: TextView = itemView.findViewById(R.id.tv_achievement_title)
        private val description: TextView = itemView.findViewById(R.id.tv_achievement_desc)
        private val lockOverlay: ImageView = itemView.findViewById(R.id.img_lock_overlay)

        fun bind(achievement: Achievement) {
            title.setText(achievement.titleResId)
            description.setText(achievement.descriptionResId)
            icon.setImageResource(achievement.iconResId)

            if (achievement.isUnlocked) {
                card.alpha = 1.0f
                lockOverlay.visibility = View.GONE
                icon.setColorFilter(itemView.context.getColor(R.color.gold))
            } else {
                card.alpha = 0.6f
                lockOverlay.visibility = View.VISIBLE
                icon.setColorFilter(itemView.context.getColor(R.color.md_theme_outline))
            }
        }
    }

    class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean = oldItem == newItem
    }
}