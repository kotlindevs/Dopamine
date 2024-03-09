package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.viewHolders.YoutubePlaylistsVideosViewHolder
import com.google.android.piyush.youtube.model.Youtube

class YoutubePlaylistsVideosAdapter(
    private val context: Context,
    private val playlistVideos: Youtube?
) : RecyclerView.Adapter<YoutubePlaylistsVideosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YoutubePlaylistsVideosViewHolder {
        return YoutubePlaylistsVideosViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_activity_youtube_playlists_videos, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return playlistVideos?.items?.size!!
    }

    override fun onBindViewHolder(holder: YoutubePlaylistsVideosViewHolder, position: Int) {
        val videos = playlistVideos?.items?.get(position)
        Glide.with(context)
            .load(videos?.snippet?.thumbnails?.high?.url)
            .into(holder.image)
        holder.text1.text = videos?.snippet?.title
        holder.text2.text = videos?.snippet?.publishedAt
        holder.video.setOnClickListener {
            if (videos != null) {
                context.startActivity(
                    Intent(
                        context, YoutubePlayer::class.java
                    ).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ).putExtra(
                        "videoId",videos.contentDetails?.videoId
                    ).putExtra(
                        "channelId",videos.snippet?.channelId
                    )
                )
            }
        }
    }
}