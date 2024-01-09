package com.example.dopamine.DopamineHome.TopWeek

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

class TopWeekAdapter(

    val context : Context,
    val mList: List<TopWeek>?

) : RecyclerView.Adapter<TopWeekAdapter.ViewHolder>()
{

    private val arrayList = ArrayList<TopWeek>()

    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.top_week_image)
        val textView: TextView = itemView.findViewById(R.id.top_week_content)
        val songClick : MaterialCardView = itemView.findViewById(R.id.top_week_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopWeekAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.topweekcard, parent, false)

        return ViewHolder(view)
    }

    fun getArrayList(): ArrayList<TopWeek> {
        return arrayList.apply {
            mList?.forEach {
                add(it)
            }
        }
    }

    override fun onBindViewHolder(holder: TopWeekAdapter.ViewHolder, position: Int) {
        val TopWeek = mList?.get(position)
        holder.textView.text = TopWeek!!.song_name
        Glide.with(context)
            .load(TopWeek.mp_url)
            .into(holder.imageView)
        holder.songClick.setOnClickListener{
            context
                .startActivity(
                    Intent(context, MasterMusicPlayer::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("position",position)
                    .putExtra("TopWeek","https://api.npoint.io/bd952edd473e435304b3")
                )
        }
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }
}