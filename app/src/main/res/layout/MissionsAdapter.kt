package com.example.gestaoderisco.view.gamification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.CampaignMission
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator

class MissionsAdapter : ListAdapter<CampaignMission, MissionsAdapter.MissionViewHolder>(MissionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mission, parent, false)
        return MissionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_mission_title)
        private val description: TextView = itemView.findViewById(R.id.tv_mission_desc)
        private val progress: LinearProgressIndicator = itemView.findViewById(R.id.progress_mission)
        private val progressText: TextView = itemView.findViewById(R.id.tv_mission_progress_text)
        private val xpChip: Chip = itemView.findViewById(R.id.chip_xp_reward)

        fun bind(mission: CampaignMission) {
            title.setText(mission.titleResId)
            description.setText(mission.descriptionResId)
            progress.max = mission.targetValue
            progress.progress = mission.currentValue
            progressText.text = "${mission.currentValue}/${mission.targetValue}"
            xpChip.text = "+${mission.xpReward} XP"
        }
    }

    class MissionDiffCallback : DiffUtil.ItemCallback<CampaignMission>() {
        override fun areItemsTheSame(oldItem: CampaignMission, newItem: CampaignMission): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CampaignMission, newItem: CampaignMission): Boolean = oldItem == newItem
    }
}