package com.example.dopamine

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dopamine.TracksList.DopamineTracks
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
                val i : Intent = Intent(context,DopamineTracks::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }else if(ItemsViewModel.text == "Blade Runner") {
                //
            }
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val imageView: ImageView = itemView.findViewById(R.id.music_img)
        val textView: TextView = itemView.findViewById(R.id.music_name)
        val onAlbumClick: MaterialCardView = itemView.findViewById(R.id.AlbumClick)
    }
}
