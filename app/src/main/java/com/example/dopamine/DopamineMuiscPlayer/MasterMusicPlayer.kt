package com.example.dopamine.DopamineMuiscPlayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class MasterMusicPlayer : AppCompatActivity() ,Player.Listener{
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    private val exoPlayer by lazy { ExoPlayer.Builder(this).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupMediaAudio()
    }

    private fun setupMediaAudio() {
        val mediaItem = MediaItem.fromUri(intent.getStringExtra("preview_url")!!.toUri())
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        binding.stylePLayer.player = exoPlayer
    }
}