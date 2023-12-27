package com.example.dopamine.Bollywood

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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class BollywoodAdapter(

    val context: Context,
    val Bollywood_track : List<Bollywood>?
) : RecyclerView.Adapter<BollywoodAdapter.BollywoodViewHolder>() {

    private val arrayList = ArrayList<Bollywood>()

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

    fun getArrayList(): ArrayList<Bollywood> {
        return arrayList.apply {
            Bollywood_track?.forEach {
                add(it)
            }
        }
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
            }
        }
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.B_tracksPhoto)
        holder.B_tracks.setOnClickListener {
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position",position)
                        .putExtra("Bollywood","https://api.npoint.io/362bf03a7dd20cef3dce")
                )
        }
    }
}