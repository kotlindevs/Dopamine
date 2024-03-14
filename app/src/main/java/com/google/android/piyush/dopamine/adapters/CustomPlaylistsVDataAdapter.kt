package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.database.model.CustomPlaylists
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.viewHolders.CustomPlaylistsVDataHolder
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CustomPlaylistsVDataAdapter(
    private val playlists: List<CustomPlaylists>,
    private val context: Context
) : RecyclerView.Adapter<CustomPlaylistsVDataHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomPlaylistsVDataHolder {
        return CustomPlaylistsVDataHolder(
            LayoutInflater.from(context).inflate(R.layout.item_fragment_home, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CustomPlaylistsVDataHolder, position: Int) {
        val publishedTime = formatDuration(
            ChronoUnit.SECONDS.between(
                LocalDateTime.parse(
                    playlists[position].publishedAt, DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now()
            )
        )
        val publishedViews = viewsCount(
            playlists[position].viewCount!!.toInt()
        )

        val channelTitle = "${
            playlists[position].channelTitle
        } • $publishedViews • $publishedTime"

        holder.videoTitle.text = playlists[position].title

        holder.channelTitle.text = playlists[position].customName

        Glide.with(context)
            .load(playlists[position].thumbnail)
            .into(holder.imageView)

        Glide.with(context)
            .load(playlists[position].thumbnail)
            .into(holder.youTubePlayerView)

        holder.youTubePlayer.setOnClickListener {
            if(NetworkUtilities.isNetworkAvailable(context)) {
                context.startActivity(
                    Intent(context, YoutubePlayer::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("videoId", playlists[position].videoId)
                        .putExtra("channelId", playlists[position].channelId)
                )
            }else{
                NetworkUtilities.showNetworkError(context)
            }
        }
    }

    private fun viewsCount(views: Int): String {
        return when {
            views >= 1000000000 -> {
                val formattedViews = views / 1000000000
                "${formattedViews}B views"
            }
            views >= 1000000 -> {
                val formattedViews = views / 1000000
                "${formattedViews}M views"
            }
            views >= 1000 -> {
                val formattedViews = views / 1000
                "${formattedViews}K views"
            }
            else -> {
                "$views views"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    private fun formatDuration(seconds: Long): String {
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val secondsRemaining = seconds % 60

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "$secondsRemaining seconds"
        }
    }
}