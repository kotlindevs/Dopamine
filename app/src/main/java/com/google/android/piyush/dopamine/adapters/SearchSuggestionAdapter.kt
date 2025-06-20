package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.dopamine.databinding.ItemSearchSuggestionsBinding

class SearchSuggestionAdapter(private val suggestions : List<String>)
    : RecyclerView.Adapter<SearchSuggestionAdapter.SearchSuggestionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchSuggestionViewHolder {
        val binding = ItemSearchSuggestionsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchSuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchSuggestionViewHolder,
        position: Int
    ) {
        val suggestion = suggestions[position]
        holder.bind(suggestion)
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    inner class SearchSuggestionViewHolder(private val binding : ItemSearchSuggestionsBinding)
            : RecyclerView.ViewHolder(binding.root) {
            fun bind(suggestion : String) {
                binding.searchSuggestionText.text = suggestion
            }
        }
}