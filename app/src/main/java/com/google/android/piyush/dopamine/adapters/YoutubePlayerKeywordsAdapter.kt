package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ItemYoutubePlayerKeywordsBinding

class YoutubePlayerKeywordsAdapter(private val keywords : List<String>?) : RecyclerView.Adapter<YoutubePlayerKeywordsAdapter.YoutubePlayerKeywordsViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YoutubePlayerKeywordsViewHolder {
        val binding = ItemYoutubePlayerKeywordsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return YoutubePlayerKeywordsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: YoutubePlayerKeywordsViewHolder,
        position: Int
    ) {
        val keyword = keywords?.get(position)
        holder.bind(keyword = keyword?: "")
    }

    override fun getItemCount(): Int {
        return keywords?.size ?: 0
    }

    inner class YoutubePlayerKeywordsViewHolder(private val binding : ItemYoutubePlayerKeywordsBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(keyword : String) {
            binding.keyword.text = keyword
        }
    }
}