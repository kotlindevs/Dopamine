package com.google.android.piyush.dopamine.adapters

import android.content.ContentValues.TAG
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
        val playlistName = databaseViewModel.getPlaylistsFromDatabase()[position]
        val videoId = pref.getString("videoId", "")!!
        val title = pref.getString("title", "")!!
        val thumbnail = pref.getString("thumbnail", "")!!
        val customName = pref.getString("customName", "")!!
        val channelId = pref.getString("channelId", "")!!
        val isVideoAlreadyAdded = databaseViewModel.isExistsDataInPlaylist(playlistName,videoId)
        Log.d(TAG, "videoId : $isVideoAlreadyAdded || playlistName : $playlistName")

        if(playlistName!=null) {
            if (isVideoAlreadyAdded.equals(true)) {
                holder.selectedPlaylistItem.isChecked = true
            }
        }

        holder.title.text = playlists?.get(position)?.playListName
        holder.description.text = playlists?.get(position)?.playListDescription
        holder.selectedPlaylistItem.addOnCheckedStateChangedListener { _, isChecked ->
            if(isChecked == 1){
                if(isVideoAlreadyAdded.equals(false)){
                    databaseViewModel.addItemsInCustomPlaylist(
                        playlistName,
                        playlistsData = CustomPlaylists(
                            videoId = videoId,
                            title = title,
                            thumbnail = thumbnail,
                            customName = customName,
                            channelId = channelId
                        )
                    )
                }
                ToastUtilities.showToast(context, "Successfully added to playlist :)")
            }
        }
    }
}