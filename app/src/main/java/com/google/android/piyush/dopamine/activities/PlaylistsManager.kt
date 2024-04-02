package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.PlaylistsManagerAdapter
import com.google.android.piyush.dopamine.databinding.ActivityPlaylistsManagerBinding

class PlaylistsManager : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistsManagerBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var playlistsManager: PlaylistsManagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlaylistsManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseViewModel = DatabaseViewModel(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val playlists = databaseViewModel.getPlaylist()

        binding.playlists.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            playlistsManager = PlaylistsManagerAdapter(context, playlists, fragment = supportFragmentManager)
            playlistsManager.notifyDataSetChanged()
            adapter = playlistsManager
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("PlaylistsManager", "onStart: ")
    }
    override fun onResume() {
        super.onResume()
        Log.d("PlaylistsManager", "onResume: ")
    }
    override fun onPause() {
        super.onPause()
        Log.d("PlaylistsManager", "onPause: ")
    }
    override fun onStop() {
        super.onStop()
        Log.d("PlaylistsManager", "onStop: ")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("PlaylistsManager", "onDestroy: ")
    }
}