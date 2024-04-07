package com.google.android.piyush.dopamine.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.dopamine.viewHolders.HomeViewHolder
import com.google.android.piyush.youtube.model.Item
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.android.piyush.youtube.viewModels.MoreViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HomeAdapter(
    private val context: Context,
    private var youtube : List<Item>?
) : RecyclerView.Adapter<HomeViewHolder>() {

    private fun addVideos(videos: List<Item>?) {
        if(youtube?.containsAll(videos!!)!!.equals(false)){
            youtube.let {
                youtube = it?.plus(videos!!)
            }
        }
        notifyItemRangeInserted(
            youtube?.size ?: 0,
            videos?.size ?: 0
        )
        Log.d(TAG, "Success: ${this.itemCount}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_fragment_home, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return youtube?.size!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val publishedTime = formatDuration(
            ChronoUnit.SECONDS.between(
                LocalDateTime.parse(
                    youtube?.get(position)?.snippet!!.publishedAt, DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now()
            )
        )
        val publishedViews = viewsCount(
            youtube?.get(position)?.statistics!!.viewCount!!.toInt()
        )

        val channelTitle = "${
            youtube?.get(position)?.snippet!!.channelTitle} • $publishedViews • $publishedTime"

        holder.videoTitle.text = youtube?.get(position)?.snippet!!.title

        holder.channelTitle.text = channelTitle

        Glide.with(context)
            .load(youtube?.get(position)?.snippet!!.thumbnails!!.default!!.url)
            .into(holder.imageView)

        Glide.with(context)
            .load(youtube?.get(position)?.snippet!!.thumbnails!!.high!!.url)
            .into(holder.youTubePlayerView)

        holder.videoDuration.text = formatDuration(
            Duration.parse(youtube?.get(position)?.contentDetails!!.duration!!)
        )

        holder.youTubePlayer.setOnClickListener {
            if(NetworkUtilities.isNetworkAvailable(context)) {
                context.startActivity(
                    Intent(context, YoutubePlayer::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("videoId", youtube?.get(position)?.id)
                        .putExtra("channelId", youtube?.get(position)?.snippet!!.channelId)
                )
                Log.d("FragmentHome", "videoId: ${youtube?.get(position)?.id}")
                Log.d("FragmentHome", "channelId: ${youtube?.get(position)?.snippet!!.channelId}")
            }else{
                NetworkUtilities.showNetworkError(context)
            }
            Log.d("FragmentHome", "videoData: ${youtube?.get(position)}")
        }

        val moreViewModel = MoreViewModel()
        val regionCode = dopamineSharedPreferences(context).getString("region", "").toString()
        val pageToken = dopamineSharedPreferences(context).getString("pageToken", "").toString()
        val totalPage = dopamineSharedPreferences(context).getInt("totalPages", 0)

        for(i in 0 until totalPage) {
            if (position.equals(youtube?.size?.minus(1))) {
                Log.d(TAG, "regionCode: $regionCode || pageToken: $pageToken")
                if(pageToken.isNotEmpty()) {
                    moreViewModel.loadVideos(
                        regionCode = regionCode,
                        pageToken = pageToken
                    )
                    moreViewModel.video.observeForever {
                        if (it is YoutubeResource.Success) {
                            addVideos(it.data.items)
                            dopamineSharedPreferences(context).edit()
                                .putString("pageToken", it.data.nextPageToken).apply()
                        }
                    }
                }
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

class CustomGridAdapter(
    private val context: Context,
    private var youtube : MutableList<Item>?
) : BaseAdapter() {

    override fun getCount(): Int = youtube?.size!!

    override fun getItem(position: Int): Item? = youtube?.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_frag_list_adapter, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val publishedTime = formatDuration(
            ChronoUnit.SECONDS.between(
                LocalDateTime.parse(
                    youtube?.get(position)?.snippet!!.publishedAt, DateTimeFormatter.ISO_DATE_TIME),
                LocalDateTime.now()
            )
        )
        val publishedViews = viewsCount(
            youtube?.get(position)?.statistics!!.viewCount!!.toInt()
        )

        val channelTitle = "${
            youtube?.get(position)?.snippet!!.channelTitle} • $publishedViews • $publishedTime"

        viewHolder.videoTitle.text = youtube?.get(position)?.snippet!!.title

        viewHolder.channelTitle.text = channelTitle

        Glide.with(context)
            .load(youtube?.get(position)?.snippet!!.thumbnails!!.high!!.url)
            .into(viewHolder.youTubePlayerView)

        viewHolder.videoDuration.text = formatDuration(
            Duration.parse(youtube?.get(position)?.contentDetails!!.duration!!)
        )

        viewHolder.youTubePlayer.setOnClickListener {
            if(NetworkUtilities.isNetworkAvailable(context)) {
                context.startActivity(
                    Intent(context, YoutubePlayer::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("videoId", youtube?.get(position)?.id)
                        .putExtra("channelId", youtube?.get(position)?.snippet!!.channelId)
                )
                Log.d("FragmentHome", "videoId: ${youtube?.get(position)?.id}")
                Log.d("FragmentHome", "channelId: ${youtube?.get(position)?.snippet!!.channelId}")
            }else{
                NetworkUtilities.showNetworkError(context)
            }
            Log.d("FragmentHome", "videoData: ${youtube?.get(position)}")
        }

        val moreViewModel = MoreViewModel()
        val regionCode = dopamineSharedPreferences(context).getString("region", "").toString()
        val pageToken = dopamineSharedPreferences(context).getString("pageToken", "").toString()
        val totalPage = dopamineSharedPreferences(context).getInt("totalPages", 0)

        for(i in 0 until totalPage) {
            if (position.equals(youtube?.size?.minus(1))) {
                Log.d(TAG, "regionCode: $regionCode || pageToken: $pageToken")
                if(pageToken.isNotEmpty()) {
                    moreViewModel.loadVideos(
                        regionCode = regionCode,
                        pageToken = pageToken
                    )
                    moreViewModel.video.observeForever {
                        if (it is YoutubeResource.Success) {
                            addVideos(it.data.items?.toMutableList())
                            dopamineSharedPreferences(context).edit()
                                .putString("pageToken", it.data.nextPageToken).apply()
                        }
                    }
                }
            }
        }
        return view
    }

    private fun addVideos(videos: MutableList<Item>?) {
        if(youtube?.containsAll(videos!!)!!.equals(false)){
            youtube.let {
                youtube = it?.apply {
                    if (videos != null) {
                        addAll(videos)
                    }
                }
            }
        }
        notifyDataSetChanged()
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

private class ViewHolder(view: View) {
    val youTubePlayerView : ShapeableImageView = view.findViewById(R.id.youtube_player_view)
    val videoTitle : MaterialTextView = view.findViewById(R.id.video_title)
    val channelTitle : MaterialTextView = view.findViewById(R.id.channel_title)
    val videoDuration : MaterialTextView = view.findViewById(R.id.video_duration)
    val youTubePlayer : MaterialCardView = view.findViewById(R.id.video_card)
}