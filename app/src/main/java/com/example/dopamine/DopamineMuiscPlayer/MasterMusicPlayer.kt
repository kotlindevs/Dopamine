package com.example.dopamine.DopamineMuiscPlayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.dopamine.R
import com.example.dopamine.TracksList.Adapter.TrackListAdapter
import com.example.dopamine.TracksList.TrackListApi.TracksApi
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.authentication.googleSession
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates


class MasterMusicPlayer : AppCompatActivity(){
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var googleSession: googleSession
    private lateinit var trackListAdapter: TrackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()
        googleSession = googleSession(this)
        trackListAdapter = TrackListAdapter(applicationContext,ArrayList(),googleSession)

        Retrofit.Builder()
            .baseUrl("https://api.npoint.io/a2bbf40c66d86d855cda/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksApi::class.java)
            .getTracks()
            .enqueue(object : Callback<List<Track>> {
                override fun onResponse(call: Call<List<Track>>, response: Response<List<Track>>) {
                    trackListAdapter = TrackListAdapter(
                        applicationContext,
                        response.body()!!,
                        googleSession,
                    )
                    var currentSongPosition = intent.getIntExtra("position",0)
                    val tracksList  = trackListAdapter.getArrayList()
                    var currentSong = tracksList[currentSongPosition]

                    setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                    playSong(currentSong.preview_url.toUri())

                    Log.d("currentSong",currentSong.toString())

                    binding.nextSong.setOnClickListener {
                        if(mediaPlayer.isPlaying){
                            handler.removeCallbacks(runnable)
                            mediaPlayer.reset()
                            binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                            binding.musicSeekBar.progress = 0
                            binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                            currentSongPosition  = (currentSongPosition + 1) % tracksList.size
                            currentSong = tracksList[currentSongPosition]
                            playSong(currentSong.preview_url.toUri())
                            setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        }else{
                            currentSongPosition  = (currentSongPosition + 1) % tracksList.size
                            currentSong = tracksList[currentSongPosition]
                            mediaPlayer.reset()
                            playSong(currentSong.preview_url.toUri())
                            setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                            Log.d("currentSong",currentSong.toString())
                            Log.d("currentSongPosition",currentSongPosition.toString())
                        }
                    }
                    binding.prevSong.setOnClickListener {
                        if(mediaPlayer.isPlaying){
                            handler.removeCallbacks(runnable)
                            mediaPlayer.reset()
                            binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                            binding.musicSeekBar.progress = 0
                            binding.trackStart.text = milliSecondToTime(mediaPlayer.currentPosition.toLong())
                            currentSongPosition  = (currentSongPosition - 1) % tracksList.size
                            currentSong = tracksList[currentSongPosition]
                            playSong(currentSong.preview_url.toUri())
                            setDataForSong(currentSong.mp_url.toUri(),currentSong.song_name)
                        }else{
                            currentSongPosition = (currentSongPosition - 1) % tracksList.size
                            currentSong = tracksList[currentSongPosition]
                            mediaPlayer.reset()
                            playSong(currentSong.preview_url.toUri())
                            setDataForSong(currentSong.mp_url.toUri(), currentSong.song_name)

                            Log.d("currentSong", currentSong.toString())
                            Log.d("currentSongPosition", currentSongPosition.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<List<Track>>, t: Throwable) {
                    Log.d("Tracks", t.message.toString())
                }
            })
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

    private fun playSong(songUrl : Uri){
        try {
            mediaPlayer.setDataSource(applicationContext,songUrl)
            mediaPlayer.prepare()
            binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
            binding.musicSeekBar.max = mediaPlayer.duration
        }catch (e : Exception){
            showToast(e.message.toString())
        }
    }

    private fun setDataForSong(songImage : Uri,songName : String){
        Glide.with(applicationContext)
            .load(songImage)
            .into(binding.TracksPhoto)

        binding.TracksName.text = songName
    }

    override fun onStart() {
        super.onStart()

        binding.likeSong.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                showToast("You liked ❤️")
            }
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
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}