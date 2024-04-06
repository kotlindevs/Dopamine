package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.CustomPlayListVAdapter
import com.google.android.piyush.dopamine.adapters.PlaylistsManagerAdapter
import com.google.android.piyush.dopamine.databinding.ActivityPlaylistsManagerBinding
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class PlaylistsManager : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistsManagerBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var playlistsManager: PlaylistsManagerAdapter
    private val realtimeViewModel by viewModels<RealtimeViewModel>()
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

        if(Firebase.auth.currentUser?.uid.isNullOrEmpty()){
            val playlists = databaseViewModel.getPlaylist()

            binding.playlists.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(applicationContext)
                playlistsManager = PlaylistsManagerAdapter(context, playlists, fragment = supportFragmentManager)
                adapter = playlistsManager
            }
        }else {
            realtimeViewModel.getMasterRecords()
            realtimeViewModel.listOfCustomPlaylistView.observe(this) {
                when(it) {
                    is RealtimeResource.Loading -> {}
                    is RealtimeResource.Success -> {
                        binding.playlists.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(applicationContext)
                            playlistsManager =
                                PlaylistsManagerAdapter(context, it.data, fragment = supportFragmentManager)
                            adapter = playlistsManager
                        }
                    }
                    is RealtimeResource.Error -> {}
                }
            }
        }
    }
}