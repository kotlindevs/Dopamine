package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.CustomPlaylistsVDataAdapter
import com.google.android.piyush.dopamine.databinding.ActivityCvplaylistBinding
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CVPlaylist : AppCompatActivity() {
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var binding : ActivityCvplaylistBinding
    private val viewModel by viewModels<RealtimeViewModel>()

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
        val playlistName = intent.getStringExtra("playlistName")
        if(!playlistName.isNullOrEmpty()) {
            if(Firebase.auth.currentUser?.uid.isNullOrEmpty()) {
                binding.customPlayListVideos.apply {
                    setHasFixedSize(false)
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                    adapter = CustomPlaylistsVDataAdapter(
                        databaseViewModel.getPlaylistData(playlistName),
                        context
                    )
                }
            }else{
//                viewModel.getPlaylistVideos(playlistName)
                viewModel.getPlaylistData.observe(this) {
                    if(it is RealtimeResource.Success) {
                        it.data?.let { videos ->
                            binding.customPlayListVideos.apply {
                                setHasFixedSize(false)
                                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                                adapter = CustomPlaylistsVDataAdapter(videos,context)
                            }
                        }
                    }
                }
            }
            binding.playlistName.text = playlistName
            binding.playlistDescription.text = intent.getStringExtra("playlistDescription")
            if(Firebase.auth.currentUser?.uid.isNullOrEmpty()) {
                if (databaseViewModel.getPlaylistData(playlistName).isNotEmpty()) {
                    binding.apply {
                        binding.playlistDetails.visibility = android.view.View.VISIBLE
                        binding.playlistImage.visibility = android.view.View.VISIBLE
                        binding.playlistName.visibility = android.view.View.VISIBLE
                        binding.playlistDescription.visibility = android.view.View.VISIBLE
                        binding.customPlayListVideos.visibility = android.view.View.VISIBLE
                    }
                    Glide.with(applicationContext)
                        .load(databaseViewModel.getPlaylistData(playlistName)[0].thumbnail)
                        .into(binding.playlistImage)
                    binding.apply {
                        textNoPlaylist1.visibility = android.view.View.GONE
                        textNoPlaylist2.visibility = android.view.View.GONE
                        noPlaylistImage.visibility = android.view.View.GONE
                    }
                } else {
                    binding.apply {
                        binding.playlistImage.visibility = android.view.View.GONE
                        binding.playlistName.visibility = android.view.View.GONE
                        binding.playlistDescription.visibility = android.view.View.GONE
                        binding.customPlayListVideos.visibility = android.view.View.GONE
                    }
                }
            }else{
               // viewModel.getPlaylistVideos(playlistName)
                viewModel.getPlaylistData.observe(this) {
                    if(it is RealtimeResource.Success) {
                        it.data?.let { videos ->
                            if(videos.isNotEmpty()){
                                binding.apply {
                                    binding.playlistDetails.visibility = android.view.View.VISIBLE
                                    binding.playlistImage.visibility = android.view.View.VISIBLE
                                    binding.playlistName.visibility = android.view.View.VISIBLE
                                    binding.playlistDescription.visibility = android.view.View.VISIBLE
                                    binding.customPlayListVideos.visibility = android.view.View.VISIBLE
                                }
                                Glide.with(applicationContext)
                                    .load(videos[0].thumbnail)
                                    .into(binding.playlistImage)
                                binding.apply {
                                    textNoPlaylist1.visibility = android.view.View.GONE
                                    textNoPlaylist2.visibility = android.view.View.GONE
                                    noPlaylistImage.visibility = android.view.View.GONE
                                }
                            }else{
                                binding.apply {
                                    binding.playlistImage.visibility = android.view.View.GONE
                                    binding.playlistName.visibility = android.view.View.GONE
                                    binding.playlistDescription.visibility = android.view.View.GONE
                                    binding.customPlayListVideos.visibility = android.view.View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}