package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.dopamine.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class ShortsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val shortsPlayer : YouTubePlayerView = itemView.findViewById(R.id.shortsPlayer)
}