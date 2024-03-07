package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.viewHolders.ShortsViewHolder
import com.google.android.piyush.youtube.model.Shorts
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlin.random.Random

class ShortsAdapter(
    private val shorts: List<Shorts>?
) : RecyclerView.Adapter<ShortsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortsViewHolder {
        return ShortsViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_fragment_shorts, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return shorts?.size!!
    }

    override fun onBindViewHolder(holder: ShortsViewHolder, position: Int) {
        var uniqueRandomPosition = Random.nextInt(0, shorts?.size!!)
        while (uniqueRandomPosition == position) {
            uniqueRandomPosition = Random.nextInt(0,shorts.size)
        }
        val short = shorts[uniqueRandomPosition].videoId
        holder.shortsPlayer.getYouTubePlayerWhenReady(
            object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(short!!, 1f)
                }
            }
        )
    }
}