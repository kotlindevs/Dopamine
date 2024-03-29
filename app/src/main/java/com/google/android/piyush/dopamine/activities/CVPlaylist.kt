package com.google.android.piyush.dopamine.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.CustomPlaylistsVDataAdapter
import com.google.android.piyush.dopamine.databinding.ActivityCvplaylistBinding

class CVPlaylist : AppCompatActivity() {
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var binding : ActivityCvplaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseViewModel = DatabaseViewModel(applicationContext)
        binding = ActivityCvplaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val playlist = intent.getStringExtra("playlistName")
        if(playlist != null) {
            binding.materialTextView.text = playlist
            binding.customPlayListVideos.apply {
                setHasFixedSize(false)
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                adapter = CustomPlaylistsVDataAdapter(databaseViewModel.getPlaylistData(playlist),context)
            }
            Log.d(TAG, " -> Activity : CVPlaylist || PlaylistData : $playlist")
        }
    }
}