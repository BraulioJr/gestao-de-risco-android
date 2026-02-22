package com.example.project_gestoderisco.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.example.project_gestoderisco.databinding.*

/**
 * Modelo de UI para os diferentes tipos de itens na tela de Release Notes.
 */
sealed class ReleaseUiModel {
    data class Header(val version: String) : ReleaseUiModel()
    data class Banner(val title: String, val description: String) : ReleaseUiModel()
    data class Section(val title: String) : ReleaseUiModel()
    data class Point(val text: String) : ReleaseUiModel()
    object Footer : ReleaseUiModel()
}

class ReleaseNotesAdapter(
    private val onHistoryClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ReleaseUiModel> = emptyList()
    private var lastPosition = -1

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_BANNER = 1
        private const val TYPE_SECTION = 2
        private const val TYPE_POINT = 3
        private const val TYPE_FOOTER = 4
    }

    fun submitList(newItems: List<ReleaseUiModel>) {
        items = newItems
        lastPosition = -1
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ReleaseUiModel.Header -> TYPE_HEADER
            is ReleaseUiModel.Banner -> TYPE_BANNER
            is ReleaseUiModel.Section -> TYPE_SECTION
            is ReleaseUiModel.Point -> TYPE_POINT
            is ReleaseUiModel.Footer -> TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(ItemReleaseHeaderBinding.inflate(inflater, parent, false))
            TYPE_BANNER -> BannerViewHolder(ItemReleaseBannerBinding.inflate(inflater, parent, false))
            TYPE_SECTION -> SectionViewHolder(ItemReleaseSectionBinding.inflate(inflater, parent, false))
            TYPE_POINT -> PointViewHolder(ItemReleasePointBinding.inflate(inflater, parent, false))
            TYPE_FOOTER -> FooterViewHolder(ItemReleaseFooterBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ReleaseUiModel.Header -> (holder as HeaderViewHolder).bind(item)
            is ReleaseUiModel.Banner -> (holder as BannerViewHolder).bind(item)
            is ReleaseUiModel.Section -> (holder as SectionViewHolder).bind(item)
            is ReleaseUiModel.Point -> (holder as PointViewHolder).bind(item)
            is ReleaseUiModel.Footer -> (holder as FooterViewHolder).bind()
        }
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AlphaAnimation(0.0f, 1.0f)
            animation.duration = 500
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolders
    class HeaderViewHolder(private val binding: ItemReleaseHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReleaseUiModel.Header) {
            binding.tvVersion.text = item.version
        }
    }

    class BannerViewHolder(private val binding: ItemReleaseBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReleaseUiModel.Banner) {
            binding.tvBannerTitle.text = item.title
            binding.tvBannerDesc.text = item.description
        }
    }

    class SectionViewHolder(private val binding: ItemReleaseSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReleaseUiModel.Section) {
            binding.tvSectionTitle.text = item.title
        }
    }

    class PointViewHolder(private val binding: ItemReleasePointBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReleaseUiModel.Point) {
            binding.tvPointText.text = item.text
        }
    }

    inner class FooterViewHolder(private val binding: ItemReleaseFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnHistory.setOnClickListener { onHistoryClick() }
        }
    }
}