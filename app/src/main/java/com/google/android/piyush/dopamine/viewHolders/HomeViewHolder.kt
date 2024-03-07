package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val youTubePlayerView : ShapeableImageView = itemView.findViewById(R.id.youtube_player_view)
    val imageView : ShapeableImageView = itemView.findViewById(R.id.channel_image)
    val videoTitle : MaterialTextView = itemView.findViewById(R.id.video_title)
    val channelTitle : MaterialTextView = itemView.findViewById(R.id.channel_title)
    val videoDuration : MaterialTextView = itemView.findViewById(R.id.video_duration)
    val youTubePlayer : MaterialCardView = itemView.findViewById(R.id.video_card)
}