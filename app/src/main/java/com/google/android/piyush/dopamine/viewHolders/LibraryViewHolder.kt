package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val video : ConstraintLayout = itemView.findViewById(R.id.trendingVideo)
    val image : ShapeableImageView = itemView.findViewById(R.id.videoImage)
    val title : MaterialTextView = itemView.findViewById(R.id.videoTitle)
    val subtitle : MaterialTextView = itemView.findViewById(R.id.channelTitle)
}