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

        developersViewModel.devModel.observeForever { it ->
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

                        val meet = it.data[2]
                        binding.devMeetEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(meet.userImage)
                            .into(binding.devMeetImage)
                        binding.devMeetName.text = meet.userName
                        binding.devMeetDesignation.text = meet.userDesignation
                        binding.devMeet.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                    ).putExtra("userId",meet.userId)
                            )
                        }

                        val ajith = it.data[3]

                        binding.devAjithEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(ajith.userImage)
                            .into(binding.devAjithImage)

                        binding.devAjithName.text = ajith.userName
                        binding.devAjithDesignation.text = ajith.userDesignation
                        binding.devAjith.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                    ).putExtra("userId",ajith.userId)
                            )
                        }

                        val bhajan = it.data[4]

                        binding.devBhajanEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(bhajan.userImage)
                            .into(binding.devBhajanImage)

                        binding.devBhajanName.text = bhajan.userName
                        binding.devBhajanDesignation.text = bhajan.userDesignation
                        binding.devBhajan.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                    ).putExtra("userId",bhajan.userId)
                            )
                        }

                        val akshar = it.data[5]

                        binding.devAksharEffect.apply {
                            visibility = View.INVISIBLE
                            stopShimmer()
                            hideShimmer()
                        }

                        Glide.with(context)
                            .load(akshar.userImage)
                            .into(binding.devAksharImage)

                        binding.devAksharName.text = akshar.userName
                        binding.devAksharDesignation.text = akshar.userDesignation
                        binding.devAkshar.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    AboutDeveloper::class.java
                                    ).putExtra("userId",akshar.userId)
                            )
                        }

                        val dopamine = it.data[6]

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

                        devMeetEffect.visibility = View.VISIBLE
                        devMeetEffect.startShimmer()

                        devAjithEffect.visibility = View.VISIBLE
                        devAjithEffect.startShimmer()

                        devBhajanEffect.visibility = View.VISIBLE
                        devBhajanEffect.startShimmer()

                        devAksharEffect.visibility = View.VISIBLE
                        devAksharEffect.startShimmer()

                        aboutDopamineEffect.visibility = View.VISIBLE
                        aboutDopamineEffect.startShimmer()
                    }
                }
            }
        }
    }
}