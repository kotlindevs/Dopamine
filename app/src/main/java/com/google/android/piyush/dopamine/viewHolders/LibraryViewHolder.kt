package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val video : MaterialCardView = itemView.findViewById(R.id.video)
    val image : ShapeableImageView = itemView.findViewById(R.id.image)
    val title : MaterialTextView = itemView.findViewById(R.id.text1)
    val subtitle : MaterialTextView = itemView.findViewById(R.id.text2)
}