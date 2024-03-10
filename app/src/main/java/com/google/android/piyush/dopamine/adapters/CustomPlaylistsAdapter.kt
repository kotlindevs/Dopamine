package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.viewHolders.CustomPlaylistsViewHolder

class CustomPlaylistsAdapter(
    private var playlists: List<CustomPlaylistView>?,
) : RecyclerView.Adapter<CustomPlaylistsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomPlaylistsViewHolder {
        return CustomPlaylistsViewHolder(
            LayoutInflater.from(
                parent.context
            )
                .inflate(
                    R.layout.item_custom_playlists_view, parent, false
                )
        )
    }

    fun addPlaylists(playlists: List<CustomPlaylistView>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return playlists?.size!!
    }

    override fun onBindViewHolder(holder: CustomPlaylistsViewHolder, position: Int) {
        holder.title.text = playlists?.get(position)?.playListName
        holder.description.text = playlists?.get(position)?.playListDescription
    }
}