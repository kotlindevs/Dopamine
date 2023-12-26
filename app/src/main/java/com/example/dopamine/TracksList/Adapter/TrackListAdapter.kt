package com.example.dopamine.TracksList.Adapter

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
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.authentication.googleSession
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class TrackListAdapter(
    val context: Context,
    val tracksList: List<Track>?,
    val googleSession: googleSession = googleSession(context),
) : RecyclerView.Adapter<TrackListAdapter.TracksViewHolder>() {

    private val arrayList = ArrayList<Track>()
    class TracksViewHolder(tracksView : View) : RecyclerView.ViewHolder(tracksView) {
        val tracksPhoto : ShapeableImageView = tracksView.findViewById(R.id.TracksPhoto)
        val tracksName : MaterialTextView = tracksView.findViewById(R.id.TracksName)
        val tracksArtist : MaterialTextView = tracksView.findViewById(R.id.TracksArtist)
        val tracksLike :MaterialCheckBox = tracksView.findViewById(R.id.likeSong)
        val tracks : MaterialCardView = tracksView.findViewById(R.id.tracks)
    }

    fun getArrayList(): ArrayList<Track> {
        return arrayList.apply {
            tracksList?.forEach {
                add(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(
            LayoutInflater
            .from(parent.context)
            .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return tracksList!!.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val tracks = tracksList?.get(position)
        holder.tracksName.text = tracks!!.song_name
        holder.tracksArtist.text = tracks.artist_name
        holder.tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context,"You liked ❤️",Toast.LENGTH_LONG).show()
                googleSession.songLike(true,tracks.id)
            }else{
                googleSession.songLike(false,tracks.id)
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.tracksPhoto)
        holder.tracks.setOnClickListener {
            Log.d("TracksPhoto",tracks.rc_url)
            context
                .startActivity(Intent(context,MasterMusicPlayer::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("position",position)
                )
        }

        if(googleSession.isSongLike()){
            if (googleSession.sharedPreferences.getString("id","")!=null){
                if (googleSession.sharedPreferences.getString("id","") == tracks.id){
                    holder.tracksLike.isChecked = true
                }
            }
        }
    }
}