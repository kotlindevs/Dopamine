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
import com.google.android.material.card.MaterialCardView

class MusicChartAdapter(private val context : Context,private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<MusicChartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicChartAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_chart_card, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.textView.text = ItemsViewModel.text
        holder.songClick.setOnClickListener {
            Toast.makeText(context,ItemsViewModel.text,Toast.LENGTH_SHORT).show()
            val intent : Intent = Intent(context,DopamineUserProfile::class.java) //here change your playSong activity
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val imageView: ImageView = itemView.findViewById(R.id.music_chart_image)
        val textView: TextView = itemView.findViewById(R.id.music_chart_content)
        val songClick : MaterialCardView = itemView.findViewById(R.id.music_chart_card)
    }
}