package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.viewHolders.LibraryViewHolder
import com.google.android.piyush.youtube.model.BrowseResponse.Contents.TwoColumnBrowseResultsRenderer.Tab.TabRenderer.Content.SectionListRenderer.Contents.ItemSectionRenderer.Contents.ShelfRenderer.Content.ExpandedShelfContentsRenderer.Item.VideoRenderer

class LibraryAdapter(
    private val context: Context,
    private var videos: List<VideoRenderer>?
) : RecyclerView.Adapter<LibraryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibraryViewHolder {
        return LibraryViewHolder(
            LayoutInflater.from(
                context
            ).inflate(
                R.layout.item_fragment_trending,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: LibraryViewHolder,
        position: Int
    ) {
        val video = videos?.get(position)
        Glide.with(context)
            .load(video?.thumbnail?.thumbnails?.get(0)?.url)
            .into(holder.image)
        holder.title.text = video?.title?.runs?.get(0)?.text
        holder.subtitle.text = video?.longBylineText?.runs?.get(0)?.text
    }

    override fun getItemCount(): Int {
        return videos?.size ?: 0
    }
}