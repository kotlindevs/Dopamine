package com.example.dopamine.DopamineLibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dopamine.databinding.ActivityLikeSongsBinding

class Like_songs : AppCompatActivity() {
    private lateinit var likeSongList : ArrayList<String>
    private lateinit var binding: ActivityLikeSongsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setOnClickListener {
            finish()
        }
    }
}