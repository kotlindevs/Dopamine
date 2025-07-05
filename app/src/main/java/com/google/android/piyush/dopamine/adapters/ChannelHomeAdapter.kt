package com.google.android.piyush.dopamine.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.dopamine.databinding.ChannelHomeHeaderBinding
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.youtube.model.ChannelHomeContent
import com.google.android.piyush.youtube.model.ReelShelfRenderer

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

    @Suppress("DEPRECATION")
    inner class ChannelHomeViewHolder(private val binding : ChannelHomeHeaderBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(content : ChannelHomeContent){
            content.header.title.let { title ->
                binding.playlistTitle.visibility = View.GONE
                if(!title.isNullOrEmpty()){
                    binding.playlistTitle.visibility = View.VISIBLE
                    binding.playlistTitle.text = title
                }
            }

            content.header.subtitle.let { subtitle ->
                binding.playlistSubTitle.visibility = View.GONE
                if(!subtitle.isNullOrEmpty()){
                    binding.playlistSubTitle.visibility = View.VISIBLE
                    binding.playlistSubTitle.text = subtitle
                }
            }

            content.videos.let { videos ->
                binding.playlistVideos.visibility = View.GONE
                if(videos != null){
                    binding.playlistVideos.visibility = View.VISIBLE
                    binding.playlistVideos.apply {
                        layoutManager = LinearLayoutManager(
                            binding.root.context,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = ChannelVideosRenderer(
                            videos
                        )
                    }
                }
            }

            content.videos?.size?.let { videoCount ->
                binding.playlistChannels.visibility = View.GONE
                binding.playlistReels.visibility = View.GONE
                content.channels?.size?.let { channelCount ->
                    if(videoCount < 1 && channelCount > 0){
                        binding.playlistChannels.visibility = View.VISIBLE
                        binding.playlistChannels.apply {
                            layoutManager = LinearLayoutManager(
                                binding.root.context,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            adapter = ChannelProfileRenderer(
                                content.channels
                            )
                        }
                    }else if (videoCount < 1){
                        binding.playlistReels.visibility = View.VISIBLE
                        binding.playlistReels.apply {
                            layoutManager = LinearLayoutManager(
                                binding.root.context,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            adapter = YoutubePlayerShortsAdapter(
                                content.reels!!
                            )
                        }
                    }
                }
            }

            Log.d("Channel Title => [$position] => ", content.header.title ?: "")
            Log.d("Channel Subtitle => [$position] => ", content.header.subtitle ?: "")
            Log.d("Channel Videos => [$position] => ", content.videos?.size.toString())
            Log.d("Channel Reels => [$position] => ", content.reels?.size.toString())
            Log.d("Channel Channels => [$position] => ", content.channels?.size.toString())
            Log.d("Channel Divider => [$position] => ", "==================================================================================================================================================>")
        }
    }
}