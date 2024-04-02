package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class CustomPlaylistsVDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val youTubePlayerView : ShapeableImageView = itemView.findViewById(R.id.videoImage)
    val videoTitle : MaterialTextView = itemView.findViewById(R.id.videoTitle)
    val channelTitle : MaterialTextView = itemView.findViewById(R.id.channelTitle)
    val videoDuration : MaterialTextView = itemView.findViewById(R.id.videoDuration)
    val youTubePlayer : MaterialCardView = itemView.findViewById(R.id.video)
}