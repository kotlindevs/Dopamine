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
import com.google.android.piyush.dopamine.databinding.ItemRowViewVideoRendererBinding
import com.google.android.piyush.youtube.model.GridVideoRenderer

class ChannelVideosRenderer(
    private val videos : MutableList<GridVideoRenderer>?
) : RecyclerView.Adapter<ChannelVideosRenderer.ChannelVideosViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelVideosViewHolder {
        return ChannelVideosViewHolder(
            ItemRowViewVideoRendererBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ChannelVideosViewHolder,
        position: Int
    ) {
        val video = videos?.get(position)
        video?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return videos?.size ?: 0
    }

    inner class ChannelVideosViewHolder(private val binding : ItemRowViewVideoRendererBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(video : GridVideoRenderer){
            val videoImage = video.thumbnail?.thumbnails?.let { image ->
                image.getOrNull(1)?.url ?: image.firstOrNull()?.url
            }
            val videoTitle = video.title?.simpleText.toString()
            val channelTitle = video.shortBylineText?.runs?.firstOrNull()?.text.toString()

            videoImage?.let {
                if(it.isNotEmpty()){
                    binding.shimmerEffectVideoImage.apply {
                        visibility = View.VISIBLE
                        startShimmer()
                    }
                    binding.videoImage.visibility = View.VISIBLE

                    Glide.with(binding.root.context)
                        .load(it)
                        .listener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.shimmerEffectVideoImage.apply {
                                    stopShimmer()
                                    visibility = View.GONE
                                }
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
                                binding.shimmerEffectVideoImage.apply {
                                    stopShimmer()
                                    visibility = View.GONE
                                }
                                binding.videoImage.visibility = View.VISIBLE
                                return false
                            }
                        })
                        .into(binding.videoImage)
                }
            }
            binding.videoTitle.text = videoTitle
            binding.channelTitle.text = channelTitle
        }
    }
}