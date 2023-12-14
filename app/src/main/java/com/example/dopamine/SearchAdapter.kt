package com.example.dopamine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class SearchAdapter(private val context: Context,private val mList: List<SearchModel>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_activity_card,parent,false)
        return ViewHolder(view)
    }

    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView) {
        val imageView : ImageView = itemView.findViewById(R.id.browse_image)
        val textView : TextView = itemView.findViewById(R.id.browse_text)
        val browse_click : MaterialCardView = itemView.findViewById(R.id.browse_all)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val SearchModel = mList[position]
        holder.imageView.setImageResource(SearchModel.image)
        holder.textView.text = SearchModel.text
        holder.browse_click.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}