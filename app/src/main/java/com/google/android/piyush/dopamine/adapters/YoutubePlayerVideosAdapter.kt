package com.google.android.piyush.dopamine.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
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
                Glide.with(binding.root)
                    .load(videoImage)
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.apply {
                                /*
                                this.shimmerEffectVideoImage.visibility = View.GONE
                                this.shimmerEffectVideoImage.stopShimmer()
                                this.videoImage.visibility = View.VISIBLE */
                                shimmerEffectVideoImage.apply {
                                    this.visibility = View.VISIBLE
                                    this.startShimmer()
                                }
                                binding.videoImage.visibility = View.GONE
                            }
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.apply {
                                this.shimmerEffectVideoImage.visibility = View.GONE
                                this.shimmerEffectVideoImage.stopShimmer()
                                this.videoImage.visibility = View.VISIBLE
                            }
                            return true
                        }
                    })
                    .into(this.videoImage)
            }
        }
    }
}