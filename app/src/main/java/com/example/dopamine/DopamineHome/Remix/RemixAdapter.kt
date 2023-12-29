package com.example.dopamine.DopamineHome.Remix

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

class RemixAdapter(
    val context: Context,
    val Remix_track : List<Remix>?,
    val googleSession: googleSession = googleSession(context),
) : RecyclerView.Adapter<RemixAdapter.ViewHolder>() {

    private val arrayList = ArrayList<Remix>()

    class ViewHolder(Remix_track: View) : RecyclerView.ViewHolder(Remix_track) {
        val Rx_tracksPhoto : ShapeableImageView = Remix_track.findViewById(R.id.TracksPhoto)
        val Rx_tracksName : MaterialTextView = Remix_track.findViewById(R.id.TracksName)
        val Rx_tracksArtist : MaterialTextView = Remix_track.findViewById(R.id.TracksArtist)
        val Rx_tracksLike : MaterialCheckBox = Remix_track.findViewById(R.id.likeSong)
        val Rx_tracks : MaterialCardView = Remix_track.findViewById(R.id.tracks)
    }

    fun getArrayList(): ArrayList<Remix> {
        return arrayList.apply {
            Remix_track?.forEach {
                add(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return Remix_track!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracks = Remix_track?.get(position)
        holder.Rx_tracksName.text = tracks!!.song_name
        holder.Rx_tracksArtist.text = tracks.artist_name
        holder.Rx_tracksLike.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context,"You liked ❤️", Toast.LENGTH_LONG).show()
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.Rx_tracksPhoto)
        holder.Rx_tracks.setOnClickListener {
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position",position)
                        .putExtra("Remix","https://api.npoint.io/12abb65f1a120508b605")
                )
        }
    }
}