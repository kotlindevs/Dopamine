package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class CustomPlayListVHolder (
    itemView : View
) : RecyclerView.ViewHolder(itemView) {
    val image : ShapeableImageView = itemView.findViewById(R.id.playlistImage)
    val title: MaterialTextView = itemView.findViewById(R.id.playlistText)
    val description: MaterialTextView = itemView.findViewById(R.id.playlistDescription)
    val playlist: MaterialCardView = itemView.findViewById(R.id.playlist)
    val playlistIc : ShapeableImageView = itemView.findViewById(R.id.playlistIc)
    val playlistTxt : MaterialTextView = itemView.findViewById(R.id.playlistTxt)

}