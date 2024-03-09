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

class YoutubeChannelPlaylistsAdapter(
    private val context: Context,
    private val playlists: Youtube?
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
        Glide.with(context)
            .load(playListData?.snippet?.thumbnails?.default?.url)
            .into(holder.image)
        holder.text1.text = playListData?.snippet?.title
        holder.text2.text = playListData?.snippet?.description
        holder.video.setOnClickListener {
            if (playListData != null) {
                context.startActivity(
                    Intent(
                        context, YoutubeChannelPlaylistsVideos::class.java
                    ).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ).putExtra(
                        "playlistId", playListData.id
                    )
                )
            }
        }
    }
}