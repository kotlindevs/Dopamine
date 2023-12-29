package com.example.dopamine.DopamineHome.Gym

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

class GymAdapter(

    val context: Context,
    val Gym_track : List<Gym>?,

    val googleSession: googleSession = googleSession(context),
) : RecyclerView.Adapter<GymAdapter.ViewHolder>(){

    private val arrayList = ArrayList<Gym>()

    class ViewHolder(Phonk_track: View) : RecyclerView.ViewHolder(Phonk_track) {
        val gym_tracksPhoto : ShapeableImageView = Phonk_track.findViewById(R.id.TracksPhoto)
        val gym_tracksName : MaterialTextView = Phonk_track.findViewById(R.id.TracksName)
        val gym_tracksArtist : MaterialTextView = Phonk_track.findViewById(R.id.TracksArtist)
        val gym_tracksLike : MaterialCheckBox = Phonk_track.findViewById(R.id.likeSong)
        val gym_tracks : MaterialCardView = Phonk_track.findViewById(R.id.tracks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    fun getArrayList(): ArrayList<Gym> {
        return arrayList.apply {
            Gym_track?.forEach {
                add(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return Gym_track!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracks = Gym_track?.get(position)
        holder.gym_tracksName.text = tracks!!.song_name
        holder.gym_tracksArtist.text = tracks.artist_name
        holder.gym_tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context,"You liked ❤️", Toast.LENGTH_LONG).show()
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.gym_tracksPhoto)
        holder.gym_tracks.setOnClickListener {
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position",position)
                        .putExtra("Bollywood","https://api.npoint.io/2ffcaa6e10a2152f101b")
                )
        }
    }
}