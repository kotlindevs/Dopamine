package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.YoutubePlayer
import com.google.android.piyush.youtube.model.VideoRenderer

class TrendingAdapter(
    private val context: Context,
    private var videos: List<VideoRenderer>?
) : RecyclerView.Adapter<VideoRendererRecyclerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoRendererRecyclerViewHolder {
        return VideoRendererRecyclerViewHolder(
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
        holder: VideoRendererRecyclerViewHolder,
        position: Int
    ) {
        val video = videos?.get(position)
        val videoImage = video?.thumbnail?.thumbnails?.get(0)?.url
        videoImage?.let {
            if(it.isNotEmpty()){
                holder.shimmerEffectVideoImage.apply {
                    visibility = View.VISIBLE
                    startShimmer()
                }
                holder.image.visibility = View.VISIBLE

                Glide.with(context)
                    .load(it)
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.shimmerEffectVideoImage.apply {
                                stopShimmer()
                                visibility = View.GONE
                            }
                            holder.image.visibility = View.VISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.shimmerEffectVideoImage.apply {
                                stopShimmer()
                                visibility = View.GONE
                            }
                            holder.image.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(holder.image)
            }
        }

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
                    .putExtra("videoLength", video?.lengthText?.simpleText.toString())
                    .putExtra("channelImage", video?.avatar?.decoratedAvatarViewModel?.avatar?.avatarViewModel?.image?.sources?.get(0)?.url.toString())

            )
        }
    }

    override fun getItemCount(): Int {
        return videos?.size ?: 0
    }
}

class VideoRendererRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val video : ConstraintLayout = itemView.findViewById(R.id.trendingVideo)
    val image : ShapeableImageView = itemView.findViewById(R.id.videoImage)
    val title : MaterialTextView = itemView.findViewById(R.id.videoTitle)
    val subtitle : MaterialTextView = itemView.findViewById(R.id.channelTitle)
    val shimmerEffectVideoImage : ShimmerFrameLayout = itemView.findViewById(R.id.shimmerEffectVideoImage)
}