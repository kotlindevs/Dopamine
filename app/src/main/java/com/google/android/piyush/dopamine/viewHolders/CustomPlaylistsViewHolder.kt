package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class CustomPlaylistsViewHolder(
    itemView : View
) : RecyclerView.ViewHolder(itemView) {

    val title: MaterialTextView = itemView.findViewById(R.id.title)
    val description: MaterialTextView = itemView.findViewById(R.id.description)
    val selectedPlaylistItem : MaterialCheckBox = itemView.findViewById(R.id.selectedPlaylistItem)
}