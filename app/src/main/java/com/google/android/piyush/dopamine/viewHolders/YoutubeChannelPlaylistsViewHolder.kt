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

    var image : ImageView = itemView.findViewById(R.id.image)
    var text1 : MaterialTextView = itemView.findViewById(R.id.text1)
    var text2 : MaterialTextView = itemView.findViewById(R.id.text2)
    var video : MaterialCardView = itemView.findViewById(R.id.video)

}