package com.google.android.piyush.dopamine.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAboutUsBinding
import com.google.android.piyush.youtube.utilities.DevelopersViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AboutUs(context: Context) : MaterialAlertDialogBuilder(context) {

    private var binding: ActivityAboutUsBinding = ActivityAboutUsBinding.inflate(LayoutInflater.from(context))
    private var developersViewModel: DevelopersViewModel
    init {
        setView(binding.root)
        developersViewModel = DevelopersViewModel()

        developersViewModel.devModel.observeForever {
            if(it is YoutubeResource.Success){

                binding.devPiyushEffect.visibility = View.INVISIBLE
                binding.devPiyushEffect.hideShimmer()

                Glide.with(context)
                    .load(it.data[0].userImage)
                    .into(binding.devPiyushImage)
                binding.devPiyushName.text = it.data[0].userName
                binding.devPiyushDesignation.text = it.data[0].userDesignation

                binding.devRajatEffect.visibility = View.INVISIBLE
                binding.devRajatEffect.hideShimmer()

                Glide.with(context)
                    .load(it.data[1].userImage)
                    .into(binding.devRajatImage)
                binding.devRajatName.text = it.data[1].userName
                binding.devRajatDesignation.text = it.data[1].userDesignation

                binding.devMeetEffect.visibility = View.INVISIBLE
                binding.devMeetEffect.hideShimmer()

                Glide.with(context)
                    .load(it.data[2].userImage)
                    .into(binding.devMeetImage)
                binding.devMeetName.text = it.data[2].userName
                binding.devMeetDesignation.text = it.data[2].userDesignation

                binding.devAjithEffect.visibility = View.INVISIBLE
                binding.devAjithEffect.hideShimmer()

                Glide.with(context)
                    .load(it.data[3].userImage)
                    .into(binding.devAjithImage)
                binding.devAjithName.text = it.data[3].userName
                binding.devAjithDesignation.text = it.data[3].userDesignation

                binding.devBhajanEffect.visibility = View.INVISIBLE
                binding.devBhajanEffect.hideShimmer()

                Glide.with(context)
                    .load(it.data[4].userImage)
                    .into(binding.devBhajanImage)
                binding.devBhajanName.text = it.data[4].userName
                binding.devBhajanDesignation.text = it.data[4].userDesignation

                binding.devAksharEffect.visibility = View.INVISIBLE
                binding.devAksharEffect.hideShimmer()

                Glide.with(context)
                    .load(it.data[5].userImage)
                    .into(binding.devAksharImage)
                binding.devAksharName.text = it.data[5].userName
                binding.devAksharDesignation.text = it.data[5].userDesignation

                binding.aboutDopamineEffect.visibility = View.INVISIBLE
                binding.aboutDopamineEffect.hideShimmer()

                Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .into(binding.aboutDopamineImage)
                binding.aboutDopamineName.text = it.data[6].userName
                binding.aboutDopamineDescription.text = it.data[6].userDesignation
            }
        }
    }
}