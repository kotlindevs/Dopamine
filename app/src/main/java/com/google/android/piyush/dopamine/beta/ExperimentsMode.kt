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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperimentsModeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (this.getSharedPreferences("DopamineApp", MODE_PRIVATE)
                .getBoolean("ExperimentalMode", false).equals(true)
        ) {
            Log.d(TAG, "ExperimentalMode: true")
        } else {
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
                        .edit().putBoolean("ExperimentalMode", true).apply()
                }
            }.create().show()
        }


        binding.expUserPhotoColor.isEnabled = false


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
    }
}