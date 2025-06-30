package com.google.android.piyush.dopamine.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.piyush.database.entities.UserPlaylists
import com.google.android.piyush.dopamine.databinding.ItemUserPlaylistsBinding

class UserPlaylistsAdapter(private val playlists: MutableList<UserPlaylists>?)
    : RecyclerView.Adapter<UserPlaylistsAdapter.UserPlaylistsViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserPlaylistsViewHolder {
        return UserPlaylistsViewHolder(
            ItemUserPlaylistsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: UserPlaylistsViewHolder,
        position: Int
    ) {
        val playlist = playlists?.get(position)
        playlist?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return playlists?.size ?: 0
    }

    inner class UserPlaylistsViewHolder(private val binding: ItemUserPlaylistsBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(playlist: UserPlaylists?){
                playlist?.let{
                    binding.apply {
                        playlistNoImageView.visibility = View.VISIBLE
                        playlistTitle.apply {
                            visibility = View.VISIBLE
                            text = it.playlistName
                        }
                        playlistDescription.apply {
                            visibility = View.VISIBLE
                            text = it.playlistDescription
                        }
                    }
                }
            }
        }
}