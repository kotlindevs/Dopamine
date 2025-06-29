package com.google.android.piyush.dopamine.adapters

import android.content.Intent
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
import com.google.android.piyush.database.entities.RecentlyExplored
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.databinding.ItemFragmentTrendingBinding

class RecentlyExploredAdapter(private val videos : MutableList<RecentlyExplored>?)
    : RecyclerView.Adapter<RecentlyExploredAdapter.RecentlyExploredViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentlyExploredViewHolder {
        return RecentlyExploredViewHolder(
            ItemFragmentTrendingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecentlyExploredViewHolder,
        position: Int
    ) {
        videos?.get(position)?.let {
            holder.bind(it)

        }
    }

    override fun getItemCount(): Int {
        return videos?.size ?: 0
    }

    inner class RecentlyExploredViewHolder(private val binding: ItemFragmentTrendingBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(video: RecentlyExplored){
            val videoImage = video.thumbnail
            videoImage?.let {
                if(it.isNotEmpty()){
                    binding.shimmerEffectVideoImage.apply {
                        visibility = View.VISIBLE
                        startShimmer()
                    }
                    binding.videoImage.visibility = View.VISIBLE

                    Glide.with(binding.root)
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

                binding.videoTitle.text = video.title
                binding.channelTitle.text = video.longBylineText
                binding.trendingVideo.setOnClickListener {
                    binding.root.context.startActivity(
                        Intent(binding.root.context, YoutubePlayer::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("videoId", video.videoId)
                            .putExtra("channelName", video.longBylineText)
                            .putExtra("publishedTime", video.publishedTimeText)
                            .putExtra("viewCount",video.shortViewCountText)
                            .putExtra("videoLength", video.lengthText)
                            .putExtra("channelImage", video.avatar)

                    )
                }
            }
        }
    }
}