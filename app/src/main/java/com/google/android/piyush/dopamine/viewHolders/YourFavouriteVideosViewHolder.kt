package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class YourFavouriteVideosViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    val image : ShapeableImageView = itemView.findViewById(R.id.image)
    val title : MaterialTextView = itemView.findViewById(R.id.text)
    val customName : MaterialTextView = itemView.findViewById(R.id.text1)
    val videoCard : MaterialCardView = itemView.findViewById(R.id.video_card)

}