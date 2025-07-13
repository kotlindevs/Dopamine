package com.google.android.piyush.dopamine.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R

class SearchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val searchHistoryItem : MaterialTextView = itemView.findViewById(R.id.textView)
}