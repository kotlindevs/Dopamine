package com.example.dopamine.DopamineMuiscPlayer

import android.content.Context
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
        val currentId = intent.getStringExtra("id").toString()
        val arrayList = sqliteHelper.trackLists1()
        val currentSong = sqliteHelper.trackLists(currentId, arrayList.indexOf(trackId(currentId)))
        var currentIndex = arrayList.indexOf(trackId(currentId))

    }
}