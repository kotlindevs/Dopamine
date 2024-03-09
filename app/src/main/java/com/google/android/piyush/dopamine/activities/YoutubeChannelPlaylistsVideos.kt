package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityYoutubeChannelBinding
import com.google.android.piyush.dopamine.databinding.ActivityYoutubeChannelPlaylistsVideosBinding
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl

class YoutubeChannelPlaylistsVideos : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubeChannelPlaylistsVideosBinding
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var youtubeChannelViewModel: YoutubeChannelViewModel
    private lateinit var youtubeChannelViewModelFactory: YoutubeChannelViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubeChannelPlaylistsVideosBinding.inflate(layoutInflater)
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        youtubeChannelViewModelFactory = YoutubeChannelViewModelFactory(youtubeRepositoryImpl)
        youtubeChannelViewModel = ViewModelProvider(this, youtubeChannelViewModelFactory)[YoutubeChannelViewModel::class.java]
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}