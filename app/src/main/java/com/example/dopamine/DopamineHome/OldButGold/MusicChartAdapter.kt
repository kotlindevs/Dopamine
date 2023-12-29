package com.example.dopamine.DopamineHome.OldButGold

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

class MusicChartAdapter(

    val context : Context,
    val mList: List<Chart>?

) : RecyclerView.Adapter<MusicChartAdapter.ViewHolder>() {

    private val arrayList = ArrayList<Chart>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_chart_card, parent, false)

        return ViewHolder(view)
    }

    fun getArrayList(): ArrayList<Chart> {
        return arrayList.apply {
            mList?.forEach {
                add(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Chart = mList?.get(position)
        holder.textView.text = Chart!!.song_name
        Glide.with(context)
            .load(Chart.mp_url)
            .into(holder.imageView)
        holder.songClick.setOnClickListener{
            context
                .startActivity(Intent(context,MasterMusicPlayer::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("position",position)
                    .putExtra("OldButGold","https://api.npoint.io/504ec4f9cb720cbeb8df")
                )
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val imageView: ImageView = itemView.findViewById(R.id.music_chart_image)
        val textView: TextView = itemView.findViewById(R.id.music_chart_content)
        val songClick : MaterialCardView = itemView.findViewById(R.id.music_chart_card)
    }
}