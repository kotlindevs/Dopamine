package com.example.dopamine.DopamineMuiscPlayer

import android.media.MediaPlayer
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()

        binding.musicSeekBar.max = mediaPlayer.duration

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
            updateSeekBar()
        }

        prepareMediaPlayer()

        binding.musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mediaPlayer.seekTo(progress)
                binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                val playPos = (mediaPlayer.duration /100) * seekBar!!.progress
                mediaPlayer.seekTo(playPos)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val stopPos = (mediaPlayer.duration /100) * seekBar!!.progress
                mediaPlayer.seekTo(stopPos)
            }
        })

        mediaPlayer.setOnCompletionListener {
            binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
        }

    }

    private fun prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(applicationContext,intent.getStringExtra("preview_url")!!.toUri())
            mediaPlayer.prepare()
            binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
        }catch (e : Exception){
            showToast(e.message.toString())
        }
    }
    private fun updateSeekBar(){
        if(mediaPlayer.isPlaying){
            binding.musicSeekBar.progress = (mediaPlayer.currentPosition)
            Log.d("Progress", mediaPlayer.currentPosition.toString())
            handler.postDelayed(runnable,1000)
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
}