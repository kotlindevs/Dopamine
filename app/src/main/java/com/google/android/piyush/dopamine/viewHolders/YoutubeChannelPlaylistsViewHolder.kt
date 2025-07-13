package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class YoutubeChannelPlaylistsViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    var playlistImage : ImageView = itemView.findViewById(R.id.playlistImage)
    var playlistTitle : MaterialTextView = itemView.findViewById(R.id.playlistTitle)
    var playlistChannelTitle : MaterialTextView = itemView.findViewById(R.id.playlistChannelTitle)
    var playlist : MaterialCardView = itemView.findViewById(R.id.playlist)

}