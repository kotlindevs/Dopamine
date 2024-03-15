package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import android.util.Log
import android.view.SoundEffectConstants
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.YoutubeChannelPlaylistsAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubeChannelBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource

class YoutubeChannel : AppCompatActivity() {

    private val TAG = "YoutubeChannel"
    private lateinit var binding: ActivityYoutubeChannelBinding
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var youtubeChannelViewModel: YoutubeChannelViewModel
    private lateinit var youtubeChannelViewModelFactory: YoutubeChannelViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubeChannelBinding.inflate(layoutInflater)
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        youtubeChannelViewModelFactory = YoutubeChannelViewModelFactory(youtubeRepositoryImpl)
        youtubeChannelViewModel = ViewModelProvider(
            this,
            youtubeChannelViewModelFactory
        )[YoutubeChannelViewModel::class.java]
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val channelId = intent.getStringExtra("channelId").toString()
        Log.d(TAG, "channelId: $channelId")

        youtubeChannelViewModel.getChannelDetails(channelId)

        youtubeChannelViewModel.channelDetails.observe(this) { channelDetails ->
            when(channelDetails){
                is YoutubeResource.Loading -> {}
                is YoutubeResource.Success -> {
                    val channelTitle = channelDetails.data.items?.get(0)?.snippet?.title
                    val channelCustomUrl = channelDetails.data.items?.get(0)?.snippet?.customUrl
                    val channelSubscribers = " ${counter(channelDetails.data.items?.get(0)?.statistics?.subscriberCount!!.toInt()) } Subscribers"
                    val channelDescription = channelDetails.data.items?.get(0)?.snippet?.description
                    val channelBanner = channelDetails.data.items?.get(0)?.brandingSettings?.image?.bannerExternalUrl
                    val channelLogo = channelDetails.data.items?.get(0)?.snippet?.thumbnails?.default?.url

                    if(channelBanner.isNullOrEmpty()){
                        Glide.with(this).load(Utilities.DEFAULT_BANNER).into(binding.channelBanner)
                    }else {
                        Glide.with(this).load(channelBanner).into(binding.channelBanner)
                    }

                    if(channelLogo.isNullOrEmpty()){
                        Glide.with(this).load(Utilities.DEFAULT_LOGO).into(binding.channelLogo)
                    }else {
                        Glide.with(this).load(channelLogo).into(binding.channelLogo)
                    }

                    binding.apply {
                        this.channelTitle.text = channelTitle
                        this.channelCustomUrl.text = channelCustomUrl
                        this.channelSubscribers.text = channelSubscribers
                        this.channelDescription.text = channelDescription
                    }
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "Error: ${channelDetails.exception.message.toString()}")
                }
            }
        }

        youtubeChannelViewModel.getChannelsPlaylist(channelId)

        youtubeChannelViewModel.channelsPlaylists.observe(this) { channelsPlaylists ->
            when(channelsPlaylists){
                is YoutubeResource.Loading -> {}
                is YoutubeResource.Success -> {
                    binding.channelsPlaylist.apply {
                        layoutManager = LinearLayoutManager(this@YoutubeChannel)
                        adapter = YoutubeChannelPlaylistsAdapter(
                            applicationContext, channelsPlaylists.data
                        )
                    }
                    Log.d(TAG, "channelsPlaylists: ${channelsPlaylists.data}")
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "Error: ${channelsPlaylists.exception.message.toString()}")
                    binding.channelPlaylistLoader.apply {
                        visibility = View.VISIBLE
                        setAnimation(R.raw.no_data)
                        playAnimation()
                        playSoundEffect(SoundEffectConstants.CLICK)  //sound effect
                        speed = 1.5f        //speed of animation
                        @Suppress("DEPRECATION")
                        loop(true)
                    }
                }
            }
        }
    }
    private fun counter(count : Int) : String {
        var num: Double = count.toDouble()
        val data: String
        if (num > 1000000.00) {
            num /= 1000000.00
            data = "${num}M"
        } else {
            num /= 1000
            data = "${num}K"
        }
        return data
    }
}