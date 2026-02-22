package com.example.project_gestoderisco.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.network.ReleaseNoteItem
import com.example.project_gestoderisco.databinding.ItemReleaseNoteBinding

class ReleaseNotesAdapter : RecyclerView.Adapter<ReleaseNotesAdapter.ViewHolder>() {

    private var items: List<ReleaseNoteItem> = emptyList()

    fun submitList(newItems: List<ReleaseNoteItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReleaseNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ItemReleaseNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReleaseNoteItem) {
            binding.textTitle.text = item.title
            binding.textDate.text = item.updated
            binding.textSummary.text = item.summary
        }
    }
}