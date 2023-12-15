package com.example.dopamine.DopamineMuiscPlayer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.HttpDataSource


class MasterMusicPlayer : AppCompatActivity(){
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    private lateinit var musicPref: MusicPref
    private val player by lazy { ExoPlayer.Builder(this).build() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stylePLayer.player = player
        musicPref = MusicPref(applicationContext)

            player.setMediaItem(
                MediaItem
                .fromUri(intent
                    .getStringExtra("preview_url")!!
                    .toUri()
                ),true
            )
        player.prepare()
        player.play()
        musicPref.playTrack(intent.getStringExtra("id")!!)

        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        showToast("playing")
                    } else {
                        showToast("pause")
                    }
                }
            }
        )

        player.addListener(
            object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    val cause = error.cause
                    if (cause is HttpDataSource.HttpDataSourceException) {
                        showToast(cause.message.toString())
                        if (cause is HttpDataSource.InvalidResponseCodeException) {
                            showToast(cause.message.toString())
                        }
                    }
                }
            }
        )
    }

    private fun showToast(str : String) {
        Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
        player.playbackState
        Log.d("UserVal",player.playbackState.toString())

    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
        player.playbackState
        Log.d("UserVal",player.playbackState.toString())
    }
}