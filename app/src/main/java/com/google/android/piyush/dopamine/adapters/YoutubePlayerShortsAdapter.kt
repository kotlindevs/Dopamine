package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.databinding.ItemYoutubePlayerShortsBinding
import com.google.android.piyush.youtube.model.SearchResponse.Contents.TwoColumnSearchResultsRenderer.PrimaryContents.SectionListRenderer.Content.ItemSectionRenderer.Content.ReelShelfRenderer.Item.ShortsLockupViewModel

class YoutubePlayerShortsAdapter(private val shorts : MutableList<ShortsLockupViewModel>) :
RecyclerView.Adapter<YoutubePlayerShortsAdapter.ShortsViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShortsViewHolder {
        val binding = ItemYoutubePlayerShortsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShortsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShortsViewHolder,
        position: Int
    ) {
        val short = shorts[position]
        holder.bind(short)
    }

    override fun getItemCount(): Int {
        return shorts.size
    }

    inner class ShortsViewHolder(val binding : ItemYoutubePlayerShortsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shortsViewModel : ShortsLockupViewModel) {
            val url = shortsViewModel.thumbnail?.sources?.firstOrNull()?.url.toString()
            val title = shortsViewModel.overlayMetadata?.primaryText?.content?.toString()
            val views = shortsViewModel.overlayMetadata?.secondaryText?.content?.toString()
            val videoId = shortsViewModel.onTap?.innertubeCommand?.reelWatchEndpoint?.videoId.toString()
            binding.apply {
                this.shortsTitle.text = title
                this.shortsViews.text = views
                Glide.with(this.root).load(url).into(this.shortsImage)
            }
        }
    }
}