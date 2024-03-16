package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.CVPlaylist
import com.google.android.piyush.dopamine.viewHolders.CustomPlayListVHolder
import kotlin.random.Random


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
        val playlistName = playlists?.get(position)?.playListName.toString()
        val playlistDescription = playlists?.get(position)?.playListDescription

        Log.d("playlistName", playlistName)

        val getColor = Color.argb(
            255,
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )

        holder.image.background.setTint(getColor)

        holder.title1.text = getString(playlistName)
        holder.title.text = playlistName
        holder.description.text = playlistDescription
        holder.playlist.setOnClickListener {
            val intent = Intent(context, CVPlaylist::class.java)
            intent.putExtra("playlistName", playlistName)
            context.startActivity(intent)
        }
    }

    private fun getString(name: String): String {
        if(name.isNullOrEmpty()){
            return "🧿"
        }else{
            var initials = ""
            val nameParts = name.split(" ")
            for (i in 0 until minOf(nameParts.size, 2)) {
                initials += nameParts[i][0].uppercase()
            }
            return initials
        }
    }
}