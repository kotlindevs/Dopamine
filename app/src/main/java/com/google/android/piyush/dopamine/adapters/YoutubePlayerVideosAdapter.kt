package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.databinding.ItemYoutubePlayerRelativeVideosBinding
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
            val channelName = video.longBylineText?.runs?.firstOrNull()?.text.toString()
            val viewCount = video.shortViewCountText?.simpleText?.toString()
            val publishedTime = video.publishedTimeText?.simpleText.toString()
            val channelImage = video.channelThumbnailSupportedRenderers?.channelThumbnailWithLinkRenderer?.thumbnail?.thumbnails?.firstOrNull()?.url.toString()
            val videoImage = video.thumbnail?.thumbnails?.firstOrNull()?.url.toString()

            val videoInfo = "$channelName • $viewCount • $publishedTime"
            binding.apply {
                Glide.with(root).load(videoImage).into(this.videoImage)
                Glide.with(root).load(channelImage).into(this.channelImage)
                videoTitle.text = video.title?.runs?.firstOrNull()?.text.toString()
                otherVideoInfo.text = videoInfo
            }
        }
    }
}