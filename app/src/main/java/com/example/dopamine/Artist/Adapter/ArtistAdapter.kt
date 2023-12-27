package com.example.dopamine.Artist.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.Artist.ArtistData.Artist
import com.example.dopamine.Artist.ArtistProfile
import com.example.dopamine.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView

class ArtistAdapter(

    val context: Context,
    val artistList : List<Artist>?

) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    private val arrayList = ArrayList<Artist>()

    class ArtistViewHolder(artistViewData : View) : RecyclerView.ViewHolder(artistViewData) {
        val artistPhoto : CircleImageView = artistViewData.findViewById(R.id.ArtistPhoto)
        val artistName : MaterialTextView = artistViewData.findViewById(R.id.ArtistName)
        val artist : MaterialCardView = artistViewData.findViewById(R.id.artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.artist_layout, parent, false)
        )
    }

    fun getArrayList(): ArrayList<Artist> {
        return arrayList.apply {
            artistList?.forEach {
                add(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return artistList!!.size
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artistList?.get(position)
        holder.artistName.text = artist!!.name
        Glide.with(context)
            .load(artist.ar_url)
            .into(holder.artistPhoto)
        holder.artist.setOnClickListener {
            context
                .startActivity(
                    Intent(context, ArtistProfile::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position",position)
                        .putExtra("Artist","https://api.npoint.io/a3ea088449c3a010fb5d")
                )
        }
    }
}