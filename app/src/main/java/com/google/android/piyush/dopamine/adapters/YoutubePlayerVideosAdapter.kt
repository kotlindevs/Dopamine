package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.databinding.ItemYoutubePlayerRelativeVideosBinding
import com.google.android.piyush.youtube.model.SearchResponse
import com.google.android.piyush.youtube.model.SearchResponse.Contents.TwoColumnSearchResultsRenderer.PrimaryContents.SectionListRenderer.Content.ItemSectionRenderer.Content.VideoRenderer

class YoutubePlayerVideosAdapter(private val videos : MutableList<VideoRenderer>) :
RecyclerView.Adapter<YoutubePlayerVideosAdapter.YoutubePlayerVideosViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YoutubePlayerVideosViewHolder {
        val binding = ItemYoutubePlayerRelativeVideosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return YoutubePlayerVideosViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: YoutubePlayerVideosViewHolder,
        position: Int
    ) {
        val video = videos[position]
        holder.bind(video)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class YoutubePlayerVideosViewHolder(private val binding : ItemYoutubePlayerRelativeVideosBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(video : VideoRenderer) {
            val videoInfo = "${video.longBylineText?.runs?.firstOrNull()?.text.toString()} • ${video.shortViewCountText?.simpleText?.toString()} • ${video.publishedTimeText?.simpleText.toString()}"
            binding.apply {
                videoTitle.text = video.title?.runs?.firstOrNull()?.text.toString()
                otherVideoInfo.text = videoInfo
                Glide.with(root).load(video.thumbnail?.thumbnails?.firstOrNull()?.url.toString()).into(videoImage)
                Glide.with(root).load(video.channelThumbnailSupportedRenderers?.channelThumbnailWithLinkRenderer?.thumbnail?.thumbnails?.firstOrNull()?.url.toString()).into(channelImage)
            }
        }
    }
}