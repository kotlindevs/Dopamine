package com.example.dopamine.DopamineHome.Travelling

import android.content.Context
import android.content.Intent
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

class TravellingAdapter(

    val context: Context,
    val Travelling_track : List<Travelling>?,
    val googleSession: googleSession = googleSession(context),

    ) : RecyclerView.Adapter<TravellingAdapter.ViewHolder>(){

    private val arrayList = ArrayList<Travelling>()

    class ViewHolder(Travelling_track : View) : RecyclerView.ViewHolder(Travelling_track) {
        val Tr_tracksPhoto : ShapeableImageView = Travelling_track.findViewById(R.id.TracksPhoto)
        val Tr_tracksName : MaterialTextView = Travelling_track.findViewById(R.id.TracksName)
        val Tr_tracksArtist : MaterialTextView = Travelling_track.findViewById(R.id.TracksArtist)
        val Tr_tracksLike : MaterialCheckBox = Travelling_track.findViewById(R.id.likeSong)
        val Tr_tracks : MaterialCardView = Travelling_track.findViewById(R.id.tracks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    fun getArrayList(): ArrayList<Travelling> {
        return arrayList.apply {
            Travelling_track?.forEach {
                add(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return Travelling_track!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracks = Travelling_track?.get(position)
        holder.Tr_tracksName.text = tracks!!.song_name
        holder.Tr_tracksArtist.text = tracks.artist_name
        holder.Tr_tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context,"You liked ❤️", Toast.LENGTH_LONG).show()
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.Tr_tracksPhoto)
        holder.Tr_tracks.setOnClickListener {
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position",position)
                        .putExtra("Travelling","https://api.npoint.io/2a0dd0282835656afdcd")
                )
        }
    }
}