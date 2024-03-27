package com.google.android.piyush.dopamine.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAboutDopamineBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.utilities.Developer
import com.google.android.piyush.youtube.utilities.Photos
import kotlin.random.Random

class AboutDopamine : AppCompatActivity() {
    private lateinit var binding: ActivityAboutDopamineBinding

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
        binding.dopamineImageView.setLayoutManager(CarouselLayoutManager(FullScreenCarouselStrategy()))
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(binding.dopamineImageView)

        val imageList = ArrayList<Int>()
        for(i in 1..1000){
            imageList.add(R.drawable.ic_launcher_foreground)
        }
        val adapter = AboutDopamineRecyclerViewAdapter(this, imageList)
        binding.dopamineImageView.setAdapter(adapter)
        binding.appVersion.text = Utilities.PRE_RELEASE_VERSION
        binding.appRelease.text = Utilities.PRE_RELEASE
        binding.appReleaseDate.text = Utilities.RELEASE_DATE
        binding.github.setOnClickListener{
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                ).apply {
                    data = Uri.parse(Utilities.GITHUB)
                }
            )
        }

        binding.email.setOnClickListener{
            startActivity(
                Intent(
                    Intent.ACTION_SENDTO,
                ).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(Utilities.EMAIL, Utilities.EMAIL1))
                    putExtra(Intent.EXTRA_SUBJECT, Utilities.PROJECT_VERSION)
                }
            )
        }

        binding.shareApp.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Utilities.GITHUB)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}

class AboutDopamineRecyclerViewAdapter(val context: Context, private val imageList : ArrayList<Int>)
    : RecyclerView.Adapter<AboutDopamineRecyclerViewAdapter.RCHolder>() {

    class RCHolder(v: View) : RecyclerView.ViewHolder(v){
        val image: ImageView = v.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCHolder {
        return RCHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.app_dopamine_image,parent,false)
        )
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: RCHolder, position: Int) {
        Glide.with(context).load(imageList[position]).into(holder.image)
        holder.image.background = ColorDrawable(Color.rgb(Random.nextInt(256),Random.nextInt(256),Random.nextInt(256)))
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