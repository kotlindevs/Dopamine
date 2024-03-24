package com.google.android.piyush.dopamine.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
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
                                       // Glide.with(this).load(Utilities.DEFAULT_BANNER).into(binding.developerBanner)
                                        binding.developerBannerEffect.apply {
                                            visibility = android.view.View.VISIBLE
                                            startShimmer()
                                        }
                                    }else{
                                        binding.developerBannerEffect.apply {
                                            visibility = android.view.View.GONE
                                            stopShimmer()
                                        }
                                        Glide.with(this).load(dev.userBanner).into(binding.developerBanner)
                                    }

                                    if(dev.userImage.isNullOrEmpty()){
                                        //Glide.with(this).load(Utilities.DEFAULT_LOGO).into(binding.developerImage)
                                        binding.developerImageEffect.apply {
                                            visibility = android.view.View.VISIBLE
                                            startShimmer()
                                        }
                                    }else {
                                        binding.developerImageEffect.apply {
                                            visibility = android.view.View.GONE
                                            stopShimmer()
                                        }
                                        Glide.with(this).load(dev.userImage).into(binding.developerImage)
                                    }
                                    binding.apply {
                                        developerNameEffect.apply {
                                            visibility = android.view.View.INVISIBLE
                                            stopShimmer()
                                        }
                                        developerDesignationEffect.apply {
                                            visibility = android.view.View.INVISIBLE
                                            stopShimmer()
                                        }
                                        developerLocationEffect.apply {
                                            visibility = android.view.View.INVISIBLE
                                            stopShimmer()
                                        }
                                        developerName.text = dev.userName
                                        developerDesignation.text = dev.userDesignation
                                        developerLocation.text = dev.userLocation
                                        if(dev.userAbout.isNullOrEmpty()){
                                            developerDescriptionEffect.apply {
                                                visibility = android.view.View.VISIBLE
                                                startShimmer()
                                            }
                                        }else{
                                            developerDescriptionEffect.apply {
                                                visibility = android.view.View.GONE
                                                stopShimmer()
                                            }
                                            developerDescription.text = dev.userAbout
                                        }

                                        if(dev.userWebsite.isNullOrEmpty()){
                                            binding.website.isVisible = false
                                        }else{
                                            binding.website.isVisible = true
                                            binding.website.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW
                                                    ).apply {
                                                        data = android.net.Uri.parse(dev.userWebsite)
                                                    }
                                                )
                                            }
                                        }

                                        if(dev.userWhatsapp.isNullOrEmpty()){
                                            binding.whatsapp.isVisible = false
                                        }else{
                                            binding.whatsapp.isVisible = true
                                            binding.whatsapp.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW
                                                    ).apply {
                                                        data =
                                                            android.net.Uri.parse("https://api.whatsapp.com/send?phone=${dev.userWhatsapp}&text=Hello "+dev.userName)
                                                    }
                                                )
                                            }
                                        }

                                        if(dev.userFacebook.isNullOrEmpty()){
                                            binding.facebook.isVisible = false
                                        }else{
                                            binding.facebook.isVisible = true
                                            binding.facebook.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW
                                                    ).apply {
                                                        data = android.net.Uri.parse(dev.userFacebook)
                                                    }
                                                )
                                            }
                                        }

                                        if(dev.userInstagram.isNullOrEmpty()){
                                            binding.instagram.isVisible = false
                                        }else{
                                            binding.instagram.isVisible = true

                                            binding.instagram.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW
                                                    ).apply {
                                                        data = android.net.Uri.parse(dev.userInstagram)
                                                    }
                                                )
                                            }
                                        }

                                        if(dev.userEmail.isNullOrEmpty()){
                                            binding.sendMail.isVisible = false
                                        }else{
                                            binding.sendMail.isVisible = true

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

                                        if(dev.userWhatsapp.isNullOrEmpty() &&
                                            dev.userFacebook.isNullOrEmpty() &&
                                            dev.userInstagram.isNullOrEmpty() &&
                                            dev.userEmail.isNullOrEmpty() &&
                                            dev.userWebsite.isNullOrEmpty()){
                                            binding.noContactAvailable.isVisible = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is YoutubeResource.Error -> {

                    }
                }
            }
        }
    }
}