package com.google.android.piyush.dopamine.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.databinding.ActivityAboutUsBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.youtube.utilities.DevelopersViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AboutUs(context: Context) : MaterialAlertDialogBuilder(context) {

    private var binding: ActivityAboutUsBinding = ActivityAboutUsBinding.inflate(LayoutInflater.from(context))
    private var developersViewModel: DevelopersViewModel
    init {
        setView(binding.root)
        developersViewModel = DevelopersViewModel()

        developersViewModel.devModel.observeForever {
            when(it){
                is YoutubeResource.Loading -> {}
                is YoutubeResource.Success -> {
                    if(NetworkUtilities.isNetworkAvailable(context)){
                        val developer = it.data
                        binding.devPiyushEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(developer.userImage)
                            .into(binding.devPiyushImage)
                        binding.devPiyushName.text = developer.userName
                        binding.devPiyushDesignation.text = developer.userDesignation
                        binding.devPiyush.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                ).putExtra("userId",developer.userId)
                            )
                        }
                    }
                }
                is YoutubeResource.Error -> {
                    binding.apply {
                        devPiyushEffect.visibility = View.VISIBLE
                        devPiyushEffect.startShimmer()

                        aboutDopamineEffect.visibility = View.VISIBLE
                        aboutDopamineEffect.startShimmer()
                    }
                }
            }
        }
    }
}