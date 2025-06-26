package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.entities.RecentSearch
import com.google.android.piyush.dopamine.databinding.ItemRecentSearchBinding

class ExploreRecentSearchAdapter(
    private val recentSearch : MutableList<RecentSearch>,
    private val selectedSearch : (RecentSearch) -> Unit,
    private val deleteSearch : (RecentSearch) -> Unit
)
    : RecyclerView.Adapter<ExploreRecentSearchAdapter.ExploreRecentSearchViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExploreRecentSearchViewHolder {
        return ExploreRecentSearchViewHolder(
            ItemRecentSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ExploreRecentSearchViewHolder,
        position: Int
    ) {
        val search = recentSearch[position]
        holder.bind(search)
    }

    override fun getItemCount(): Int {
        return recentSearch.size
    }

    inner class ExploreRecentSearchViewHolder(private val binding: ItemRecentSearchBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(recentSearch: RecentSearch){
            val search = recentSearch.searchText
            search.let { text ->
                binding.RecentSearchText.text = text
            }
            binding.selectedRecentSearchText.setOnClickListener {
                selectedSearch(recentSearch)
            }
            binding.deleteRecentSearchText.setOnClickListener {
                deleteSearch(recentSearch)
            }
        }
    }
}