package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.viewModel.DatabaseViewModel
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
                    R.layout.item_fragment_user, parent, false
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
        holder.title.text = playlistName
        holder.description.text = playlistDescription
        holder.playlist.setOnClickListener {
            val intent = Intent(context, CVPlaylist::class.java)
            intent.putExtra("playlistName", playlistName)
            context.startActivity(intent)
        }
        if(holder.title.text.length > 20){
            holder.title.text =  holder.title.text.toString().substring(0,20)
        }
        if(holder.description.text.length > 20){
            holder.description.text =  holder.description.text.toString().substring(0,20)
        }
        val database = DatabaseViewModel(context)
        if(database.getPlaylistData(playlistName).isEmpty()){
            holder.apply {
                playlistIc.visibility = View.VISIBLE
                playlistTxt.visibility = View.VISIBLE
            }
        }else{
            holder.apply {
                playlistIc.visibility = View.GONE
                playlistTxt.visibility = View.GONE
                Glide.with(context).load(database.getPlaylistData(playlistName)[0].thumbnail).into(image)
            }
        }
    }
}