package com.example.dopamine.OldButGold

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dopamine.DopamineMuiscPlayer.MasterMusicPlayer
import com.example.dopamine.R
import com.google.android.material.card.MaterialCardView

class MusicChartAdapter(val context : Context,private val mList: List<Chart>) : RecyclerView.Adapter<MusicChartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_chart_card, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Chart = mList[position]
        holder.textView.text = Chart.song_name
        Glide.with(context)
            .load(Chart.mp_url)
            .into(holder.imageView)
        holder.songClick.setOnClickListener{
            context
                .startActivity(Intent(context,MasterMusicPlayer::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("id",Chart.id)
                    .putExtra("artist_name",Chart.artist_name)
                    .putExtra("song_name",Chart.song_name)
                    .putExtra("type",Chart.type)
                    .putExtra("is_playable",Chart.is_playable)
                    .putExtra("url",Chart.mp_url)
                    .putExtra("preview_url",Chart.preview_url)
                    .putExtra("release_date",Chart.release_date)
                )
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val imageView: ImageView = itemView.findViewById(R.id.music_chart_image)
        val textView: TextView = itemView.findViewById(R.id.music_chart_content)
        val songClick : MaterialCardView = itemView.findViewById(R.id.music_chart_card)
    }
}