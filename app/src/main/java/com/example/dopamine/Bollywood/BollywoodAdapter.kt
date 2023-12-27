package com.example.dopamine.Bollywood

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

class BollywoodAdapter(
    val context: Context,
    val Bollywood_track : List<Bollywood>?,
    val googleSession: googleSession = googleSession(context),
) : RecyclerView.Adapter<BollywoodAdapter.BollywoodViewHolder>() {
    class BollywoodViewHolder(Bollywood_track : View) : RecyclerView.ViewHolder(Bollywood_track) {
        val B_tracksPhoto : ShapeableImageView = Bollywood_track.findViewById(R.id.TracksPhoto)
        val B_tracksName : MaterialTextView = Bollywood_track.findViewById(R.id.TracksName)
        val B_tracksArtist : MaterialTextView = Bollywood_track.findViewById(R.id.TracksArtist)
        val B_tracksLike : MaterialCheckBox = Bollywood_track.findViewById(R.id.likeSong)
        val B_tracks : MaterialCardView = Bollywood_track.findViewById(R.id.tracks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BollywoodViewHolder {
        return BollywoodViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return Bollywood_track!!.size
    }

    override fun onBindViewHolder(holder: BollywoodViewHolder, position: Int) {
        val tracks = Bollywood_track?.get(position)
        holder.B_tracksName.text = tracks!!.song_name
        holder.B_tracksArtist.text = tracks.artist_name
        holder.B_tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context,"You liked ❤️",Toast.LENGTH_LONG).show()
                googleSession.songLike(true,tracks.id)
            }else{
                googleSession.songLike(false,tracks.id)
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.B_tracksPhoto)
        holder.B_tracks.setOnClickListener {
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

        if(googleSession.isSongLike()){
            if (googleSession.sharedPreferences.getString("id","")!=null){
                if (googleSession.sharedPreferences.getString("id","") == tracks.id){
                    holder.B_tracksLike.isChecked = true
                }
            }
        }
    }
}