package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubeChannelPlaylistsVideos
import com.google.android.piyush.dopamine.viewHolders.YoutubeChannelPlaylistsViewHolder
import com.google.android.piyush.youtube.model.SearchTube
import com.google.android.piyush.youtube.model.Youtube
import com.google.android.piyush.youtube.model.channelPlaylists.ChannelPlaylists

class YoutubeChannelPlaylistsAdapter(
    private val context: Context,
    private val playlists: ChannelPlaylists?
) : RecyclerView.Adapter<YoutubeChannelPlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YoutubeChannelPlaylistsViewHolder {
        return YoutubeChannelPlaylistsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_activity_youtube_channel_playlists, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return playlists?.items?.size!!
    }

    override fun onBindViewHolder(holder: YoutubeChannelPlaylistsViewHolder, position: Int) {
        val playListData = playlists?.items?.get(position)
        val playlistImage = playListData?.snippet?.thumbnails?.default?.url
        val playlistTitle = playListData?.snippet?.title
        val playlistChannelTitle = playListData?.snippet?.channelTitle

        Glide.with(context).load(playlistImage).into(holder.playlistImage)

        holder.playlistTitle.text = playlistTitle
        holder.playlistChannelTitle.text = playlistChannelTitle
        holder.playlist.setOnClickListener {
            if (!playlists?.items.isNullOrEmpty()) {
                context.startActivity(
                    Intent(
                        context, YoutubeChannelPlaylistsVideos::class.java
                    ).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ).putExtra(
                        "playlistId", playListData?.id
                    )
                )
            }
        }
    }
}