package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.dopamine.databinding.ChannelHomeHeaderBinding
import com.google.android.piyush.youtube.model.ChannelHomeContent

class ChannelHomeAdapter(
    private val channelContent : MutableList<ChannelHomeContent>?
) : RecyclerView.Adapter<ChannelHomeAdapter.ChannelHomeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelHomeViewHolder {
        return ChannelHomeViewHolder(
            ChannelHomeHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ChannelHomeViewHolder,
        position: Int
    ) {
        val content = channelContent?.get(position)
        content?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return channelContent?.size ?: 0
    }

    inner class ChannelHomeViewHolder(private val binding : ChannelHomeHeaderBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(content : ChannelHomeContent){
            binding.apply {
                playlistTitle.text = content.header.title
                playlistSubTitle.text = content.header.subtitle
                playlistVideos.apply {
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = ChannelVideosRenderer(
                        content.items
                    )
                }
            }
        }
    }
}