package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.databinding.ChannelProfileShortViewRendererBinding
import com.google.android.piyush.youtube.model.GridChannelRenderer

class ChannelProfileRenderer(
    private val channels : MutableList<GridChannelRenderer>?
) : RecyclerView.Adapter<ChannelProfileRenderer.ChannelProfileRendererViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelProfileRendererViewHolder {
        return ChannelProfileRendererViewHolder(
            ChannelProfileShortViewRendererBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ChannelProfileRendererViewHolder,
        position: Int
    ) {
        val channel = channels?.get(position)
        channel?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return channels?.size ?: 0
    }

    inner class ChannelProfileRendererViewHolder(private val binding: ChannelProfileShortViewRendererBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(channel : GridChannelRenderer){
                val channelImage = channel.thumbnail?.thumbnails?.let { image ->
                    image.getOrNull(1)?.url ?: image.firstOrNull()?.url
                }
                val channelTitle = channel.title?.simpleText
                val channelSubtitle = channel.subscriberCountText?.simpleText

                Glide.with(binding.root.context)
                    .load("https:$channelImage")
                    .into(binding.channelProfileImage)

                binding.channelProfileTitle.text = channelTitle
                binding.channelProfileSubtitle.text = channelSubtitle
            }
        }
}