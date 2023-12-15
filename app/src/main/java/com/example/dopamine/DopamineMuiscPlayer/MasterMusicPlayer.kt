package com.example.dopamine.DopamineMuiscPlayer

import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.dopamine.R
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding


class MasterMusicPlayer : AppCompatActivity(){
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var musicSession: musicSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()
        musicSession = musicSession(this)

        if(musicSession.isSongRunning() && musicSession.isPlaying()){
            showToast("well")
        }

        try {
            mediaPlayer.setDataSource(applicationContext,intent.getStringExtra("preview_url")!!.toUri())
            mediaPlayer.prepare()
            binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
            binding.musicSeekBar.max = mediaPlayer.duration
            musicSession.setMusicPlayer(
                intent.getStringExtra("id")!!,
                intent.getStringExtra("artist_name")!!,
                intent.getStringExtra("song_name")!!,
                intent.getStringExtra("url")!!,
                intent.getStringExtra("preview_url")!!,
                intent.getStringExtra("type")!!,
                intent.getStringExtra("release_date")!!,
                intent.getBooleanExtra("is_playable",true)
                )
        }catch (e : Exception){
            showToast(e.message.toString())
        }

        Glide.with(applicationContext)
            .load(intent.getStringExtra("url").toString().toUri())
            .into(binding.TracksPhoto)

        binding.TracksName.text = intent.getStringExtra("song_name")

        binding.playPause.setOnClickListener {
            if(mediaPlayer.isPlaying){
                handler.removeCallbacks(runnable)
                mediaPlayer.pause()
                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
            }else{
                mediaPlayer.start()
                binding.playPause.setImageResource(R.drawable.baseline_pause_circle_24)
                updateSeekBar()
            }
        }

        runnable= Runnable {
            binding.musicSeekBar.setProgress(mediaPlayer.currentPosition,true)
            binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
            musicSession.currentPos(mediaPlayer.currentPosition)
            updateSeekBar()
        }

        binding.musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress)
                    binding.trackStart.text =
                        milliSecondToTime(mediaPlayer.currentPosition.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mediaPlayer.setOnCompletionListener {
            Log.d("UserVal","Called")
            binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
            binding.musicSeekBar.progress = 0
        }
    }

    private fun updateSeekBar(){
        if(mediaPlayer.isPlaying){
            binding.musicSeekBar.progress = mediaPlayer.currentPosition
            Log.d("Progress", mediaPlayer.currentPosition.toString())
            handler.postDelayed(runnable,100)
        }
    }

    private fun milliSecondToTime(milliSecond : Long) : String{
        var timer1 = ""
        var timer2 = ""
        val hours = milliSecond / (1000 * 60 * 60)
        val minutes = (milliSecond % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = ((milliSecond % (1000 * 60 * 60)) % (1000 * 60) / 1000)

        if(hours > 0){
            timer1 = "${hours}:"
        }

        timer2 = if(seconds < 10){
            "0${seconds}"
        }else{
            "$seconds"
        }

        timer1 = "$timer1$minutes:$timer2"
        return timer1
    }

    private fun showToast(str : String) {
        Toast.makeText(applicationContext,str,Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if(mediaPlayer.isPlaying){
            showToast("You are playing song in background.")
        }
    }
}