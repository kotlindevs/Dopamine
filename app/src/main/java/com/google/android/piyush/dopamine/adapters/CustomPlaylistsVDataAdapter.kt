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
) : RecyclerView.Adapter<CustomPlaylistsVDataHolder>() {
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
        val playlist = playlists[position]

        // Safely parse the published time, handling potential null or empty strings
        val publishedTime = parseDate(playlist.publishedAt)?.let {
            formatDuration(ChronoUnit.SECONDS.between(it, LocalDateTime.now()))
        } ?: ""

        val publishedViews = playlist.viewCount

        val channelTitle = "${playlist.channelTitle} • $publishedViews • $publishedTime"

        holder.videoTitle.text = playlist.title
        holder.channelTitle.text = channelTitle

        // Safely parse the duration, handling potential empty strings
        val videoDuration = try {
            formatDuration(Duration.parse(playlist.duration))
        } catch (e: Exception) {
            ""
        }

        holder.videoDuration.text = videoDuration

        Glide.with(context)
            .load(playlist.thumbnail)
            .into(holder.imageView)

        Glide.with(context)
            .load(playlist.thumbnail)
            .into(holder.youTubePlayerView)

        holder.youTubePlayer.setOnClickListener {
            if (NetworkUtilities.isNetworkAvailable(context)) {
                context.startActivity(
                    Intent(context, YoutubePlayer::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("videoId", playlist.videoId)
                        putExtra("channelId", playlist.channelId)
                    }
                )
            } else {
                NetworkUtilities.showNetworkError(context)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(dateString: String?): LocalDateTime? {
        if (dateString.isNullOrEmpty()) {
            return null
        }
        return try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception){
            null
        }
    }
}