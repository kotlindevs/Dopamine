package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.viewHolders.RecentVideosViewHolder

class RecentVideosAdapter(
    private val context: Context,
    private var videos: List<EntityRecentVideos>?
) : RecyclerView.Adapter<RecentVideosViewHolder>() {

    fun setVideos(newVideos: List<EntityRecentVideos>?) {
        videos = newVideos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentVideosViewHolder {
        return RecentVideosViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recent_history, parent, false)
        )
    }

    override fun getItemCount() = videos?.size ?: 0

    override fun onBindViewHolder(holder: RecentVideosViewHolder, position: Int) {
        val videos = videos?.get(position)
        Glide.with(context)
            .load(videos?.thumbnail)
            .into(holder.image)
        holder.title.text = videos?.title
        holder.subtitle.text = videos?.timing
        holder.video.setOnClickListener {
            context.startActivity(
                Intent(context, YoutubePlayer::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("videoId",videos?.videoId)
                    .putExtra("channelId",videos?.channelId)
            )
        }
        if(holder.title.text.length < 20){
            holder.title.text = holder.title.text.toString().padEnd(20 ,'\n')
        }
        holder.videoLength.text = videos?.length
    }
}