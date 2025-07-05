package com.google.android.piyush.dopamine.adapters

import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.piyush.dopamine.databinding.ChannelPlaylistsRendererBinding
import com.google.android.piyush.youtube.model.LockupViewModel

class PlaylistsChannelRenderer(
    private val playlists : MutableList<LockupViewModel>?
) : RecyclerView.Adapter<PlaylistsChannelRenderer.PlaylistsChannelViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsChannelViewHolder {
        return PlaylistsChannelViewHolder(
            ChannelPlaylistsRendererBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: PlaylistsChannelViewHolder,
        position: Int
    ) {
        playlists?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return playlists?.size ?: 0
    }

    inner class PlaylistsChannelViewHolder(
        private val binding : ChannelPlaylistsRendererBinding
    ) : RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.S)
        fun bind(playlist : LockupViewModel){
            val playlistImage = playlist.contentImage?.collectionThumbnailViewModel?.primaryThumbnail?.thumbnailViewModel?.image?.sources?.firstOrNull()?.url

            playlistImage.let {
                binding.apply {
                    shimmerEffectPlaylistImage.visibility = View.VISIBLE
                    shimmerEffectPlaylistImage.startShimmer()
                    playlistsImage.visibility = View.GONE
                }
                Glide.with(
                    binding.root.context
                ).load(it).listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.apply {
                            shimmerEffectPlaylistImage.visibility = View.GONE
                            shimmerEffectPlaylistImage.stopShimmer()
                            playlistsImage.visibility = View.GONE
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.apply {
                            shimmerEffectPlaylistImage.visibility = View.GONE
                            shimmerEffectPlaylistImage.stopShimmer()
                            playlistsImage.visibility = View.VISIBLE
                        }
                        return false
                    }

                })
            }.into(binding.playlistsImage)

            Glide.with(binding.root.context).load(playlistImage).into(binding.playlistsImageOverlay1)
            Glide.with(binding.root.context).load(playlistImage).into(binding.playlistsImageOverlay2)

            playlist.metadata?.lockupMetadataViewModel?.title?.content?.let { title ->
                binding.playlistName.visibility = View.GONE
                if(title.isNotEmpty()){
                    binding.playlistName.visibility = View.VISIBLE
                    binding.playlistName.text = title
                }
            }
        }
    }
}