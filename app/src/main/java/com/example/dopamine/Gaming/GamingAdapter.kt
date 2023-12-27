package com.example.dopamine.Gaming

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineMuiscPlayer.MasterMusicPlayer
import com.example.dopamine.R
import com.example.dopamine.authentication.googleSession
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class GamingAdapter(
    val context: Context,
    val Gaming_track : List<Gaming>?,
    val googleSession: googleSession = googleSession(context),
) : RecyclerView.Adapter<GamingAdapter.ViewHolder>(){
    class ViewHolder(Gaming_track: View) : RecyclerView.ViewHolder(Gaming_track) {
        val g_tracksPhoto : ShapeableImageView = Gaming_track.findViewById(R.id.TracksPhoto)
        val g_tracksName : MaterialTextView = Gaming_track.findViewById(R.id.TracksName)
        val g_tracksArtist : MaterialTextView = Gaming_track.findViewById(R.id.TracksArtist)
        val g_tracksLike : MaterialCheckBox = Gaming_track.findViewById(R.id.likeSong)
        val g_tracks : MaterialCardView = Gaming_track.findViewById(R.id.tracks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return Gaming_track!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracks = Gaming_track?.get(position)
        holder.g_tracksName.text = tracks!!.song_name
        holder.g_tracksArtist.text = tracks.artist_name
        holder.g_tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context,"You liked ❤️", Toast.LENGTH_LONG).show()
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.g_tracksPhoto)
        holder.g_tracks.setOnClickListener {
            Log.d("TracksPhoto",tracks.rc_url)
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("id",tracks.id)
                        .putExtra("artist_name",tracks.artist_name)
                        .putExtra("song_name",tracks.song_name)
                        .putExtra("type",tracks.type)
                        .putExtra("is_playable",tracks.is_playable)
                        .putExtra("url",tracks.mp_url)
                        .putExtra("preview_url",tracks.preview_url)
                        .putExtra("release_date",tracks.release_date)
                )
        }
    }
}