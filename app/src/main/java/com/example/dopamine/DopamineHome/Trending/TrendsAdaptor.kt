package com.example.dopamine.DopamineHome.Trending

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineMuiscPlayer.MasterMusicPlayer
import com.example.dopamine.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class TrendsAdaptor(

    var context: Context,
    val trendList: List<Trend>?

) : RecyclerView.Adapter<TrendsAdaptor.TrendsViewHolder>() {

    private val arrayList = ArrayList<Trend>()

    class TrendsViewHolder(tracksView : View) : RecyclerView.ViewHolder(tracksView){
        val tracksPhoto : ShapeableImageView = tracksView.findViewById(R.id.TracksPhoto)
        val tracksName : MaterialTextView = tracksView.findViewById(R.id.TracksName)
        val tracksArtist : MaterialTextView = tracksView.findViewById(R.id.TracksArtist)
        val tracks : MaterialCardView = tracksView.findViewById(R.id.tracks)
    }

    fun getArrayList(): ArrayList<Trend> {
        return arrayList.apply {
            trendList?.forEach {
                add(it)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendsViewHolder {
        return TrendsViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.tracks_list_layout_mdc,parent,false)
        )    }

    override fun getItemCount(): Int {
        return trendList!!.size
    }

    override fun onBindViewHolder(holder: TrendsViewHolder, position: Int) {
        val tracks = trendList?.get(position)
        holder.tracksName.text = tracks!!.song_name
        holder.tracksArtist.text = tracks.artist_name
        Glide.with(context)
            .load(tracks.mp_url)
            .into(holder.tracksPhoto)
        holder.tracks.setOnClickListener {
            context.startActivity(
                Intent(context,MasterMusicPlayer::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("position",position)
                    .putExtra("trendings","https://api.npoint.io/23ad1a516e3776ed61b5")
            )
        }
    }
}