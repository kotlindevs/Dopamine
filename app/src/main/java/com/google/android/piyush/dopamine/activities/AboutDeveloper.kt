package com.google.android.piyush.dopamine.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAboutDeveloperBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.youtube.utilities.DevelopersViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AboutDeveloper : AppCompatActivity() {
    private lateinit var binding: ActivityAboutDeveloperBinding
    private lateinit var developersViewModel: DevelopersViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutDeveloperBinding.inflate(layoutInflater)
        developersViewModel = DevelopersViewModel()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if(!intent.getStringExtra("userId").isNullOrEmpty()){
            developersViewModel.devModel.observe(this){
                when(it){
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        if(it.data.isNotEmpty()){
                            for(dev in it.data){
                                if(dev.userId == intent.getStringExtra("userId")){
                                    if(dev.userBanner.isNullOrEmpty()){
                                        binding.developerBannerEffect.apply {
                                            visibility = View.VISIBLE
                                            startShimmer()
                                        }
                                    }else{
                                        binding.developerBannerEffect.apply {
                                            visibility = View.GONE
                                            stopShimmer()
                                        }
                                        Glide.with(this).load(dev.userBanner).into(binding.developerBanner)
                                    }

                                    if(dev.userImage.isNullOrEmpty()){
                                        binding.developerImageEffect.apply {
                                            visibility = View.VISIBLE
                                            startShimmer()
                                        }
                                    }else {
                                        binding.developerImageEffect.apply {
                                            visibility = View.GONE
                                            stopShimmer()
                                        }
                                        Glide.with(this).load(dev.userImage).into(binding.developerImage)
                                    }
                                    binding.apply {
                                        developerNameEffect.apply {
                                            visibility = View.INVISIBLE
                                            stopShimmer()
                                        }
                                        developerDesignationEffect.apply {
                                            visibility = View.INVISIBLE
                                            stopShimmer()
                                        }
                                        developerLocationEffect.apply {
                                            visibility = View.INVISIBLE
                                            stopShimmer()
                                        }
                                        developerName.text = dev.userName
                                        developerDesignation.text = dev.userDesignation
                                        developerLocation.text = dev.userLocation
                                        if(dev.userAbout.isNullOrEmpty()){
                                            developerDescription.text = "No Description"
                                            developerDescriptionEffect.apply {
                                                visibility = View.GONE
                                                stopShimmer()
                                            }
                                        }else{
                                            developerDescriptionEffect.apply {
                                                visibility = View.GONE
                                                stopShimmer()
                                            }
                                            developerDescription.text = dev.userAbout
                                        }

                                        if(dev.userEmail.isNullOrEmpty()){
                                            binding.sendMail.visibility = View.GONE
                                        }else{
                                            binding.sendMail.visibility = View.VISIBLE
                                            binding.sendMail.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_SENDTO,
                                                    ).apply {
                                                        data = Uri.parse("mailto:")
                                                        putExtra(Intent.EXTRA_EMAIL, arrayOf(dev.userEmail))
                                                        putExtra(Intent.EXTRA_SUBJECT, Utilities.PROJECT_VERSION)
                                                    }
                                                )
                                            }
                                        }

                                        if(!dev.userPhotos.isNullOrEmpty()){
                                            binding.photosFromUserText.visibility = View.VISIBLE
                                            binding.photosFromUser.visibility = View.VISIBLE
                                            binding.photosFromUser.layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())
                                            val snapHelper = CarouselSnapHelper()
                                            snapHelper.attachToRecyclerView(binding.photosFromUser)
                                            binding.photosFromUser.apply {
                                                adapter = AboutDeveloperRecyclerViewAdapter(
                                                    context = applicationContext,dev.userPhotos!!
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is YoutubeResource.Error -> {}
                }
            }
        }
    }
}