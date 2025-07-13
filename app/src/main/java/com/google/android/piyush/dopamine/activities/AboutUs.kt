package com.google.android.piyush.dopamine.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R
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
                        val piyush = it.data[0]
                        binding.devPiyushEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(piyush.userImage)
                            .into(binding.devPiyushImage)
                        binding.devPiyushName.text = piyush.userName
                        binding.devPiyushDesignation.text = piyush.userDesignation
                        binding.devPiyush.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                ).putExtra("userId",piyush.userId)
                            )
                        }


                        val rajat = it.data[1]

                        binding.devRajatEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(rajat.userImage)
                            .into(binding.devRajatImage)
                        binding.devRajatName.text = rajat.userName
                        binding.devRajatDesignation.text = rajat.userDesignation
                        binding.devRajat.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                    ).putExtra("userId",rajat.userId)
                            )
                        }

                        val dopamine = it.data[2]

                        binding.aboutDopamineEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(R.mipmap.ic_launcher)
                            .into(binding.aboutDopamineImage)

                        binding.aboutDopamineName.text = dopamine.userName
                        binding.aboutDopamineDescription.text = dopamine.userDesignation
                        binding.aboutDopamine.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDopamine::class.java
                                )
                            )
                        }
                    }
                }
                is YoutubeResource.Error -> {
                    binding.apply {
                        devPiyushEffect.visibility = View.VISIBLE
                        devPiyushEffect.startShimmer()

                        devRajatEffect.visibility = View.VISIBLE
                        devRajatEffect.startShimmer()

                        aboutDopamineEffect.visibility = View.VISIBLE
                        aboutDopamineEffect.startShimmer()
                    }
                }
            }
        }
    }
}