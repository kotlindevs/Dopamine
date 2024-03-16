package com.google.android.piyush.dopamine.beta

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dcastalia.localappupdate.DownloadApk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.beta.youtubedl.DownloadVideo
import com.google.android.piyush.dopamine.beta.youtubedl.StreamVideo
import com.google.android.piyush.dopamine.databinding.ActivityExperimentsModeBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.utilities.DopamineVersionViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlin.system.exitProcess


class ExperimentsMode : AppCompatActivity() {
    private lateinit var binding: ActivityExperimentsModeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dopamineVersionViewModel: DopamineVersionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperimentsModeBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences("DopamineApp", MODE_PRIVATE)
        dopamineVersionViewModel = DopamineVersionViewModel()
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.useExpSearch.isChecked = sharedPreferences.getBoolean("ExperimentalSearch", false)

        Glide.with(this).load(Utilities.VIDEO_WATCH_FROM_LINK).into(binding.image)
        Glide.with(this).load(Utilities.VIDEO_DOWNLOAD_FROM_LINK).into(binding.image11)

        binding.playVideoFromLink.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StreamVideo::class.java
                )
            )
        }

        binding.downloadVideoFromLink.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DownloadVideo::class.java
                )
            )
        }

        binding.useExpSearch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked.equals(true)){
                sharedPreferences.edit().putBoolean("ExperimentalSearch", true).apply()
            }else{
                sharedPreferences.edit().putBoolean("ExperimentalSearch", false).apply()
            }
        }

        binding.checkForUpdate.setOnClickListener {
            dopamineVersionViewModel.update.observe(this) { update ->
                when (update) {
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        if(update.data.versionName == Utilities.PROJECT_VERSION) {
                            MaterialAlertDialogBuilder(this).apply {
                                this.setTitle("Wow ! 🫡")
                                this.setMessage("You are already using the latest version of Dopamine . Happy Coding :) ")
                                this.setIcon(R.drawable.ic_alert)
                                this.setCancelable(true)
                                this.setPositiveButton("Okay") { dialog, _ ->
                                    dialog?.dismiss()
                                }
                            }.create().show()
                        }else {
                            if(this.getSharedPreferences("DopamineApp", MODE_PRIVATE)
                                    .getBoolean("ExperimentalUpdate", false).equals(true)) {
                            }else{
                                MaterialAlertDialogBuilder(this).apply {
                                    this.setTitle("NOTICES")
                                    this.setMessage("This feature is currently available in limited users if this feature not working in your device don't panic , we will fix it soon ! ")
                                    this.setIcon(R.drawable.ic_info)
                                    this.setCancelable(true)
                                    this.setPositiveButton("Don't show again") { _, _ ->
                                        context.getSharedPreferences(
                                            "DopamineApp",
                                            MODE_PRIVATE
                                        )
                                            .edit().putBoolean("ExperimentalUpdate", true).apply()
                                    }
                                }.create().show()
                            }
                            val downloadApk = DownloadApk(this@ExperimentsMode)
                            downloadApk.startDownloadingApk(update.data.url.toString())
                        }
                        Log.d(TAG, update.data.toString())
                    }
                    is YoutubeResource.Error -> {
                        Log.d(TAG, update.exception.message.toString())
                    }
                }
            }
        }
    }
}