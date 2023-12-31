package com.example.dopamine.DopamineHome

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dopamine.DopamineHome.Bollywood.DopamineBollywood
import com.example.dopamine.DopamineHome.Gaming.DopamineGaming
import com.example.dopamine.DopamineHome.Gym.DopamineGym
import com.example.dopamine.DopamineHome.Phonk.DopaminePhonk
import com.example.dopamine.DopamineHome.Remix.DopamineRemix
import com.example.dopamine.DopamineHome.TracksList.DopamineTracks
import com.example.dopamine.DopamineHome.Travelling.DopamineTravelling
import com.example.dopamine.DopamineHome.Trending.DopamineTrending
import com.example.dopamine.R
import com.google.android.material.card.MaterialCardView

class MusicAdapter(private val context : Context,private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_card, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.textView.text = ItemsViewModel.text
        holder.onAlbumClick.setOnClickListener {
            if(ItemsViewModel.text == "Dopamine's Picks"){
                val Picks : Intent = Intent(context, DopamineTracks::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Picks)
            }else if(ItemsViewModel.text == "Trending") {
                val Trending : Intent = Intent(context, DopamineTrending::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Trending)
            } else if(ItemsViewModel.text == "Bollywood"){
                val Bollywood : Intent = Intent(context, DopamineBollywood::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Bollywood)
            } else if(ItemsViewModel.text == "Travelling"){
                val Travelling : Intent = Intent(context, DopamineTravelling::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Travelling)
            } else if(ItemsViewModel.text == "Phonk"){
                val Phonk : Intent = Intent(context, DopaminePhonk::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Phonk)
            } else if(ItemsViewModel.text == "Remix"){
                val Remix : Intent = Intent(context, DopamineRemix::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Remix)
            } else if(ItemsViewModel.text == "Gaming and chill"){
                val Gaming : Intent = Intent(context, DopamineGaming::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Gaming)
            } else if(ItemsViewModel.text == "Gym & Workout"){
                val Gym : Intent = Intent(context, DopamineGym::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Gym)
            }
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val imageView: ImageView = itemView.findViewById(R.id.music_img)
        val textView: TextView = itemView.findViewById(R.id.music_name)
        val onAlbumClick: MaterialCardView = itemView.findViewById(R.id.AlbumClick)
    }
}
