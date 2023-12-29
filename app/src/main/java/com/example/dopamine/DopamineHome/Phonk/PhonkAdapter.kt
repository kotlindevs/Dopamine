package com.example.dopamine.DopamineHome.Phonk

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

class PhonkAdapter(

    val context: Context,
    val Phonk_track : List<Phonk>?,
    val googleSession: googleSession = googleSession(context),

    ) : RecyclerView.Adapter<PhonkAdapter.ViewHolder>(){

    private val arrayList = ArrayList<Phonk>()

    class ViewHolder(Phonk_track: View) : RecyclerView.ViewHolder(Phonk_track) {
        val Ph_tracksPhoto : ShapeableImageView = Phonk_track.findViewById(R.id.TracksPhoto)
        val Ph_tracksName : MaterialTextView = Phonk_track.findViewById(R.id.TracksName)
        val Ph_tracksArtist : MaterialTextView = Phonk_track.findViewById(R.id.TracksArtist)
        val Ph_tracksLike : MaterialCheckBox = Phonk_track.findViewById(R.id.likeSong)
        val Ph_tracks : MaterialCardView = Phonk_track.findViewById(R.id.tracks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    fun getArrayList(): ArrayList<Phonk> {
        return arrayList.apply {
            Phonk_track?.forEach {
                add(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return Phonk_track!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracks = Phonk_track?.get(position)
        holder.Ph_tracksName.text = tracks!!.song_name
        holder.Ph_tracksArtist.text = tracks.artist_name
        holder.Ph_tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                Toast.makeText(context, "You liked ❤️", Toast.LENGTH_LONG).show()
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.Ph_tracksPhoto)
        holder.Ph_tracks.setOnClickListener {
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position",position)
                        .putExtra("Phonk","https://api.npoint.io/cea292aae5d0b392abdc")
                )
        }
    }
}