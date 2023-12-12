package com.example.dopamine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Like_songs : AppCompatActivity() {
    private lateinit var likeSongList : ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_songs)
    }
}