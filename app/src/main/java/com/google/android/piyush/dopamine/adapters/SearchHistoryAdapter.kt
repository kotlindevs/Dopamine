package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.entities.EntityVideoSearch
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.viewHolders.SearchHistoryViewHolder

class SearchHistoryAdapter(
    private val searchHistoryItem : List<EntityVideoSearch>?
) : RecyclerView.Adapter<SearchHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_fragment_search, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return searchHistoryItem?.size!!
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.searchHistoryItem.text = searchHistoryItem?.get(position)?.search
    }
}