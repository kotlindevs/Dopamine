package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.CVPlaylist
import com.google.android.piyush.dopamine.viewHolders.CustomPlayListVHolder

class CustomPlayListVAdapter(
    private val context: Context,
    private var playlists: List<CustomPlaylistView>?,
) : RecyclerView.Adapter<CustomPlayListVHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomPlayListVHolder {
        return CustomPlayListVHolder(
            LayoutInflater.from(
                parent.context
            )
                .inflate(
                    R.layout.item_frag_cust_lst_view, parent, false
                )
        )
    }

    override fun getItemCount(): Int {
        return playlists?.size!!
    }

    override fun onBindViewHolder(holder: CustomPlayListVHolder, position: Int) {
        holder.title.text = playlists?.get(position)?.playListName
        holder.description.text = playlists?.get(position)?.playListDescription
        holder.playlist.setOnClickListener {
            val intent = Intent(context, CVPlaylist::class.java)
            intent.putExtra("playlistName", playlists?.get(position)?.playListName)
            context.startActivity(intent)
        }
    }
}