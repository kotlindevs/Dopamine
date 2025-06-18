package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.youtube.model.BrowseResponse.Contents.TwoColumnBrowseResultsRenderer.Tab.TabRenderer.Content.SectionListRenderer.Contents.ItemSectionRenderer.Contents.ShelfRenderer.Content.ExpandedShelfContentsRenderer.Item.VideoRenderer

class TrendingAdapter(
    private val context: Context,
    private var videos: List<VideoRenderer>?
) : RecyclerView.Adapter<TrendingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrendingViewHolder {
        return TrendingViewHolder(
            LayoutInflater.from(
                context
            ).inflate(
                R.layout.item_fragment_trending,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: TrendingViewHolder,
        position: Int
    ) {
        val video = videos?.get(position)
        Glide.with(context)
            .load(video?.thumbnail?.thumbnails?.get(0)?.url)
            .into(holder.image)
        holder.title.text = video?.title?.runs?.get(0)?.text
        holder.subtitle.text = video?.longBylineText?.runs?.get(0)?.text
        holder.video.setOnClickListener {
            context.startActivity(
                Intent(context, YoutubePlayer::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("videoId", video?.videoId.toString())
                    .putExtra("channelName", video?.longBylineText?.runs?.get(0)?.text.toString())
                    .putExtra("publishedTime", video?.publishedTimeText?.simpleText.toString())
                    .putExtra("viewCount",video?.shortViewCountText?.simpleText.toString())
                    .putExtra("channelImage", video?.avatar?.decoratedAvatarViewModel?.avatar?.avatarViewModel?.image?.sources?.get(0)?.url.toString())

            )
        }
    }

    override fun getItemCount(): Int {
        return videos?.size ?: 0
    }
}

class TrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val video : ConstraintLayout = itemView.findViewById(R.id.trendingVideo)
    val image : ShapeableImageView = itemView.findViewById(R.id.videoImage)
    val title : MaterialTextView = itemView.findViewById(R.id.videoTitle)
    val subtitle : MaterialTextView = itemView.findViewById(R.id.channelTitle)
}