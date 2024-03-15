package com.google.android.piyush.dopamine.beta

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.github.tutorialsandroid.appxupdater.AppUpdater
import com.github.tutorialsandroid.appxupdater.enums.Display
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.beta.youtubedl.DownloadVideo
import com.google.android.piyush.dopamine.beta.youtubedl.StreamVideo
import com.google.android.piyush.dopamine.databinding.ActivityExperimentsModeBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.viewModels.SharedViewModel

class ExperimentsMode : AppCompatActivity() {
    private lateinit var binding: ActivityExperimentsModeBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperimentsModeBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences("DopamineApp", MODE_PRIVATE)
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
            val appUpdater = AppUpdater(this)
                .setDisplay(Display.SNACKBAR)
                .setUpdateJSON("https://raw.githubusercontent.com/kotlindevs/Dopamine/Stable/version.json")
                .showEvery(5)
                .showAppUpdated(true)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available dopamine!")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update now?")
                .setButtonDismiss("Maybe later")
                .setButtonDoNotShowAgain("Huh, not interested")
                .setCancelable(false)
            appUpdater.start()
        }
    }
}