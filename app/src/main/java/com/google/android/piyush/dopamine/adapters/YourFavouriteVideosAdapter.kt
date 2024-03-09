package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.viewHolders.YourFavouriteVideosViewHolder

class YourFavouriteVideosAdapter(
    private val context: Context,
    private val videos: List<EntityFavouritePlaylist>?
) : RecyclerView.Adapter<YourFavouriteVideosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YourFavouriteVideosViewHolder {
        return YourFavouriteVideosViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_fragment_library_your_favourite_videos, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return videos?.size!!
    }

    override fun onBindViewHolder(holder: YourFavouriteVideosViewHolder, position: Int) {
        val favouriteVideo = videos?.get(position)
        holder.title.text = favouriteVideo?.title
        holder.customName.text = favouriteVideo?.customName
        Glide.with(context).load(favouriteVideo?.thumbnail).into(holder.image)
        holder.videoCard.setOnClickListener {
            context.startActivity(
                Intent(context, YoutubePlayer::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("videoId",favouriteVideo?.videoId)
                    .putExtra("channelId",favouriteVideo?.channelId)
            )
        }
    }
}