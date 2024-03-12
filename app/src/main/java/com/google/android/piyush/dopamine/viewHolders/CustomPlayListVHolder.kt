package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class CustomPlayListVHolder (
    itemView : View
) : RecyclerView.ViewHolder(itemView) {
    val title: MaterialTextView = itemView.findViewById(R.id.playlist_name)
    val description: MaterialTextView = itemView.findViewById(R.id.playlist_description)
    val playlist: MaterialCardView = itemView.findViewById(R.id.playlist)
}