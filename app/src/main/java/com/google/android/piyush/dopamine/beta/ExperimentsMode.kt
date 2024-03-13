package com.google.android.piyush.dopamine.beta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.beta.youtubedl.DownloadVideo
import com.google.android.piyush.dopamine.beta.youtubedl.StreamVideo
import com.google.android.piyush.dopamine.databinding.ActivityExperimentsModeBinding
import com.google.android.piyush.dopamine.utilities.Utilities

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

        binding.feedbackBtn.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/rajatt.dev/")
                )
            )
        }
    }
}