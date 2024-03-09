package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubeChannel
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.viewHolders.SearchViewHolder
import com.google.android.piyush.youtube.model.Youtube

class SearchAdapter(
    private val context: Context,
    private val youtube: Youtube?
)  : RecyclerView.Adapter<SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_fragment_search_videos, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return youtube?.items?.size!!
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val video = youtube?.items?.get(position)
        Glide.with(context)
            .load(video?.snippet?.thumbnails?.high?.url)
            .into(holder.image)
        holder.text1.text = video?.snippet?.title
        holder.text2.text = video?.snippet?.channelTitle
        holder.channelview.setOnClickListener {
            Toast.makeText(context, video?.kind, Toast.LENGTH_SHORT).show()
            context.startActivity(
                Intent(
                    context,
                    YoutubeChannel::class.java
                )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("channelId", video?.snippet?.channelId)
            )
        }
        holder.video.setOnClickListener {
            Toast.makeText(context, video?.kind, Toast.LENGTH_SHORT).show()
            context.startActivity(
                Intent(
                    context,
                    YoutubePlayer::class.java
                )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("videoId", video?.contentDetails?.videoId)
            )
        }
    }
}