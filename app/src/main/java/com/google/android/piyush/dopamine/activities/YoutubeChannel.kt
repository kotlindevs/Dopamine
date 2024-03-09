package com.google.android.piyush.dopamine.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.YoutubeChannelPlaylistsAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubeChannelBinding
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubeChannelViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource

class YoutubeChannel : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubeChannelBinding
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var youtubeChannelViewModel: YoutubeChannelViewModel
    private lateinit var youtubeChannelViewModelFactory: YoutubeChannelViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubeChannelBinding.inflate(layoutInflater)
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        youtubeChannelViewModelFactory = YoutubeChannelViewModelFactory(youtubeRepositoryImpl)
        youtubeChannelViewModel = ViewModelProvider(this, youtubeChannelViewModelFactory)[YoutubeChannelViewModel::class.java]
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        youtubeChannelViewModel.getChannelDetails(
            intent.getStringExtra("channelId").toString()
        )

        youtubeChannelViewModel.channelDetails.observe(this){ channelDetails ->
            when(channelDetails){
                is YoutubeResource.Loading -> {
                    Log.d(TAG, "Loading: True")
                }
                is YoutubeResource.Success -> {
                    Glide.with(this)
                        .load(channelDetails.data.items?.get(0)?.snippet?.thumbnails?.high?.url)
                        .into(binding.imageView)

                    val text = "${
                        channelDetails.data.items?.get(0)?.statistics?.subscriberCount?.let { it1 ->
                            counter(
                                it1.toInt())
                        }} subscribers"

                    binding.apply {
                        this.text1.text = channelDetails.data.items?.get(0)?.snippet?.title
                        this.text2.text = channelDetails.data.items?.get(0)?.snippet?.customUrl
                        this.text3.text = text
                        this.text4.text = channelDetails.data.items?.get(0)?.snippet?.description
                    }
                    //Log.d(TAG, "Success: ${channelDetails.data}")
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "Error: ${channelDetails.exception.message.toString()}")
                    MaterialAlertDialogBuilder(applicationContext)
                        .apply {
                            this.setTitle("Error")
                            this.setMessage(channelDetails.exception.message.toString())
                            this.setIcon(R.drawable.ic_dialog_error)
                            this.setCancelable(false)
                            this.setNegativeButton("OK") { dialog, _ ->
                                dialog?.dismiss()
                            }.create().show()
                        }
                }
            }
        }

        youtubeChannelViewModel.getChannelsPlaylist(
            intent.getStringExtra("channelId").toString()
        )

        youtubeChannelViewModel.channelsPlaylists.observe(this) { channelsPlaylists ->
            when(channelsPlaylists){
                is YoutubeResource.Loading -> {
                    Log.d(TAG, "Loading: True")
                }
                is YoutubeResource.Success -> {
                    binding.recyclerview.apply {
                        this.setHasFixedSize(true)
                        this.layoutManager = LinearLayoutManager(this@YoutubeChannel)
                        adapter = YoutubeChannelPlaylistsAdapter(
                            applicationContext, channelsPlaylists.data
                        )

                    }
                    //Log.d(TAG, "Success: ${channelsPlaylists.data.items}")
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "Error: ${channelsPlaylists.exception.message.toString()}")
                }
            }
        }


    }

    private fun counter(count : Int) : String{
        var num : Double = count.toDouble()
        var data = ""
        if(num > 1000000.00){
            num /= 1000000.00
            data = "${num}M"
        }else {
            num /= 1000
            data = "${num}K"
        }
        return data
    }
}