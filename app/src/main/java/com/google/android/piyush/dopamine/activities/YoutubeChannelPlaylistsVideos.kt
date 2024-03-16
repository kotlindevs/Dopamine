package com.google.android.piyush.dopamine.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.YoutubePlaylistsVideosAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubeChannelBinding
import com.google.android.piyush.dopamine.databinding.ActivityYoutubeChannelPlaylistsVideosBinding
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelPlaylistsVideosViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelPlaylistsViewModelFactory
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class YoutubeChannelPlaylistsVideos : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubeChannelPlaylistsVideosBinding
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var youtubeChannelPlaylistsVideosViewModel: YoutubeChannelPlaylistsVideosViewModel
    private lateinit var youtubeChannelPlaylistsViewModelFactory: YoutubeChannelPlaylistsViewModelFactory
    private var youTubePlayer : YouTubePlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubeChannelPlaylistsVideosBinding.inflate(layoutInflater)
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        youtubeChannelPlaylistsViewModelFactory = YoutubeChannelPlaylistsViewModelFactory(youtubeRepositoryImpl)
        youtubeChannelPlaylistsVideosViewModel = ViewModelProvider(
            this, youtubeChannelPlaylistsViewModelFactory
        )[YoutubeChannelPlaylistsVideosViewModel::class.java]
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val playlistId = intent.getStringExtra("playlistId").toString()

        youtubeChannelPlaylistsVideosViewModel.getPlaylistsVideos(playlistId)

        youtubeChannelPlaylistsVideosViewModel.playlistsVideos.observe(this) { playlistsVideos ->
            when (playlistsVideos) {
                is YoutubeResource.Loading -> {}
                is YoutubeResource.Success -> {
                    binding.recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@YoutubeChannelPlaylistsVideos)
                        adapter = YoutubePlaylistsVideosAdapter(context,playlistsVideos.data)
                    }
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "Error: ${playlistsVideos.exception.message.toString()}")
                }
            }
        }

        binding.playlistsPlayer.apply {
            enableAutomaticInitialization = false
            lifecycle.addObserver(this)
            initialize(
                youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        super.onReady(youTubePlayer)
                        this@YoutubeChannelPlaylistsVideos.youTubePlayer = youTubePlayer
                    }
                },
                handleNetworkEvents = true,
                IFramePlayerOptions.Builder()
                    .controls(1)
                    .listType("playlist")
                    .list(playlistId)
                    .build()
            )
        }
    }
}