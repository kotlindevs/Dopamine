package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.viewHolders.LibraryViewHolder
import com.google.android.piyush.youtube.model.Youtube

class LibraryAdapter(
    private val context: Context,
    private var youtube: Youtube?
) : RecyclerView.Adapter<LibraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        return LibraryViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_fragment_library, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return youtube?.items?.size!!
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val videos = youtube?.items?.get(position)
        Glide.with(context)
            .load(videos?.snippet?.thumbnails?.standard?.url)
            .into(holder.image)
        holder.title.text = videos?.snippet?.title
        holder.subtitle.text = videos?.snippet?.publishedAt
        holder.video.setOnClickListener {
            context.startActivity(
                Intent(context, YoutubePlayer::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("videoId",videos?.contentDetails?.videoId)
                .putExtra("channelId",videos?.snippet?.channelId)
            )
        }
    }
}