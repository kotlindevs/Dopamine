package com.google.android.piyush.dopamine.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.piyush.dopamine.databinding.ItemYoutubePlayerRelativeVideosBinding
import com.google.android.piyush.youtube.model.VideoRenderer

class YoutubePlayerVideosAdapter(
    private val videos : MutableList<VideoRenderer>,
    private val onVideoClick : (VideoRenderer) -> Unit,
    private val onChannelClick : (VideoRenderer) -> Unit
) :
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
        holder.bind(video, onVideoClick, onChannelClick)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class YoutubePlayerVideosViewHolder(private val binding : ItemYoutubePlayerRelativeVideosBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            video : VideoRenderer,
            onVideoClick : (VideoRenderer) -> Unit,
            onChannelClick : (VideoRenderer) -> Unit,
        ) {
            val channelName = video.longBylineText?.runs?.firstOrNull()?.text.toString()
            val viewCount = video.shortViewCountText?.simpleText?.toString()
            val publishedTime = video.publishedTimeText?.simpleText.toString()
            val channelImage =
                video.channelThumbnailSupportedRenderers?.channelThumbnailWithLinkRenderer?.thumbnail?.thumbnails?.firstOrNull()?.url.toString()
            val videoImage = video.thumbnail?.thumbnails?.let {
                it.getOrNull(1)?.url ?: it.firstOrNull()?.url
            }
            val videoTitle = video.title?.runs?.firstOrNull()?.text.toString()
            val videoInfo = "$channelName • $viewCount • $publishedTime"

            binding.apply {
                videoImage?.let {
                    if(it.isNotEmpty()) {
                        shimmerEffectVideoImage.visibility = View.VISIBLE
                        shimmerEffectVideoImage.startShimmer()
                        this.videoImage.visibility = View.VISIBLE
                        this.videoImage.setOnClickListener {
                            onVideoClick(video)
                        }
                        Glide.with(binding.root.context).load(it)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable?>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.shimmerEffectVideoImage.visibility = View.GONE
                                    binding.shimmerEffectVideoImage.stopShimmer()
                                    binding.videoImage.visibility = View.VISIBLE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable?>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.shimmerEffectVideoImage.visibility = View.GONE
                                    binding.shimmerEffectVideoImage.stopShimmer()
                                    binding.videoImage.visibility = View.VISIBLE
                                    return false
                                }

                            })
                            .into(this.videoImage)
                    }
                }

                channelImage.let {
                    if(it.isNotEmpty()){
                        shimmerEffectChannelImage.visibility = View.VISIBLE
                        shimmerEffectChannelImage.startShimmer()
                        this.channelImage.visibility = View.VISIBLE
                        this.channelImage.setOnClickListener {
                            onChannelClick(video)
                        }
                        Glide.with(binding.root.context).load(it)
                            .listener(object : RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable?>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.shimmerEffectChannelImage.visibility = View.GONE
                                    binding.shimmerEffectChannelImage.stopShimmer()
                                    binding.channelImage.visibility = View.VISIBLE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable?>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.shimmerEffectChannelImage.visibility = View.GONE
                                    binding.shimmerEffectChannelImage.stopShimmer()
                                    binding.channelImage.visibility = View.VISIBLE
                                    return false
                                }

                            })
                            .into(this.channelImage)

                    }else{
                        binding.shimmerEffectChannelImage.visibility = View.GONE
                        binding.shimmerEffectChannelImage.stopShimmer()
                        binding.channelImage.visibility = View.VISIBLE
                    }
                }

                videoInfo.let {
                    shimmerEffectChannelInfo.visibility = View.VISIBLE
                    shimmerEffectChannelInfo.startShimmer()
                    if(it.isNotEmpty() || videoTitle.isNotEmpty()) {
                        shimmerEffectChannelInfo.visibility = View.GONE
                        shimmerEffectChannelInfo.stopShimmer()
                        this.videoTitle.visibility = View.VISIBLE
                        this.videoTitle.text = videoTitle
                        this.otherVideoInfo.visibility = View.VISIBLE
                        this.otherVideoInfo.text = it
                        this.otherVideoInfo.setOnClickListener {
                            onChannelClick(video)
                        }
                    }else{
                        binding.shimmerEffectChannelInfo.visibility = View.GONE
                        binding.shimmerEffectChannelInfo.stopShimmer()
                        binding.otherVideoInfo.visibility = View.GONE
                        binding.videoTitle.visibility = View.GONE
                    }
                }
            }
        }
    }
}