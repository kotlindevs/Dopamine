package com.google.android.piyush.dopamine.activities

import android.os.Bundle
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
            adapter = playlistsManager
        }
    }
}