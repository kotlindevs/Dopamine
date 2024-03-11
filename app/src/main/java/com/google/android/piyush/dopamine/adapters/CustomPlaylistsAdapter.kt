package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.model.CustomPlaylists
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.viewHolders.CustomPlaylistsViewHolder

class CustomPlaylistsAdapter(
    private val context: Context,
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

    override fun getItemCount(): Int {
        return playlists?.size!!
    }

    override fun onBindViewHolder(holder: CustomPlaylistsViewHolder, position: Int) {
        val databaseViewModel = DatabaseViewModel(context = context)
        val pref = context.getSharedPreferences("customPlaylist", Context.MODE_PRIVATE)
        val videoId = pref.getString("videoId", "")!!
        val title = pref.getString("title", "")!!
        val thumbnail = pref.getString("thumbnail", "")!!
        val customName = pref.getString("customName", "")!!
        val channelId = pref.getString("channelId", "")!!
        holder.title.text = playlists?.get(position)?.playListName
        holder.description.text = playlists?.get(position)?.playListDescription
        holder.selectedPlaylistItem.addOnCheckedStateChangedListener { _, isChecked ->
            if(isChecked == 1){
                val playlistName = databaseViewModel.getPlaylistsFromDatabase()[position]
                if(databaseViewModel.isExistsDataInPlaylist(playlistName, videoId).equals(true)){
                    holder.selectedPlaylistItem.isChecked = true
                }else{
                    databaseViewModel.addItemsInCustomPlaylist(
                        playlistName,
                        CustomPlaylists(
                            videoId = videoId,
                            title =  title,
                            thumbnail = thumbnail,
                            customName = customName,
                            channelId = channelId,
                        )
                    )
                    ToastUtilities.showToast(context, "Successfully added to playlist !")
                    holder.selectedPlaylistItem.isChecked = true
                }
            }else{
                ToastUtilities.showToast(context, "Successfully removed from playlist !")
            }
        }
    }
}