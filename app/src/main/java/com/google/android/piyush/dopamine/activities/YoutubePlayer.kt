package com.google.android.piyush.dopamine.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.adapters.YoutubeChannelPlaylistsAdapter
import com.google.android.piyush.dopamine.databinding.ActivityYoutubePlayerBinding
import com.google.android.piyush.dopamine.viewModels.YoutubePlayerViewModel
import com.google.android.piyush.dopamine.viewModels.YoutubePlayerViewModelFactory
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import java.text.DecimalFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Suppress("DEPRECATION")
class YoutubePlayer : AppCompatActivity() {

    private lateinit var binding: ActivityYoutubePlayerBinding
    private lateinit var youtubeRepositoryImpl: YoutubeRepositoryImpl
    private lateinit var youtubePlayerViewModel: YoutubePlayerViewModel
    private lateinit var youtubePlayerViewModelFactory: YoutubePlayerViewModelFactory
    private lateinit var databaseViewModel: DatabaseViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubePlayerBinding.inflate(layoutInflater)
        youtubeRepositoryImpl = YoutubeRepositoryImpl()
        youtubePlayerViewModelFactory = YoutubePlayerViewModelFactory(youtubeRepositoryImpl)
        databaseViewModel = DatabaseViewModel(applicationContext)
        youtubePlayerViewModel = ViewModelProvider(
            this, youtubePlayerViewModelFactory
        )[YoutubePlayerViewModel::class.java]

        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseViewModel.isFavouriteVideo(
            intent?.getStringExtra("videoId").toString()
        )
        databaseViewModel.isFavourite.observe(this){
            binding.addToPlayList.isChecked = it == intent.getStringExtra("videoId").toString()
        }

        binding.YtPlayer.enableBackgroundPlayback(true)
        binding.YtPlayer.enableAutomaticInitialization = false
        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .rel(1)
            .controls(1)
            .fullscreen(1)
            .build()

        binding.YtPlayer.initialize(youTubePlayerListener = object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                youTubePlayer.apply {
                    loadVideo(
                        intent?.getStringExtra("videoId")!!,
                        0F
                    )
                }
            } },true,iFramePlayerOptions)

        binding.YtPlayer.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                binding.YtPlayer.visibility = View.GONE
                binding.addToPlayList.visibility = View.GONE
                binding.frameLayout.addView(fullscreenView)
            }

            override fun onExitFullscreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                binding.frameLayout.removeAllViews()
                binding.YtPlayer.visibility = View.VISIBLE
            }
        })

        binding.enterInPip.setOnClickListener {
            val supportsPIP =
                packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            if (supportsPIP) enterPictureInPictureMode()
        }

        youtubePlayerViewModel.getVideoDetails(
            intent?.getStringExtra("videoId").toString()
        )

        youtubePlayerViewModel.videoDetails.observe(this){ videoDetails ->
            when(videoDetails){
                is YoutubeResource.Loading -> {
                    Log.d(TAG, "YoutubePlayer || VideoDetails : True")
                }
                is YoutubeResource.Success -> {
                    val video = videoDetails.data
                    val videoLinkData = "https://YouTube.com/watch?v=${intent.getStringExtra("videoId")}"
                    val textLikedData = counter(video.items?.get(0)?.statistics?.likeCount!!.toInt())
                    val textViewData  = counter(video.items?.get(0)?.statistics?.viewCount!!.toInt())
                    binding.apply {
                        textTitle.text = video.items?.get(0)?.snippet?.title
                        textKind.text  = video.items?.get(0)?.kind
                        textTags.text  = video.items?.get(0)?.snippet?.tags.toString()
                        videoLink.text = videoLinkData
                        textLiked.text = textLikedData
                        textView.text  = textViewData
                        textDescription.text = video.items?.get(0)?.snippet?.description
                        copyVideoLink.setOnClickListener{
                            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("Text Copied", videoLink.text)
                            clipboardManager.setPrimaryClip(clipData)
                            Toast.makeText(applicationContext,"Copied", Toast.LENGTH_SHORT).show()
                        }
                        addToPlayList.addOnCheckedStateChangedListener { _, isFavourite ->
                            if(isFavourite == 1){
                               databaseViewModel.insertFavouriteVideos(
                                    EntityFavouritePlaylist(
                                        videoId = intent.getStringExtra("videoId").toString(),
                                        thumbnail = video.items!![0].snippet?.thumbnails?.high?.url,
                                        title = video.items!![0].snippet?.title,
                                        customName = video.items!![0].snippet?.customUrl,
                                        channelId = video.items!![0].snippet?.channelId!!
                                    )
                               )
                            }else{
                                databaseViewModel.deleteFavouriteVideo(
                                    intent.getStringExtra("videoId").toString()
                                )
                            }
                        }
                    }

                    databaseViewModel.isRecentVideo( videoId = video.items?.get(0)?.id.toString())

                    databaseViewModel.isRecent.observe(this){
                        if(it == intent.getStringExtra("videoId").toString()){
                            databaseViewModel.updateRecentVideo(
                                videoId = intent.getStringExtra("videoId").toString(),
                                time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toString()
                            )
                        }else{
                            databaseViewModel.insertRecentVideos(
                                EntityRecentVideos(
                                    id = Random.nextInt(1, 100000),
                                    videoId = video.items?.get(0)?.id,
                                    thumbnail = video.items?.get(0)?.snippet?.thumbnails?.high?.url,
                                    title = video.items?.get(0)?.snippet?.title,
                                    customName = video.items?.get(0)?.snippet?.customUrl,
                                    timing = LocalTime.now()
                                        .format(DateTimeFormatter.ofPattern("hh:mm a"))
                                        .toString(),
                                    channelId = video.items?.get(0)?.snippet?.channelId
                                )
                            )
                        }
                    }
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "YoutubePlayer: ${videoDetails.exception.message.toString()}")
                }
            }
        }

        youtubePlayerViewModel.getChannelDetails(
            intent?.getStringExtra("channelId").toString()
        )

        youtubePlayerViewModel.channelDetails.observe(this){ channelDetails ->
            when(channelDetails){
                is YoutubeResource.Loading -> {
                    Log.d(TAG, "YoutubePlayer || ChannelDetails : True")
                }
                is YoutubeResource.Success -> {
                    val channel = channelDetails.data
                    val subscriberData =  "${channel.items?.get(0)?.statistics?.subscriberCount?.let { subscriber -> counter(subscriber.toInt()) }} subscribers"
                    Glide.with(this)
                        .load(channel.items?.get(0)?.snippet?.thumbnails?.high?.url)
                        .into(binding.imageView)
                    binding.apply {
                        this.text1.text = channel.items?.get(0)?.snippet?.title
                        this.text2.text = channel.items?.get(0)?.snippet?.customUrl
                        this.text3.text = subscriberData
                        this.text4.text = channel.items?.get(0)?.snippet?.description
                    }
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "YoutubePlayer: ${channelDetails.exception.message.toString()}")
                }
            }
        }

        youtubePlayerViewModel.getChannelsPlaylist(
            intent?.getStringExtra("channelId").toString()
        )

        youtubePlayerViewModel.channelsPlaylist.observe(this){ channelsPlaylist ->
            when(channelsPlaylist){
                is YoutubeResource.Loading -> {
                    Log.d(TAG, "YoutubePlayer || ChannelsPlaylist : True")
                }
                is YoutubeResource.Success -> {
                    binding.channelsPlaylist.apply {
                        layoutManager = LinearLayoutManager(this@YoutubePlayer)
                        adapter = YoutubeChannelPlaylistsAdapter (context,channelsPlaylist.data)
                    }
                }
                is YoutubeResource.Error -> {
                    Log.d(TAG, "YoutubePlayer: ${channelsPlaylist.exception.message.toString()}")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            binding.YtPlayer.wrapContent()
            binding.addToPlayList.visibility = View.GONE
        } else {
            binding.addToPlayList.visibility = View.VISIBLE
        }
    }

    private fun counter(count : Int) : String{
        var num : Double = count.toDouble()
        val data: String
        if(num > 1000000.00){
            num /= 1000000.00
            num = DecimalFormat("#.##").format(num).toDouble()
            data = "${num}M"
        }else {
            num /= 1000
            num = DecimalFormat("#.##").format(num).toDouble()
            data = "${num}K"
        }
        return data
    }
}