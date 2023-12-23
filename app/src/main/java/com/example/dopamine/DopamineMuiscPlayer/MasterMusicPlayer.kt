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
import com.example.dopamine.SqliteDb.SqliteHelper
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.TracksList.TracksDataClass.trackId
import com.example.dopamine.databinding.ActivityMasterMusicPlayerBinding


class MasterMusicPlayer : AppCompatActivity(){
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: ActivityMasterMusicPlayerBinding
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var sqliteHelper: SqliteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()
        sqliteHelper = SqliteHelper(this)
        val id = intent.getStringExtra("id").toString()
        val arrayList =  sqliteHelper.trackLists1()

        binding.nextSong.setOnClickListener {
            try {
                handler.removeCallbacks(runnable)
                mediaPlayer.pause()
                mediaPlayer.stop()
                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                mediaPlayer.setDataSource(applicationContext,sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).plus(1)].preview_url!!.toUri())
                binding.musicSeekBar.progress = 0
                mediaPlayer.prepare()
                binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
                binding.musicSeekBar.max = mediaPlayer.duration
            }catch (e : Exception){
                showToast(e.message.toString())
            }

            Glide.with(applicationContext)
                .load(sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).plus(1)].mp_url!!.toUri())
                .into(binding.TracksPhoto)

            binding.TracksName.text = sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).plus(1)].song_name.toString()
        }

        binding.prevSong.setOnClickListener {
            try {
                handler.removeCallbacks(runnable)
                mediaPlayer.pause()
                mediaPlayer.stop()
                binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
                mediaPlayer.setDataSource(applicationContext,sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).minus(1)].preview_url!!.toUri())
                binding.musicSeekBar.progress = 0
                mediaPlayer.prepare()
                binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
                binding.musicSeekBar.max = mediaPlayer.duration
            }catch (e : Exception){
                showToast(e.message.toString())
            }
            Glide.with(applicationContext)
                .load(sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).minus(1)].mp_url!!.toUri())
                .into(binding.TracksPhoto)

            binding.TracksName.text = sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).minus(1)].song_name.toString()
        }

        if(sqliteHelper.Exists(intent.getStringExtra("id").toString())){
            Log.d("UserVal",arrayList.indexOf(trackId(id)).toString())
            Log.d("UserVal",sqliteHelper.trackLists(id,arrayList.indexOf(trackId(id)).plus(1)).toString())
            Log.d("UserValNextPosition",arrayList.indexOf(trackId(id)).plus(1).toString())
            Log.d("UserValPreviousPosition",arrayList.indexOf(trackId(id)).minus(1).toString())
            Log.d("UserVal", sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id)).plus(1)] .toString())
            try {
                mediaPlayer.setDataSource(applicationContext,sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id))].preview_url!!.toUri())
                mediaPlayer.prepare()
                binding.trackEnd.text = milliSecondToTime(mediaPlayer.duration.toLong())
                binding.musicSeekBar.max = mediaPlayer.duration
            }catch (e : Exception){
                showToast(e.message.toString())
            }
        }



        Glide.with(applicationContext)
            .load(sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id))].mp_url!!.toUri())
            .into(binding.TracksPhoto)

        binding.TracksName.text = sqliteHelper.trackLists0()[arrayList.indexOf(trackId(id))].song_name.toString()

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
            binding.playPause.setImageResource(R.drawable.baseline_play_circle_24)
            binding.musicSeekBar.progress = 0
        }

    }

    private fun updateSeekBar(){
        if(mediaPlayer.isPlaying){
            binding.musicSeekBar.progress = mediaPlayer.currentPosition
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