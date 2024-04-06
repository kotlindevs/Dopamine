package com.google.android.piyush.dopamine.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.CustomPlayListVAdapter
import com.google.android.piyush.dopamine.adapters.ManagerPlaylistsBottomSheet
import com.google.android.piyush.dopamine.adapters.PlaylistsManagerAdapter
import com.google.android.piyush.dopamine.databinding.ActivityPlaylistsManagerBinding
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
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

        val tryIt = dopamineSharedPreferences(this).getBoolean("tryIt", false)
        if(tryIt.equals(false)) {
            MaterialAlertDialogBuilder(this).apply {
                setTitle("Under Development")
                setIcon(R.drawable.bug_report)
                setMessage("Currently we are working on this feature, and these is not working properly if you are faced with any issue. So, please report it to us 😊")
                setPositiveButton("Try it !") { dialog, _ ->
                    dopamineSharedPreferences(context).edit {
                        putBoolean("tryIt", true)
                    }
                    dialog.dismiss()
                }
                setNegativeButton("Send Feedback") { dialog, _ ->
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://forms.visme.co/formsPlayer/x4vpdzr0-dopamine-feedback"
                            )
                        )
                    )
                    dialog.dismiss()
                }
                setCancelable(true)
            }.create().show()
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