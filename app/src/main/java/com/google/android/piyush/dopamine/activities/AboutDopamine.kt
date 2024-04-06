package com.google.android.piyush.dopamine.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAboutDopamineBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.utilities.AboutAppViewModel
import com.google.android.piyush.youtube.utilities.Photos
import com.google.android.piyush.youtube.utilities.YoutubeResource
import kotlin.random.Random

class AboutDopamine : AppCompatActivity() {
    private lateinit var binding: ActivityAboutDopamineBinding
    private val viewModel by viewModels<AboutAppViewModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutDopamineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(NetworkUtilities.isNetworkAvailable(this)) {
            viewModel.aboutUs.observe(this) { aboutApp ->
                if (aboutApp is YoutubeResource.Success) {
                    val aboutData = aboutApp.data
                    binding.apply {
                        dopamineTeamImage.visibility = View.GONE
                        dopamineTeamText1.visibility = View.GONE
                        dopamineTeamText2.visibility = View.GONE
                        topAppBar.visibility = View.VISIBLE
                        card.visibility = View.VISIBLE
                        version.visibility = View.VISIBLE
                        release.visibility = View.VISIBLE
                        compileTime.visibility = View.VISIBLE
                        androidVersion.visibility = View.VISIBLE
                        updateStatus.visibility = View.VISIBLE
                        buildCompileTime.visibility = View.VISIBLE
                        buildVersion.visibility = View.VISIBLE
                        buildRelease.visibility = View.VISIBLE
                        build.visibility = View.VISIBLE
                        androidVersionDivider.visibility = View.VISIBLE
                        releaseDivider.visibility = View.VISIBLE
                        versionDivider.visibility = View.VISIBLE
                        currentAndroidVersion.visibility = View.VISIBLE
                        updateStatus.visibility = View.VISIBLE
                        systemUpdateStatus.visibility = View.VISIBLE
                        company.visibility = View.VISIBLE
                        developer.visibility = View.VISIBLE
                        title.visibility = View.VISIBLE
                        description.visibility = View.VISIBLE
                        os.visibility = View.VISIBLE
                        title.text = aboutData.appName
                        description.text = aboutData.description
                        version.text = aboutData.versionName
                        release.text = aboutData.releaseType
                        compileTime.text = aboutData.releaseDate.plus(" ").plus(aboutData.releaseTime)
                        androidVersion.text = Build.VERSION.RELEASE
                        updateStatus.text = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) "Up to date" else "Under development"
                    }
                }
            }
        }else{
            binding.dopamineTeamImage.visibility = View.VISIBLE
            binding.dopamineTeamText1.visibility = View.VISIBLE
            binding.dopamineTeamText2.visibility = View.VISIBLE
        }

        binding.topAppBar.setNavigationOnClickListener {
            finishActivity(0)
            startActivity(
                Intent(this, DopamineYtSettings::class.java)
            )
        }
    }
}

class AboutDeveloperRecyclerViewAdapter(val context: Context, private val devImage : List<Photos>)
    : RecyclerView.Adapter<AboutDeveloperRecyclerViewAdapter.DevHolder>() {

    class DevHolder(v: View) : RecyclerView.ViewHolder(v){
        val image: ImageView = v.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevHolder {
        return DevHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.app_dopamine_image,parent,false)
        )
    }

    override fun getItemCount(): Int = devImage.size

    override fun onBindViewHolder(holder: DevHolder, position: Int) {
        Glide.with(context).load(devImage[position].photo).into(holder.image)
    }
}