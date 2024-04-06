package com.google.android.piyush.dopamine.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.model.CustomPlaylists
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.dopamine.viewHolders.CustomPlaylistsViewHolder
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
        val viewModel = RealtimeViewModel()
        val pref = context.getSharedPreferences("customPlaylist", Context.MODE_PRIVATE)
        val videoId = pref.getString("videoId", "")!!
        val title = pref.getString("title", "")!!
        val thumbnail = pref.getString("thumbnail", "")!!
        val channelId = pref.getString("channelId", "")!!
        val viewCount = pref.getString("viewCount", "")!!
        val channelTitle = pref.getString("channelTitle", "")!!
        val publishedAt = pref.getString("publishedAt", "")!!
        val duration = pref.getString("duration", "")!!

        if(Firebase.auth.currentUser?.uid.isNullOrEmpty()){
            val playlistName = databaseViewModel.getPlaylistsFromDatabase()[position]
            val isVideoAlreadyAdded = databaseViewModel.isExistsDataInPlaylist(playlistName,videoId)

            if(playlistName.isEmpty()) {
                Log.d(TAG, "playlistName : $playlistName")
            }else{
                if (isVideoAlreadyAdded.equals(true)) {
                    holder.selectedPlaylistItem.isChecked = true
                }else{
                    holder.selectedPlaylistItem.isChecked = false
                }
            }

            holder.selectedPlaylistItem.addOnCheckedStateChangedListener { _, isChecked ->
                if(isChecked == 1){
                    if(isVideoAlreadyAdded.equals(false)){
                        databaseViewModel.addItemsInCustomPlaylist(
                            playlistName,
                            playlistsData = CustomPlaylists(
                                videoId = videoId,
                                title = title,
                                thumbnail = thumbnail,
                                channelId = channelId,
                                viewCount = viewCount,
                                channelTitle = channelTitle ,
                                publishedAt = publishedAt,
                                duration = duration
                            )
                        )
                        Log.d(TAG, "videoId : $videoId || playlistName : $playlistName")
                    }
                    ToastUtilities.showToast(context, "Successfully added to playlist :)")
                }else{
                    if(isVideoAlreadyAdded.equals(true)){
                        Log.d(TAG, "videoId : $videoId || playlistName : $playlistName")
                        databaseViewModel.deleteVideoFromPlaylist(
                            playlistName,
                            videoId
                        )
                    }
                }
            }
        }else{
            viewModel.getAllPlaylists()
            viewModel.getAllPlaylists.observeForever {
                if(it is RealtimeResource.Success) {
                    it.data?.let { playlist ->
                        val playlistName = playlist[0]
                        dopamineSharedPreferences(context).edit {
                            putString("finalPlaylistName", playlistName)
                        }
                        Log.d(TAG, "playlistName : ${playlist[position]}")
                    }
                }
            }
            val playlistName = dopamineSharedPreferences(context).getString("finalPlaylistName", "")
            viewModel.isVideoExists(
                playlistName = playlistName!!,
                videoId = videoId
            )
            viewModel.isVideoExists.observeForever {
                if(it is RealtimeResource.Success) {
                    it.data?.let { isVideoAlreadyAdded ->
                        if (isVideoAlreadyAdded.equals(true)) {
                            holder.selectedPlaylistItem.isChecked = true
                            val isVideoExists = true
                            dopamineSharedPreferences(context).edit {
                                putBoolean("finalIsVideoExists", isVideoExists)
                            }
                        }else{
                            holder.selectedPlaylistItem.isChecked = false
                            val isVideoExists = false
                            dopamineSharedPreferences(context).edit {
                                putBoolean("finalIsVideoExists", isVideoExists)
                            }
                        }
                    }
                }
            }

            val isVideoExists = dopamineSharedPreferences(context).getBoolean("finalIsVideoExists", false)
            holder.selectedPlaylistItem.addOnCheckedStateChangedListener { _, isChecked ->
                if(isChecked == 1){
                    if(isVideoExists.equals(false)){
                        viewModel.addInYourPlaylist(
                            playlists = CustomPlaylists(
                                videoId = videoId,
                                title = title,
                                thumbnail = thumbnail,
                                channelId = channelId,
                                viewCount = viewCount,
                                channelTitle = channelTitle ,
                                publishedAt = publishedAt,
                                duration = duration
                            ),
                            playlistName
                        )

                        viewModel.updatePlaylist(
                            playListName = playlistName,
                            playlists = CustomPlaylists(
                                videoId = videoId,
                                title = title,
                                thumbnail = thumbnail,
                                channelId = channelId,
                                viewCount = viewCount,
                                channelTitle = channelTitle ,
                                publishedAt = publishedAt,
                                duration = duration
                            )
                        )
                    }
                    ToastUtilities.showToast(context, "Successfully added to playlist :)")
                }else{
                    if(isVideoExists.equals(true)){
                        viewModel.deleteFromPlaylist(
                            playlistName,
                            videoId
                        )
                    }
                }
            }
        }
        holder.title.text = playlists?.get(position)?.playListName
        holder.description.text = playlists?.get(position)?.playListDescription
    }
}